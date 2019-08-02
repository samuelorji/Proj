package com.lunatech.imdb.service.resolvers

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingAdapter
import com.lunatech.imdb.service.QueryHelpers.CoincidenceQueryHelperT

object CoincidenceQueryResolver {
  case class GetCoincidenceRequest(first : String, second : String)
  case class ShowAndTitle(title : Option[String], kind_of_show : Option[String])
  case class GetCoincidenceResponse(coincidences : List[ShowAndTitle],error : Option[String])

  def props = Props[CoincidenceQueryResolver]
}

private[resolvers]class CoincidenceQueryResolver extends Actor with ActorLogging with CoincidenceQueryHelperT {

  import CoincidenceQueryResolver._

  override def receive: Receive = {
    case req : GetCoincidenceRequest    =>
      log.info(s"Processing request: [$req]")
      val currentSender = sender()
      getCoincidence(req,currentSender)
  }

  override def logger: LoggingAdapter = log
}
