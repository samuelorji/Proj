package com.lunatech.imdb.web.marshalling

import akka.http.scaladsl.server.{MissingFormFieldRejection, ValidationRejection}
import com.lunatech.imdb.web.Test.WebUnitTestT
import io.moia.web.service.Test.WebUnitTestT

class UrlToShortenRequestSpec extends WebUnitTestT {

  val validUrlWithProtocol = "https://moia.ly/cgrt56"
  val validUrlWithoutProtocol = "moia.ly/cgrt56"
  "A UrlToShorten element" should
    "correctly parse fields from forms " in {


      assert(UrlToShortenRequest.fromFields(Map())
        === Right(MissingFormFieldRejection("url")))
      assert(UrlToShortenRequest.fromFields(
        Map[String, String]("url" -> validUrlWithProtocol))
        === Left(UrlToShortenRequest(validUrlWithProtocol)))

    assert(UrlToShortenRequest.fromFields(
      Map[String, String]("url" -> validUrlWithoutProtocol))
      === Right(ValidationRejection("requirement failed: Url should contain correct Http Protocol(http:// or https://)",None)))


  }




}
