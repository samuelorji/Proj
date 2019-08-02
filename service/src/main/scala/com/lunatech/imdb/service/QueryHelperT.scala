package com.lunatech.imdb.service

import akka.actor.ActorRef
import akka.event.LoggingAdapter
import com.github.mauricio.async.db.RowData
import com.lunatech.imdb.core.db.neo4j.mapper.Neo4jMapper
import com.lunatech.imdb.core.db.postgres.mapper.ImdbMapper
import com.lunatech.imdb.service.resolvers.CoincidenceQueryResolver._
import com.lunatech.imdb.service.resolvers.DegreeOfSeparationQueryResolver.{GetDegreeOfSeparationRequest, GetDegreeOfSeparationResponse}
import com.lunatech.imdb.service.resolvers.TypeCastQueryResolver.{CheckIfTypeCastedRequest, CheckIfTypeCastedResponse}
import org.neo4j.driver.v1.exceptions.NoSuchRecordException

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


object QueryHelpers {

  trait DegreeOfSeparationHelperT{
    def logger: LoggingAdapter
    def getDegreeOfSeparation(req : GetDegreeOfSeparationRequest , sender : ActorRef) = {
      Neo4jMapper.getDegreeOfSeparation(req.person).onComplete{
        case Success(deg) => sender ! GetDegreeOfSeparationResponse(Some(deg.asInt()),None)
        case Failure(ex)  =>
          ex match {
            case _ : NoSuchRecordException => //In case No record is found
              sender ! GetDegreeOfSeparationResponse(Some(-1),None)
            case _  =>
              logger.error(s"Error received while processing [$req], error : [${ex.getMessage}]")
              sender ! GetDegreeOfSeparationResponse(None,Some(ex.getMessage))
          }

      }
    }

  }

  trait CoincidenceQueryHelperT {
    def logger: LoggingAdapter

    private def rowToShowType(row: RowData) = ShowAndTitle(
      title = Some(row("primary_title").asInstanceOf[String]),
      kind_of_show = Some(row("title_type").asInstanceOf[String]),
    )

    def getCoincidence(req: GetCoincidenceRequest, sender: ActorRef) = {
      ImdbMapper.getCoincidence(req.first, req.second).onComplete {
        case Success(res) =>
          res.rows match {
            case Some(rows) =>
              if (rows.isEmpty) {
                sender ! GetCoincidenceResponse(List(), None)
              } else {
                sender ! GetCoincidenceResponse(rows.map(rowToShowType).toList, None)
              }
            case None =>
              sender ! GetCoincidenceResponse(List(), None)
          }

        case Failure(ex) =>
          logger.error(s"Error encountered when processing [$req] : error : [$ex]")
          sender ! GetCoincidenceResponse(List(), Some(ex.getMessage))
      }
    }

  }

  trait TypeCastQueryHelperT {

    def logger: LoggingAdapter


    def getTypeCastStatus(req: CheckIfTypeCastedRequest, sender: ActorRef) = {
      ImdbMapper.getTypeCastStatus(req.person).onComplete {
        case Success(res) =>
          res.rows match {
            case Some(rows) =>
              if (rows.isEmpty) {
                sender ! CheckIfTypeCastedResponse(false, None)
              } else {
                val maps = rows.foldLeft(Map[String, ArrayBuffer[String]]()) {
                  case (map, row) =>
                    map.updated(
                      key = row("primary_title").asInstanceOf[String],
                      value = row("genres").asInstanceOf[ArrayBuffer[String]]
                    )
                }
                val sortedMaps = maps.values.flatten.foldLeft(Map[String, Int]()) {
                  case (map, genres) =>
                    val oldCount = map.getOrElse(genres, 0)
                    map.+(genres -> (oldCount + 1))
                }

                val lengthOfTotalWorks = maps.keys.size
                val maxKnownGenres = sortedMaps.maxBy(_._2)._2

                val isTypeCasted = (maxKnownGenres.toDouble / lengthOfTotalWorks.toDouble) >= 0.5

                sender ! CheckIfTypeCastedResponse(isTypeCasted, None)
              }

            case None =>

              sender ! CheckIfTypeCastedResponse(false, None)
          }

        case Failure(ex) =>
          logger.error(s"Error encountered when processing [$req] : error : [$ex]")

          sender ! CheckIfTypeCastedResponse(false, Some(ex.getMessage))
      }


    }

  }

}
