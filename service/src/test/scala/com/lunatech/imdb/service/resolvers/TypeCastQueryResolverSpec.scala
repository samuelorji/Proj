package com.lunatech.imdb.service.resolvers

import akka.actor.{ActorRef, Props}
import com.lunatech.imdb.service.TestServiceT
import com.lunatech.imdb.service.resolvers.TypeCastQueryResolver.{CheckIfTypeCastedRequest, CheckIfTypeCastedResponse}

class TypeCastQueryResolverSpec extends TestServiceT {

  "The Coincidence Query Resolver " must {
    "Properly parse a Failed  GetCoinidenceQueryRequest with error  " in {

      val expectedErrorMessage      = "Failure occured while fetching data"
      val typecastQueryResolver = system.actorOf(Props(new TypeCastQueryResolver(){
        override def getTypeCastStatus(req: TypeCastQueryResolver.CheckIfTypeCastedRequest, sender: ActorRef): Unit = {
          //simulate a failed Check with an error
          sender ! CheckIfTypeCastedResponse(false,Some(expectedErrorMessage))
        }
      }))

      val request = CheckIfTypeCastedRequest("Jennifer Anisto")

      typecastQueryResolver ! request

      val response = expectMsgType[CheckIfTypeCastedResponse]
      response.error should be(Some(expectedErrorMessage))
    }

    "Properly parse a Successful GetCoinidenceQueryRequest without error " in {

      //simulate a successful query wihout an error

      val typecastQueryResolver = system.actorOf(Props(new TypeCastQueryResolver(){
        override def getTypeCastStatus(req: TypeCastQueryResolver.CheckIfTypeCastedRequest, sender: ActorRef): Unit = {
          //simulate a failed Check with an error
          sender ! CheckIfTypeCastedResponse(false,None)
        }

      }))
      val request = CheckIfTypeCastedRequest("Jennifer Aniston")

      typecastQueryResolver ! request

      val response = expectMsgType[CheckIfTypeCastedResponse]
      response.error should be(None)
    }


  }

}
