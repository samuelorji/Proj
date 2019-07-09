package io.moia.core.util

import java.net.URL

import scala.util.Try

import org.apache.commons.validator.routines.UrlValidator

object UrlShortenerUtil {

  def parseUrl(input : String)  : Option[URL] = new UrlValidator().isValid(input) match {
    case true  => Some(new URL(input))
    case false => None
  }

  def parseInt(number : String) = Try(number.toInt).toOption

}
