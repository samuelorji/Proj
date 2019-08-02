package com.lunatech.imdb.core
package util

import com.typesafe.config.ConfigFactory

trait ConfigT {
  val config = ConfigFactory.load()

}
