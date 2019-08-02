package com.lunatech.imdb.core.config

import com.lunatech.imdb.core.TestServiceT

class ImdbConfigSpec extends TestServiceT {

  ImdbConfig.neo4jDbPass should be("Awesome@0")
  ImdbConfig.neo4jDbUser should be("neo4j")
  ImdbConfig.neo4jUrl should be("bolt://localhost/7474")
  ImdbConfig.postgresDbHost should be("localhost")
  ImdbConfig.postgresDbName should be ("postgres")
  ImdbConfig.postgresDbPass should be("Awesome@0")
  ImdbConfig.postgresDbPort should be (5432)
  ImdbConfig.postgresDbName should be ("postgres")
  ImdbConfig.webHost should be("127.0.0.1")
  ImdbConfig.webPort should be (8080)

}
