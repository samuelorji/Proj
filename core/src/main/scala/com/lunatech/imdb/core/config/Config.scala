package com.lunatech.imdb.core.config

import com.lunatech.imdb.core.util.ConfigT

object ImdbConfig  extends ConfigT{

  // Web Interface
  val webHost = config.getString("imdb.interface.web.host")
  val webPort = config.getInt("imdb.interface.web.port")

  // postgres

  val postgresDbHost  = config.getString("imdb.db.postgres.host")
  val postgresDbPort  = config.getInt("imdb.db.postgres.port")
  val postgresDbUser  = config.getString("imdb.db.postgres.user")
  val postgresDbPass  = config.getString("imdb.db.postgres.pass")
  val postgresDbName  = config.getString("imdb.db.postgres.name")

  val postgresDbPoolMaxObjects   = config.getInt("imdb.db.postgres.pool.max-objects")
  val postgresDbPoolMaxIdle      = config.getInt("imdb.db.postgres.pool.max-idle")
  val postgresDbPoolMaxQueueSize = config.getInt("imdb.db.postgres.pool.max-queue-size")

  //neo4j
  val neo4jUrl        = config.getString("imdb.db.neo4j.url")
  val neo4jDbPass     = config.getString("imdb.db.neo4j.pass")
  val neo4jDbUser     = config.getString("imdb.db.neo4j.user")

  //timeouts
//  val httpRequestsTimeout   = Try(FiniteDuration(config.getInt("imdb.web.http-requests-timeout"),"seconds")).toOption.get

  //val shortenedUrlPrefix = config.getString("moia.api.url-prefix")
}
