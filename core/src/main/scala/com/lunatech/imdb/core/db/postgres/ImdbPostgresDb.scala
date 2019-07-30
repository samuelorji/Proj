package com.lunatech.imdb.core.db.postgres

import com.github.mauricio.async.db.Configuration
import com.github.mauricio.async.db.pool.{ConnectionPool, PoolConfiguration}
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.lunatech.imdb.core.config.ImdbConfig

private[postgres] object ImdbPostgresDb {
  private val configuration = new Configuration(
    username = ImdbConfig.postgresDbUser,
    host     = ImdbConfig.postgresDbHost,
    port     = ImdbConfig.postgresDbPort,
    password = Some(ImdbConfig.postgresDbPass),
    database = Some(ImdbConfig.postgresDbName)

  )

  private val poolConfiguration = new PoolConfiguration(
    maxObjects   = ImdbConfig.postgresDbPoolMaxObjects,
    maxIdle      = ImdbConfig.postgresDbPoolMaxIdle,
    maxQueueSize = ImdbConfig.postgresDbPoolMaxQueueSize
  )

  private val factory = new PostgreSQLConnectionFactory(configuration)
  lazy val pool       = new ConnectionPool(factory,poolConfiguration)

}

 trait ImdbPostgresDb{
  lazy val pool = ImdbPostgresDb.pool
}


