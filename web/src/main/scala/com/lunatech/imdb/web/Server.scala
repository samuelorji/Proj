package com.lunatech.imdb
package web

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout

import core.config.ImdbConfig

object Server extends App {

  implicit val system       = ActorSystem("Imdb")
  implicit val materializer = ActorMaterializer()


  val bdgFut = Http().bindAndHandle(
    new WebServiceT {
      override implicit val actorSystem: ActorSystem = system
      override implicit val timeout: Timeout = ImdbConfig.httpRequestsTimeout
    }.routes,
    ImdbConfig.webHost, ImdbConfig.webPort)

  StdIn.readLine()
  bdgFut
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())


}
