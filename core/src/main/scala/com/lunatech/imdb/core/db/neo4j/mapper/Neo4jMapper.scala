package com.lunatech.imdb.core.db.neo4j.mapper

import com.lunatech.imdb.core.db.neo4j.Neo4jDb
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object Neo4jMapper {
  def getDegreeOfSeparation(name : String) = {
    Future(Neo4jDb.session.run("MATCH p=shortestPath( \n(bacon:Actor {name:\"Bacon, Kevin (I)\"})-[*]-(blair:Actor {name:\"" + name + "\"}))\n " +
      "RETURN length (p) as path").single().get("path"))

  }
}
