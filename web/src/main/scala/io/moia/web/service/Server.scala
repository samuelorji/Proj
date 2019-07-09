package io.moia.web
package service

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout

import io.moia.core.config.UrlShortenerConfig
import io.moia.service.shortner.UrlShortner

object Server extends App {

  implicit val system       = ActorSystem("UrlShortener")
  implicit val materializer = ActorMaterializer()

  UrlShortner.initializeRedis

  val bdgFut = Http().bindAndHandle(
    new WebServiceT {
      override val actorRefFactory: ActorSystem = system
      override implicit val timeout: Timeout = UrlShortenerConfig.httpRequestsTimeout
    }.routes,
    UrlShortenerConfig.webHost, UrlShortenerConfig.webPort)

  StdIn.readLine()
  bdgFut
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate()) // and shutdown when done

}
