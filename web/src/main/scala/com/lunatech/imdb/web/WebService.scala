package com.lunatech.imdb.web

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.lunatech.imdb.service.marshalling.JsonHelper
import com.lunatech.imdb.service.resolvers.QueryResolver

import scala.util.{Failure, Success}


trait WebServiceT extends JsonHelper {

  implicit val timeout: Timeout

  implicit val actorSystem: ActorSystem

  private lazy val queryResolver = createQueryResolver

  def createQueryResolver = actorSystem.actorOf(Props[QueryResolver])

  lazy val routes = {
    path("api" / "typecasted") {
      get {
        parameter('name) { name =>
          //  (queryResolver ? QueryResolver.CheckIfTypeCasted(name))
          complete("Hello")

        }
      }
    } ~
      path("api" / "coincidence") {
        get {
          (parameter('name1) & parameter('name2)) { (name1, name2) =>
            complete(StatusCodes.OK, (queryResolver ? QueryResolver.GetCoincidenceRequest(name1, name2)).mapTo[QueryResolver.GetCoincidenceResponse])
          }
        }
      }
  }
}
