package com.lunatech.imdb.service.shortener

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.lunatech.imdb.core.config.UrlShortenerConfig
import com.lunatech.imdb.core.db.MoiaRedisDbService
import com.lunatech.imdb.core.util.InstanceManager
import com.lunatech.imdb.service.marshalling.JsonHelper
import io.moia.core.db.MoiaRedisDbService
import io.moia.core.db.RedisDbT.{AddElementRequest, AddElementResult, FetchElementRequest, FetchElementResult}
import io.moia.core.util.InstanceManager
import io.moia.service.marshalling.JsonHelper
import spray.json._

object UrlShortener extends InstanceManager[ActorRef]{


  case class ShortenUrlRequest(url : String)
  case class ShortenUrlResponse(url : String,exception: Option[Throwable] = None)

  case class UrlRedisData(shortUrl : String , actualUrl : String)

  case class RedirectUrlRequest(shortUrl : String)
  case class RedirectUrlResponse(actualUrl : String , exception : Option[Throwable] = None)

  def initializeRedis(implicit context : ActorSystem) = {
    setInstance(MoiaRedisDbService.getRedisInstance)
  }
  def props : Props = Props[UrlShortener]
}
class UrlShortener extends Actor
  with StringManipulatorT
  with JsonHelper
  with ActorLogging {

  import UrlShortener._

  private val redisDbService = createRedisDbService
  def createRedisDbService   = UrlShortener.getInstance

  private implicit val timeout : Timeout = UrlShortenerConfig.moiaRedisTimeout
  override def receive: Receive = {
    case req : ShortenUrlRequest =>
      log.info(s"processing request : $req")
      val currentSender = sender()
      val shortenedUrl  = getUniqueString

      val urlRedisDataString = UrlRedisData(
        shortUrl  = shortenedUrl,
        actualUrl = req.url
      ).toJson.prettyPrint
      (redisDbService ? AddElementRequest(shortenedUrl, urlRedisDataString)).mapTo[AddElementResult] onComplete{
        case Success(res) => res.status match {
          case true  => currentSender ! ShortenUrlResponse(shortenedUrl)
          case false => currentSender ! ShortenUrlResponse("",Some(new Exception("Unable To Add to Redis")))
        }
        case Failure(ex)  => currentSender ! ShortenUrlResponse("",Some(ex))
      }

    case req : RedirectUrlRequest =>
      log.info(s"processing request : $req")
      val currentSender = sender()
      val shortUrl      =
        try {
          val exactPrefix = UrlShortenerConfig.shortenedUrlPrefix.substring(0,UrlShortenerConfig.shortenedUrlPrefix.length-1)
          req.shortUrl.startsWith("http") match {
            case true =>
              //user added http protocol to request
              val entries = req.shortUrl.split("//")(1)
              //first entry should be http(s)://moia.ly/
              entries.split("/")(0) == exactPrefix match {
                  case true  => Some(entries.split("/")(1))
                  case false => None
                }

            case false =>
              //user entered the link without the http protocol
             val entries = req.shortUrl.split("/")
              entries(0) == exactPrefix match {
                case true  => Some(entries(1))
                case false => None
              }
          }
        }catch {
          case _: ArrayIndexOutOfBoundsException => None
        }

      shortUrl match {
        case Some(sUrl) =>
          (redisDbService ? FetchElementRequest(sUrl)).mapTo[FetchElementResult] onComplete{
            case Success(res) =>
              res.result match {
                case Some(payload) =>
                  val urlData   = payload.parseJson.convertTo[UrlRedisData]
                  currentSender ! RedirectUrlResponse(urlData.actualUrl)
                case None          =>
                  currentSender ! RedirectUrlResponse("",Some(new Exception("Invalid Shortened Url")))
              }
            case Failure(ex)  =>
              log.error(s"Error received while fetching data for ${req.shortUrl} from Redis : ${ex.getMessage}")
              currentSender ! RedirectUrlResponse("",Some(new Exception("Internal Error, Cannot fetch Data")))
          }

        case None =>
          currentSender ! RedirectUrlResponse("",Some(new Exception("Invalid Shortened Url")))
      }
  }
}
