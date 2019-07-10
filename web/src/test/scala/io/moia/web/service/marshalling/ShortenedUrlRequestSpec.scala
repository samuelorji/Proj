package io.moia.web.service.marshalling

import akka.http.scaladsl.server.MissingFormFieldRejection

import io.moia.web.service.Test.WebUnitTestT

class ShortenedUrlRequestSpec extends WebUnitTestT {

  "A ShortenRequest element" should "correctly parse fields from forms " in {
    val validUrlWithProtocol    = "https://moia.ly/cgrt56"
    val validUrlWithoutProtocol = "moia.ly/cgrt56"
    assert(ShortenedUrlRequest.fromFields(Map()) === Right(MissingFormFieldRejection("url")))
    assert(ShortenedUrlRequest.fromFields(
      Map[String,String]("url" -> validUrlWithProtocol)) === Left(ShortenedUrlRequest(validUrlWithProtocol)))

    assert(ShortenedUrlRequest.fromFields(
      Map[String,String]("url" -> validUrlWithoutProtocol)) === Left(ShortenedUrlRequest(validUrlWithoutProtocol)))


  }
}
