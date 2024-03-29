package io.moia.web.service.marshalling

import io.moia.core.util.UrlShortenerUtil
import io.moia.web.service.marshalling.UrlToShortenRequest.getRequiredStringField
import io.moia.web.service.util.ParsingDirectives.{FormDataElementT, FormDataExtractorT}

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