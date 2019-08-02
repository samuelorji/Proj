package com.lunatech.imdb.core.db.neo4j

import com.lunatech.imdb.core.config.ImdbConfig
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}

object Neo4jDb extends Neo4jConfig
private[neo4j]trait  Neo4jConfig {
  private val driver = GraphDatabase.driver(ImdbConfig.neo4jUrl,
    AuthTokens.basic(ImdbConfig.neo4jDbUser, ImdbConfig.neo4jDbPass))
  val session = driver.session

}
