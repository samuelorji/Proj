package io.moia.core.util

import java.net.URL

import io.moia.core.TestServiceT

class UrlShortnerUtilSpec extends TestServiceT {
  "The UrlShortnerUtil" must {
    "properly parse a valid url" in {
      UrlShortenerUtil.parseUrl("https://moia.io")
        .shouldBe(Some(new URL("https://moia.io")))
    }
    "Return None for an invalid URL" in {
      UrlShortenerUtil.parseUrl("https://moia.ioty")
      .shouldBe(None)
    }

    "properly parse a valid Integer" in {
      UrlShortenerUtil.parseInt("34").shouldBe(Some(34))
    }
    "return None for an invalid Int" in {
      UrlShortenerUtil.parseInt("445r") shouldBe(None)
    }
  }

}
