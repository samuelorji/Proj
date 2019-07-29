package com.lunatech.imdb.web.Test

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

abstract class WebTestServiceT extends TestKit(ActorSystem("Web"))
  with ImplicitSender
  with Matchers
  with WordSpecLike
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
