package com.lunatech.imdb.service.marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import com.lunatech.imdb.service.shortener.UrlShortener.UrlRedisData
import spray.json._

trait JsonHelper extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val urlRedisDataFormat = jsonFormat2(UrlRedisData)
}
