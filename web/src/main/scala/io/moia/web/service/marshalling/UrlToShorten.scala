package io.moia.web
package service.marshalling

import io.moia.core.config.UrlShortenerConfig
import io.moia.core.util.UrlShortenerUtil
import io.moia.service.shortner.UrlShortner.ShortenUrlResponse
import io.moia.web.service.util.ParsingDirectives._


private[service] case class UrlToShortenRequest(url : String ) extends FormDataElementT {
  require(url.startsWith("http"),"Url should contain correct Http Protocol(http:// or https://)")
  require(UrlShortenerUtil.parseUrl(url).isDefined,"Invalid Url")
}
private[service] object UrlToShortenRequest extends FormDataExtractorT {
  override protected def getRequiredStringFields: List[String] = List("url")
  override protected def fromFieldsImplementation(fields: Map[String, String]): FormDataElementT =
    UrlToShortenRequest(
      url = getRequiredStringField(fields,"url")
    )
}

private[service] case class UrlToShortenResponse(url : String , error : Option[String] = None) extends FormDataElementT

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

private[service] case class ShortenedUrlRequest(url : String) extends FormDataElementT

private[service] object ShortenedUrlRequest extends FormDataExtractorT{
  override protected def getRequiredStringFields: List[String] = List("url")
  override protected def fromFieldsImplementation(fields: Map[String, String]): FormDataElementT =
    ShortenedUrlRequest(
      url = getRequiredStringField(fields,"url")
    )
}

