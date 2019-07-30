package com.lunatech.imdb.web

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.lunatech.imdb.service.resolvers.QueryResolver
import com.lunatech.imdb.service.resolvers.QueryResolver.CheckIfTypeCastedResponse
import com.lunatech.imdb.web.marshalling.{Coincidences, JsonHelper, TypecastStatus}

import scala.concurrent.ExecutionContext.Implicits.global


trait WebServiceT extends JsonHelper {

  implicit val timeout: Timeout

  implicit val actorSystem: ActorSystem

  private lazy val queryResolver = createQueryResolver

  def createQueryResolver = actorSystem.actorOf(Props[QueryResolver])

  lazy val routes = {
    path("api" / "typecasted") {
      get {
        parameter('name) { name =>
           complete((queryResolver ? QueryResolver.CheckIfTypeCastedRequest(name))
             .mapTo[CheckIfTypeCastedResponse].map(
             x => TypecastStatus.fromCheckIfTypeCastedResponse(x)
           ))
        }
      }
    } ~
      path("api" / "coincidence") {
        get {
          (parameter('name1) & parameter('name2)) { (name1, name2) =>
            complete((queryResolver ? QueryResolver.GetCoincidenceRequest(name1, name2))
              .mapTo[QueryResolver.GetCoincidenceResponse].map(
              x => Coincidences.fromGetCoincidenceResponse(x)
            ))
          }
        }
      }
  }
}
