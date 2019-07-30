package com.lunatech.imdb.service.resolvers

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.github.mauricio.async.db.RowData
import com.lunatech.imdb.core.db.postgres.mapper.ImdbMapper

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object QueryResolver{
  case class CheckIfTypeCastedRequest(person : String)
  case class CheckIfTypeCastedResponse(status : Boolean,error : Option[String])
  case class GetCoincidenceRequest(first : String, second : String)
  case class ShowAndTitle(title : Option[String], kind_of_show : Option[String])
  case class GetCoincidenceResponse(coincidences : List[ShowAndTitle],error : Option[String])
}

class QueryResolver extends Actor with ActorLogging{
  import QueryResolver._

  override def receive: Receive = {
    case req : CheckIfTypeCastedRequest =>
      val currentSender = sender()
      getTypeCastStatus(req,currentSender)


    case req : GetCoincidenceRequest    =>
      val currentSender = sender()
      getCoincidence(req,currentSender)

  }

  private def rowToShowType(row: RowData) = ShowAndTitle(
    title        = Some(row("primary_title").asInstanceOf[String]),
    kind_of_show = Some(row("title_type").asInstanceOf[String]),
  )

  private def getCoincidence(req : GetCoincidenceRequest , sender : ActorRef) = {
    ImdbMapper.getCoincidence(req.first,req.second).onComplete{
      case Success(res) =>
        res.rows match {
          case Some(rows) =>
            if(rows.isEmpty){
              sender ! GetCoincidenceResponse(List(),None)
            }else{
              sender ! GetCoincidenceResponse(rows.map(rowToShowType).toList,None)
            }
          case None =>
            sender ! GetCoincidenceResponse(List(),None)
        }

      case Failure(ex)  =>
        log.error(s"Error encountered when processing [$req] : error : [$ex]")
        sender ! GetCoincidenceResponse(List(),Some(ex.getMessage))
    }
  }

  private def getTypeCastStatus(req: CheckIfTypeCastedRequest,sender : ActorRef) = {
    ImdbMapper.getTypeCastStatus(req.person).onComplete{
      case Success(res) =>
          res.rows match {
            case Some(rows) =>
              if(rows.isEmpty){
                sender ! CheckIfTypeCastedResponse(false,None)
              }else {
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

              sender ! CheckIfTypeCastedResponse(false,None)
          }

      case Failure(ex)  =>
        log.error(s"Error encountered when processing [$req] : error : [$ex]")

        sender ! CheckIfTypeCastedResponse(false,Some(ex.getMessage))
    }


  }
}
