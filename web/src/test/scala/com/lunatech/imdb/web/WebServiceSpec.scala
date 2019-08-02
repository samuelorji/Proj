package com.lunatech.imdb.web

import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.RouteTestTimeout
import akka.testkit.TestProbe
import akka.util.Timeout
import com.lunatech.imdb.service.resolvers.TypeCastQueryResolver
import com.lunatech.imdb.web.marshalling.TypecastStatus

import scala.concurrent.duration.FiniteDuration

class WebServiceSpec extends TestServiceT
with WebServiceT {


  override implicit val timeout: Timeout = Timeout(10 seconds)

  override implicit val actorSystem: ActorSystem = system


  implicit val routeTestTimeout = RouteTestTimeout(FiniteDuration(30, "seconds"))


 "The Imdb Web Service " should {
   "Reject a Get Typecast Query without a given parameter 'name' " in {
     HttpRequest(
       method = HttpMethods.GET,
       uri    = "/api/typecasted",

     ) ~> Route.seal(routes) ~> check {
       status shouldEqual StatusCodes.NotFound
       responseAs[String] shouldEqual "Request is missing required query parameter 'name'"
     }
   }
   "Reject a Get Coincidence Query without given parameter 'first'" in {
     HttpRequest(
       method = HttpMethods.GET,
       uri    = "/api/coincidence",

     ) ~> Route.seal(routes) ~> check {
       status shouldEqual StatusCodes.NotFound
       responseAs[String] shouldEqual "Request is missing required query parameter 'first'"
     }
   }
   "Reject a Get Coincidence Query without given parameter 'second'" in {
     HttpRequest(
       method = HttpMethods.GET,
       uri    = "/api/coincidence?first=Adam%20Sandler",

     ) ~> Route.seal(routes) ~> check {
       status shouldEqual StatusCodes.NotFound
       responseAs[String] shouldEqual "Request is missing required query parameter 'second'"
     }
   }
   "Reject a Get Degree Of Separation Query without given parameter 'name'" in {
     HttpRequest(
       method = HttpMethods.GET,
       uri    = "/api/dos",

     ) ~> Route.seal(routes) ~> check {
       status shouldEqual StatusCodes.NotFound
       responseAs[String] shouldEqual "Request is missing required query parameter 'name'"
     }
   }
   "leave requests to base path unhandled" in {
     Get() ~> routes ~> check {
       handled shouldEqual false
     }
   }
   "leave requests to other paths unhandled" in {
     Get("/other") ~> routes ~> check {
       handled shouldEqual false
     }
   }

 }
}
