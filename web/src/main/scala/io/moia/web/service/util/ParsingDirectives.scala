package io.moia.web.service.util

import akka.http.scaladsl.server._
import Directives._

import io.moia.core.util.UrlShortenerUtil

object ParsingDirectives {

  trait FormDataElementT

  trait FormDataExtractorT{

    final def fromFields(fields : Map[String,String]) : Either[FormDataElementT, Rejection] = {
      (getRequiredStringFields ++ getRequiredIntFields).filter(!fields.contains(_)) match {
        case x :: _ => Right(MissingFormFieldRejection(x))
        case Nil     =>
          getRequiredIntFields.filter(x => UrlShortenerUtil.parseInt(fields(x)).isEmpty) match {
            case x :: _ => Right(MalformedFormFieldRejection(x,s"Expected a number but got $x"))
            case Nil    =>
              getOptionalIntFields.filter(fields.get(_) match {
                case Some(r) => UrlShortenerUtil.parseInt(r).isEmpty
                case None    => false
              }) match{
                case x :: _ => Right(MalformedFormFieldRejection(x,s"Expected a number but got $x"))
                case Nil    =>
                  try{
                    Left(fromFieldsImplementation(fields))
                  }catch {
                    case ex : Throwable =>
                      Right(ValidationRejection(ex.getMessage,None))
                  }
              }
          }
      }
    }

    protected def getRequiredStringFields : List[String] = Nil
    protected def getRequiredIntFields : List[String]    = Nil
    protected def getOptionalIntFields :List[String]     = Nil

    protected def fromFieldsImplementation(fields: Map[String, String]): FormDataElementT

    protected def getRequiredStringField(
      fields: Map[String, String],
      name: String
    ): String = fields(name)

    protected def getOptionalStringField(
      fields: Map[String, String],
      name: String
    ): Option[String] = fields.get(name)

    protected def getRequiredIntField(
     fields: Map[String, String],
     name: String
   ): Int = UrlShortenerUtil.parseInt(fields(name)).get

    protected def getOptionalIntField(
       fields: Map[String, String],
       name: String
     ): Option[Int] = fields.get(name) match {
      case Some(x) => UrlShortenerUtil.parseInt(x)
      case None => None
    }
  }

  def formDataElement(
   extractorT: FormDataExtractorT,
   fields : Map[String,String]
   ) : Directive1[FormDataElementT] = {
    extractorT.fromFields(fields) match {
      case Left(element)    => provide(element)
      case Right(rejection) => reject(rejection)
    }

  }
}
