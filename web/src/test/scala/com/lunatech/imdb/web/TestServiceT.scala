package com.lunatech.imdb.web

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestKit

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

private[web] abstract class TestServiceT
  extends Matchers
  with WordSpecLike
  with ScalatestRouteTest
  with BeforeAndAfterAll
{

  override def beforeAll {
    Thread.sleep(2000)
  }

  override def afterAll {
    Thread.sleep(2000)
    TestKit.shutdownActorSystem(system)
  }
}
