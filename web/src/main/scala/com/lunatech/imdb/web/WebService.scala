package com.lunatech.imdb.web

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson._
import akka.pattern.ask
import akka.util.Timeout
import com.lunatech.imdb.service.resolvers.DegreeOfSeparationQueryResolver.GetDegreeOfSeparationResponse
import com.lunatech.imdb.service.resolvers.{CoincidenceQueryResolver, DegreeOfSeparationQueryResolver, TypeCastQueryResolver}
import com.lunatech.imdb.service.resolvers.TypeCastQueryResolver.CheckIfTypeCastedResponse
import com.lunatech.imdb.web.marshalling.{Coincidences, DegreeOfSeparation, JsonHelper, TypecastStatus}

import scala.concurrent.ExecutionContext.Implicits.global


trait WebServiceT extends JsonHelper {

  implicit val timeout: Timeout

  implicit val actorSystem: ActorSystem

  private lazy val coincidenceQueryResolver  = createCoincidenceQueryResolver
  def createCoincidenceQueryResolver         = actorSystem.actorOf(CoincidenceQueryResolver.props)

  private lazy val typeCastQueryResolver     = createTypecastQueryResolver
  def createTypecastQueryResolver            = actorSystem.actorOf(TypeCastQueryResolver.props)

  private lazy val degreeOfSeparationResolver = createDegreeOfSeparationResolver
  def createDegreeOfSeparationResolver        = actorSystem.actorOf(DegreeOfSeparationQueryResolver.props)

  lazy val routes = {
    path("api" / "typecasted") {
      get {
        parameter('name) { name =>
          println(s"Parsed name is $name")
           complete((typeCastQueryResolver ? TypeCastQueryResolver.CheckIfTypeCastedRequest(name))
             .mapTo[CheckIfTypeCastedResponse].map(
             x => TypecastStatus.fromCheckIfTypeCastedResponse(x)
           ))
        }
    } }~
      path("api" / "coincidence") {
        get {
          (parameter('first) & parameter('second)) { (first, second) =>
            complete((coincidenceQueryResolver ? CoincidenceQueryResolver.GetCoincidenceRequest(first, second))
              .mapTo[CoincidenceQueryResolver.GetCoincidenceResponse].map(
              x => Coincidences.fromGetCoincidenceResponse(x)
            ))
          }
        }
      } ~
    path("api" / "dos"){
      get{
        parameter('name){ name =>
          complete((degreeOfSeparationResolver ? DegreeOfSeparationQueryResolver.GetDegreeOfSeparationRequest(name))
            .mapTo[GetDegreeOfSeparationResponse].map (
            x => DegreeOfSeparation.fromGetDegreeOfSeparationResponse(x)
          ))

        }
      }
    }
  }
}
