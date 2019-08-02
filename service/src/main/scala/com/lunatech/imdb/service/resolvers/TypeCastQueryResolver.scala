package com.lunatech.imdb.service
package resolvers

import akka.actor.{ Actor, ActorLogging, Props }
import akka.event.LoggingAdapter

import QueryHelpers.TypeCastQueryHelperT

/**
  * Actor Responsible for TypeCasted Queries
  * */
object TypeCastQueryResolver {

  case class CheckIfTypeCastedRequest(person: String)

  case class CheckIfTypeCastedResponse(status: Boolean, error: Option[String])

  def props = Props[TypeCastQueryResolver]
}


private[resolvers] class TypeCastQueryResolver extends Actor
  with ActorLogging
  with TypeCastQueryHelperT {

  import TypeCastQueryResolver._

  override def receive: Receive = {
    case req: CheckIfTypeCastedRequest =>
      log.info(s"Processing request: [$req]")
      val currentSender = sender()
      getTypeCastStatus(req, currentSender)

  }
  override def logger: LoggingAdapter = log
}
