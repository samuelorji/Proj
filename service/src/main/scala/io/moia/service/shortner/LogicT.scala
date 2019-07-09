package io.moia.service.shortner

import java.security.MessageDigest

import scala.util.Random

trait LogicT {

  def getUniqueString : String = {
    val rand = new Random()
    val inputToHash = (1 to rand.nextInt(10)).map(_ => rand.nextPrintableChar()).mkString + System.currentTimeMillis()
    hash(inputToHash)
  }
  private def hash(input : String) : String = {

    MessageDigest.getInstance("SHA-256").digest(input.getBytes).map("%02x".format(_)).mkString.take(8)
  }

}
