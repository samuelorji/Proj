package com.lunatech.imdb.core.db.postgres.mapper

import com.github.mauricio.async.db.QueryResult
import com.lunatech.imdb.core.db.postgres.ImdbPostgresDb

import scala.concurrent.Future

object ImdbMapper extends ImdbPostgresDb {

 def getCoincidence(first : String,second : String): Future[QueryResult] = {
   val queryString = s"SELECT DISTINCT(primary_title) ,title_type FROM titles WHERE tconst=(SELECT unnest(known_for_titles) FROM names WHERE primary_name='$first' " +
     s"INTERSECT (SELECT unnest(known_for_titles) FROM names WHERE primary_name='$second'));"
   pool.sendPreparedStatement(queryString)
 }

  def getTypeCastStatus(person : String): Future[QueryResult] = {
    val queryString =s"SELECT DISTINCT(primary_title),genres FROM titles WHERE tconst in (SELECT unnest(known_for_titles) FROM names WHERE primary_name='$person');"
    pool.sendPreparedStatement(queryString)
  }

}

