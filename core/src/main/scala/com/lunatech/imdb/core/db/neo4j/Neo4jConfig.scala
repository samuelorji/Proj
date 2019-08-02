package com.lunatech.imdb.core
package db.neo4j

import config.ImdbConfig

import org.neo4j.driver.v1.{ AuthTokens, GraphDatabase }

object Neo4jDb extends Neo4jConfig

private[neo4j]trait  Neo4jConfig {
  private val driver = GraphDatabase.driver(ImdbConfig.neo4jUrl,
    AuthTokens.basic(ImdbConfig.neo4jDbUser, ImdbConfig.neo4jDbPass))
  val session = driver.session

}
