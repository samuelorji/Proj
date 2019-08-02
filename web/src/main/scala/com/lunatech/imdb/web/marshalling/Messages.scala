package com.lunatech.imdb
package web.marshalling

import akka.http.scaladsl.model.StatusCodes

import service.resolvers.TypeCastQueryResolver.CheckIfTypeCastedResponse
import service.resolvers.CoincidenceQueryResolver._
import service.resolvers.DegreeOfSeparationQueryResolver.GetDegreeOfSeparationResponse

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


object DegreeOfSeparation{
  def fromGetDegreeOfSeparationResponse(response : GetDegreeOfSeparationResponse) = {
    response.error match {
      case Some(_) => StatusCodes.InternalServerError -> None
      case None    => StatusCodes.OK                  -> Some(DegreeOfSeparation(response.num))
    }
  }
}
private[web] case class DegreeOfSeparation(degree : Option[Int])


