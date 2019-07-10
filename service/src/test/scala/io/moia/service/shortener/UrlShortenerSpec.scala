package io.moia.service
package shortener

import scala.concurrent.duration._

import akka.actor.{ActorRef, Props}
import akka.testkit.TestProbe

import io.moia.core.db.RedisDbT.{AddElementRequest, AddElementResult, FetchElementRequest, FetchElementResult}
import io.moia.service.marshalling.JsonHelper
import spray.json._
import io.moia.service.shortener.UrlShortener._


class UrlShortenerSpec extends TestServiceT with JsonHelper {

  val redisProbe    = TestProbe()
  val mockUniqueUrl = "moiagood"
  val actualUrl     = "https://moia.io"
  val urlRedisData  = UrlRedisData(
    shortUrl  = mockUniqueUrl,
    actualUrl = actualUrl
  )

  val urlShortener = system.actorOf(Props(new UrlShortener(){
    override def createRedisDbService: ActorRef = redisProbe.ref
    override def getUniqueString: String = mockUniqueUrl
  }))
  "The UrlShortner Actor " must {
    "Shorten a Valid URL " in {

      val shortenUrlRequest = ShortenUrlRequest(url = actualUrl)

      urlShortener ! shortenUrlRequest
      redisProbe.expectMsg(AddElementRequest(mockUniqueUrl,urlRedisData.toJson.prettyPrint))
      redisProbe.reply(AddElementResult(true))

      expectMsg(ShortenUrlResponse(mockUniqueUrl))

      expectNoMessage(100 millis)
      redisProbe.expectNoMessage(100 millis)

    }
    "return an empty string when there is an error adding to Redis " in {
      val shortenUrlRequest = ShortenUrlRequest(url = actualUrl)

      urlShortener ! shortenUrlRequest

      redisProbe.expectMsg(AddElementRequest(mockUniqueUrl,urlRedisData.toJson.prettyPrint))
      redisProbe.reply(AddElementResult(false))

      val redisResponse = expectMsgType[ShortenUrlResponse]
      redisResponse.url.shouldBe("")
      redisResponse should not be None

      expectNoMessage(100 millis)
      redisProbe.expectNoMessage(100 millis)

    }

    "Properly parse a valid RedirectUrl Request without Http protocol " in {
      urlShortener ! RedirectUrlRequest(
        shortUrl =  "moia.ly/" + mockUniqueUrl
      )

      redisProbe.expectMsg(FetchElementRequest(
        key = mockUniqueUrl
      ))
      redisProbe.reply(FetchElementResult(
        result = Some(urlRedisData.toJson.prettyPrint)
      ))

      expectMsg(RedirectUrlResponse(
        actualUrl = actualUrl
      ))

      expectNoMessage(100 millis)
      redisProbe.expectNoMessage(100 millis)
    }
  }

  "Properly parse a valid RedirectUrl Request with Http protocol " in {
    urlShortener ! RedirectUrlRequest(
      shortUrl =  "https://moia.ly/" + mockUniqueUrl
    )

    redisProbe.expectMsg(FetchElementRequest(
      key = mockUniqueUrl
    ))
    redisProbe.reply(FetchElementResult(
      result = Some(urlRedisData.toJson.prettyPrint)
    ))

    expectMsg(RedirectUrlResponse(
      actualUrl = actualUrl
    ))

    expectNoMessage(100 millis)
    redisProbe.expectNoMessage(100 millis)
  }

  "Return an empty string with an error when unable to fetch from Redis " in {
    urlShortener ! RedirectUrlRequest(
      shortUrl =  "https://moia.ly/" + mockUniqueUrl
    )

    redisProbe.expectMsg(FetchElementRequest(
      key = mockUniqueUrl
    ))
    redisProbe.reply(FetchElementResult(
      result = None
    ))

    val msg = expectMsgType[RedirectUrlResponse]

    msg.actualUrl shouldBe("")
    msg.exception should not be None

    expectNoMessage(100 millis)
    redisProbe.expectNoMessage(100 millis)

  }
}


