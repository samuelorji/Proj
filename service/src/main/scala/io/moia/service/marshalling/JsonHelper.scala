package io.moia.service.marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import io.moia.service.shortener.UrlShortener.UrlRedisData
import spray.json._

trait JsonHelper extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val urlRedisDataFormat = jsonFormat2(UrlRedisData)
}
