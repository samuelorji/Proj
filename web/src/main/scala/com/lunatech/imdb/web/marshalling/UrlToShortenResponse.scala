package com.lunatech.imdb.web.marshalling

import com.lunatech.imdb.core.config.UrlShortenerConfig
import com.lunatech.imdb.service.shortener.UrlShortener.ShortenUrlResponse
import com.lunatech.imdb.web.util.ParsingDirectives.FormDataElementT


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
