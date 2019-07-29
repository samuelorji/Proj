package com.lunatech.imdb.service.shortener

import java.security.MessageDigest

import scala.util.Random

trait StringManipulatorT {

  def getUniqueString : String = {
    val rand = new Random()
    val inputToHash  = (1 to rand.nextInt(10)).map(_ => rand.nextPrintableChar()).mkString + System.currentTimeMillis()
    val hashedString = hash(inputToHash)

    hashedString.take(6) + hashedString(rand.nextInt(20)) + hashedString(rand.nextInt(30))
  }
  private def hash(input : String) : String = {
    MessageDigest.getInstance("SHA-256").digest(input.getBytes).map("%02x".format(_)).mkString
  }

}
