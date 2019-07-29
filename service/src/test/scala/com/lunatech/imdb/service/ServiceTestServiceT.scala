package com.lunatech.imdb.service

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

abstract class ServiceTestServiceT extends TestKit(ActorSystem("Service"))
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
