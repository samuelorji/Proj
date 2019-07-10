package io.moia.core.config

import scala.concurrent.duration.FiniteDuration

import io.moia.core.CoreTestServiceT

class ConfigSpec extends CoreTestServiceT{

  "The UrlShortnerConfig" must {
    "parse values correctly" in {
      UrlShortenerConfig.webHost should be ("127.0.0.1")
      UrlShortenerConfig.webPort should be (8080)
      UrlShortenerConfig.shortenedUrlPrefix shouldBe("moia.ly/")
      UrlShortenerConfig.httpRequestsTimeout shouldBe(FiniteDuration(5,"seconds"))
      UrlShortenerConfig.moiaRedisTimeout shouldBe(FiniteDuration(5,"seconds"))
      UrlShortenerConfig.moiaRedisDbHost shouldBe("localhost")
      UrlShortenerConfig.moiaRedisDbPort shouldBe(6379)
      UrlShortenerConfig.moiaRedisDbNumWorkers shouldBe(2)
    }
  }

}
