package com.lunatech.imdb.web

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit._
import akka.util.Timeout

import org.scalatest.{Matchers, WordSpec}

class WebServiceSpec extends WordSpec
  with Matchers
   with WebServiceT
  with ScalatestRouteTest
   {

     override implicit val timeout: Timeout = Timeout(5 seconds)

     override implicit val actorSystem: ActorSystem = system

    implicit val routeTestTimeout = RouteTestTimeout(FiniteDuration(30, "seconds"))

    "The ElmerWebService" should {
      "Reject a shorten URl  POST request that is missing the url parameter" in {
        HttpRequest(
          method = HttpMethods.POST,
          uri    = "/api/shorten",
          entity = FormData(Map[String,String]()).toEntity(HttpCharsets.`UTF-8`)

        ) ~> Route.seal(routes) ~> check {
          status shouldEqual StatusCodes.BadRequest
          responseAs[String] shouldEqual "Request is missing required form field 'url'"
        }
      }
  }

   }
