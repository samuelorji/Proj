package com.lunatech.imdb.web

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.lunatech.imdb.core.config.ImdbConfig

import scala.concurrent.duration.FiniteDuration
import scala.io.StdIn

object Server extends App {

  implicit val system       = ActorSystem("Imdb")
  implicit val materializer = ActorMaterializer()


  val bdgFut = Http().bindAndHandle(
    new WebServiceT {
      override implicit val actorSystem: ActorSystem = system
      override implicit val timeout: Timeout = Timeout(FiniteDuration(10,"seconds"))
    }.routes,
    ImdbConfig.webHost, ImdbConfig.webPort)

  StdIn.readLine()
  bdgFut
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate()) // and shutdown when done




}
