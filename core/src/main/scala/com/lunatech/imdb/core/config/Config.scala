package com.lunatech.imdb.core.config

import scala.concurrent.duration._
import scala.util.Try

import com.typesafe.config.ConfigFactory

object UrlShortenerConfig {

  val config = ConfigFactory.load()

  // Web Interface
  val webHost = config.getString("moia.interface.web.host")
  val webPort = config.getInt("moia.interface.web.port")

  // redis
  val moiaRedisDbHost       = config.getString("moia.db.redis.host")
  val moiaRedisDbPort       = config.getInt("moia.db.redis.port")
  val moiaRedisDbNumWorkers = config.getInt("moia.db.redis.num-workers")

  //timeouts
  val moiaRedisTimeout      = Try(FiniteDuration(config.getInt("moia.redis.timeout"), "seconds")).toOption.get
  val httpRequestsTimeout   = Try(FiniteDuration(config.getInt("moia.web.http-requests-timeout"),"seconds")).toOption.get

  val shortenedUrlPrefix = config.getString("moia.api.url-prefix")
}
