package io.moia.service.marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import io.moia.service.shortener.UrlShortner.UrlRedisData
import spray.json.DefaultJsonProtocol

trait JsonHelper extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val urlRedisData = jsonFormat2(UrlRedisData)
}
