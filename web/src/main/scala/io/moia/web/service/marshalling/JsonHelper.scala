package io.moia.web.service
package marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json.DefaultJsonProtocol

trait JsonHelper extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val urlShortnerRequestFormat  = jsonFormat1(UrlToShortenRequest.apply)
  implicit val urlShortnerResponseFormat = jsonFormat2(UrlToShortenResponse)

}
