package io.moia.core
package db
import scala.concurrent.duration.FiniteDuration

import akka.actor.ActorSystem

import io.moia.core.config.UrlShortenerConfig

object MoiaRedisDbService extends RedisDbT{
  override val host: String = UrlShortenerConfig.moiaRedisDbHost
  override val port: Int    = UrlShortenerConfig.moiaRedisDbPort
  override val timeout: FiniteDuration = UrlShortenerConfig.moiaRedisTimeout

  def getRedisInstance(implicit system : ActorSystem) = {
    system.actorOf(props,"RedisInstance")
  }

}
