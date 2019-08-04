package com.lunatech.imdb
package web

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout

import service.resolvers._

import DegreeOfSeparationQueryResolver.GetDegreeOfSeparationResponse
import TypeCastQueryResolver.CheckIfTypeCastedResponse

import web.marshalling._

trait WebServiceT extends JsonHelper {

  implicit val timeout: Timeout

  implicit val actorSystem: ActorSystem

  private lazy val coincidenceQueryResolver  = createCoincidenceQueryResolver
  def createCoincidenceQueryResolver         = actorSystem.actorOf(CoincidenceQueryResolver.props)

  private lazy val typeCastQueryResolver     = createTypecastQueryResolver
  def createTypecastQueryResolver            = actorSystem.actorOf(TypeCastQueryResolver.props)

  private lazy val degreeOfSeparationResolver = createDegreeOfSeparationResolver
  def createDegreeOfSeparationResolver        = actorSystem.actorOf(DegreeOfSeparationQueryResolver.props)

  val routes = {
    pathPrefix("api") {
      path("typecasted") {
        get {
          parameter('name) { name =>
            complete((typeCastQueryResolver ? TypeCastQueryResolver.CheckIfTypeCastedRequest(name))
              .mapTo[CheckIfTypeCastedResponse].map(
              x => TypecastStatus.fromCheckIfTypeCastedResponse(x)
            ))
          }
        }
      } ~
        path("coincidence") {
          get {
            (parameter('first) & parameter('second)) { (first, second) =>
              complete((coincidenceQueryResolver ? CoincidenceQueryResolver.GetCoincidenceRequest(first, second))
                .mapTo[CoincidenceQueryResolver.GetCoincidenceResponse].map(
                x => Coincidences.fromGetCoincidenceResponse(x)
              ))
            }
          }
        } ~
        path( "dos") {
          get {
            parameter('name) { name =>
              complete((degreeOfSeparationResolver ? DegreeOfSeparationQueryResolver.GetDegreeOfSeparationRequest(name))
                .mapTo[GetDegreeOfSeparationResponse].map(
                x => DegreeOfSeparation.fromGetDegreeOfSeparationResponse(x)
              ))

            }
          }
        }
    }
  }
}
