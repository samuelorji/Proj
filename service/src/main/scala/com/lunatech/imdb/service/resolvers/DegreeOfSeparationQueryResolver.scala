package com.lunatech.imdb.service.resolvers

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingAdapter
import com.lunatech.imdb.service.QueryHelpers.DegreeOfSeparationHelperT

object DegreeOfSeparationQueryResolver{
  case class GetDegreeOfSeparationRequest(person : String)
  case class GetDegreeOfSeparationResponse(num : Option[Int] , error : Option[String])
  def props = Props[DegreeOfSeparationQueryResolver]
}
private[resolvers] class DegreeOfSeparationQueryResolver extends Actor
  with ActorLogging
  with DegreeOfSeparationHelperT {

  import DegreeOfSeparationQueryResolver._


  override def logger: LoggingAdapter = log

  override def receive: Receive = {
    case req : GetDegreeOfSeparationRequest =>
      val currentSender = sender()
      log.info(s"Processing request: [$req]")
      getDegreeOfSeparation(req,currentSender)



  }

}
