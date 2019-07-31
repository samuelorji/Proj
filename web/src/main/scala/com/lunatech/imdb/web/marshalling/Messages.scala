package com.lunatech.imdb.web.marshalling

import akka.http.scaladsl.model.StatusCodes
import com.lunatech.imdb.service.resolvers.TypeCastQueryResolver.CheckIfTypeCastedResponse
import com.lunatech.imdb.service.resolvers.CoincidenceQueryResolver._

object TypecastStatus{
  def fromCheckIfTypeCastedResponse(response : CheckIfTypeCastedResponse) = {
    response.error match {
      case Some(_)  => StatusCodes.InternalServerError -> None
      case None     =>
        response.status match {
          case true  => StatusCodes.OK  -> Some(TypecastStatus("TypeCasted"))
          case false => StatusCodes.OK  -> Some(TypecastStatus("Not TypeCasted"))
        }

    }
  }
}

private[web] case class TypecastStatus(typecastStatus : String)

object Coincidences{
  def fromGetCoincidenceResponse(response : GetCoincidenceResponse) = {
    response.error match {
      case Some(_) => StatusCodes.InternalServerError -> None
      case None    => StatusCodes.OK -> Some(response.coincidences)
    }
  }
}
private[web] case class Coincidences(coincidences: List[ShowAndTitle])


