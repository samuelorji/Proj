package io.moia.web.service.marshalling

import io.moia.core.config.UrlShortenerConfig
import io.moia.service.shortener.UrlShortener.ShortenUrlResponse
import io.moia.web.service.util.ParsingDirectives.FormDataElementT


private[service] case class UrlToShortenResponse(url : String , error : Option[String] = None)

object ShortenerService {
  def fromUrlToShorten(resp : ShortenUrlResponse) = {
    resp.exception match {
      case Some(_) =>
        UrlToShortenResponse("", Some("Internal Error Shortening Url"))
      case None     =>
        UrlToShortenResponse(UrlShortenerConfig.shortenedUrlPrefix + resp.url)
    }
  }
}
