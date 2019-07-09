package io.moia.service.shortner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout

import io.moia.core.config.UrlShortenerConfig
import io.moia.core.db.MoiaRedisDbService
import io.moia.core.db.RedisDbT.{AddElementRequest, AddElementResult, FetchElementRequest, FetchElementResult}
import io.moia.core.util.InstanceManager
import io.moia.service.marshalling.JsonHelper
import spray.json._

object UrlShortner extends InstanceManager[ActorRef]{
  case class ShortenUrlRequest(url : String)
  case class ShortenUrlResponse(url : String,exception: Option[Throwable] = None)

  case class UrlRedisData(shortUrl : String , actualUrl : String)

  case class RedirectUrlRequest(shortUrl : String)
  case class RedirectUrlResponse(actualUrl : String , exception : Option[Throwable] = None)

  def initializeRedis(implicit context : ActorSystem) = {
    setInstance(MoiaRedisDbService.getRedisInstance)
  }
}
class UrlShortner extends Actor
  with LogicT
  with JsonHelper
  with ActorLogging {

  import UrlShortner._

  private val redisDbService = createRedisDbService
  def createRedisDbService   = UrlShortner.getInstance

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
          req.shortUrl.startsWith("http") match {
            case true =>
              //user added http protocol to request
              Some(req.shortUrl.split("//")(1).split("/")(1))
            case false =>
              //user probably just entered the link without the http protocol
              Some(req.shortUrl.split("/")(1))
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
