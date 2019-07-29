package com.lunatech.imdb.web.marshalling

import com.lunatech.imdb.web.util.ParsingDirectives.{FormDataElementT, FormDataExtractorT}
import io.moia.web.service.util.ParsingDirectives._

private[service] case class ShortenedUrlRequest(url : String) extends FormDataElementT

private[service] object ShortenedUrlRequest extends FormDataExtractorT{
  override protected def getRequiredStringFields: List[String] = List("url")
  override protected def fromFieldsImplementation(fields: Map[String, String]): FormDataElementT =
    ShortenedUrlRequest(
      url = getRequiredStringField(fields,"url")
    )
}

