package com.lunatech.imdb.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.lunatech.imdb.core.config.ImdbConfig
import com.lunatech.imdb.core.db.neo4j.mapper.Neo4jMapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.io.StdIn
import scala.util.{Failure, Success}

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

//  val res = Neo4jMapperDb.session.run("MATCH p=shortestPath( \n(bacon:Actor {name:\"Bacon, Kevin (I)\"})-[*]-(blair:Actor {name:\"Aniston, John\"}))\n " +
//    "RETURN length (p) as path")
//
//  println(res.single().get("path"))

//  Neo4jMapper.getDegreeOfSeparation("Odunlade, Adekola").onComplete{
//    case Success(res) => println(s"result is " + res)
//    case Failure(exception) => println(s"exception is " + exception)
//  }




}
