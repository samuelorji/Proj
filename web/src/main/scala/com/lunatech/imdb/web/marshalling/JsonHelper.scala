package com.lunatech.imdb.web.marshalling

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.lunatech.imdb.service.resolvers.TypeCastQueryResolver
import com.lunatech.imdb.service.resolvers.CoincidenceQueryResolver.ShowAndTitle
import spray.json.DefaultJsonProtocol

trait JsonHelper extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val showAndTitleFormat        = jsonFormat2(ShowAndTitle)
  implicit val coincidenceResponseFormat = jsonFormat1(Coincidences.apply)
  implicit val typeCastStatusFormat      = jsonFormat1(TypecastStatus.apply)


}
