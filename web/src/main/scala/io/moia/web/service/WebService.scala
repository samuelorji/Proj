package io.moia.web.service

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

import io.moia.service.shortener.UrlShortener
import io.moia.web.service.marshalling._
import io.moia.web.service.util.ParsingDirectives._


trait WebServiceT extends JsonHelper {

  implicit val timeout :Timeout

  implicit def actorSystem: ActorSystem

  private lazy val shortener    = createShortenerActor
  def createShortenerActor      =  actorSystem.actorOf(UrlShortener.props,"UrlShortener")

  lazy val routes = {
    path("api" / "shorten"){
      post{
        formFieldMap { fields =>
          formDataElement(UrlToShortenRequest, fields) { element =>
            logRequestResult("api:shorten", Logging.InfoLevel) {
              val shortenUrlRequest = element.asInstanceOf[UrlToShortenRequest]
              complete {
                (shortener ? UrlShortener.ShortenUrlRequest(shortenUrlRequest.url)).mapTo[UrlShortener.ShortenUrlResponse].map { x =>
                  ShortenerService.fromUrlToShorten(x) match {
                    case resp@UrlToShortenResponse(_, Some(_)) => StatusCodes.InternalServerError -> resp
                    case resp@UrlToShortenResponse(_, None)    => StatusCodes.Created             -> resp
                  }
                }
              }
            }
          }
        }
      }
    } ~
    path("api" / "redirect"){
      post{
        formFieldMap{ fields =>
          formDataElement(ShortenedUrlRequest,fields){ element =>
            val shortenedUrlRequest = element.asInstanceOf[ShortenedUrlRequest]
            onComplete(
              (shortener ? UrlShortener.RedirectUrlRequest(shortenedUrlRequest.url))
                .mapTo[UrlShortener.RedirectUrlResponse]
            ){
              case Success(res) =>
                res.exception match {
                  case Some(ex)  => complete(StatusCodes.NotFound,s"${ex.getMessage}")
                  case None      => redirect(Uri(res.actualUrl),StatusCodes.Found)
                }
              case Failure(_)  => complete(StatusCodes.InternalServerError)
            }
          }

        }
      }
    }
  }
}
