package com.lunatech.imdb.service.resolvers

import akka.actor.{ActorRef, Props}
import com.lunatech.imdb.service.TestServiceT
import com.lunatech.imdb.service.resolvers.CoincidenceQueryResolver.{GetCoincidenceRequest, GetCoincidenceResponse}

class CoincidenceQueryResolverSpec extends TestServiceT{
  "The Coincidence Query Resolver " must {
    "Properly parse a Failed  GetCoinidenceQueryRequest with error  " in {

      val expectedErrorMessage      = "Failure occured while fetching data"
      val coincidenceQueryResolver = system.actorOf(Props(new CoincidenceQueryResolver(){
        override def getCoincidence(req: CoincidenceQueryResolver.GetCoincidenceRequest, sender: ActorRef): Unit = {
          //Simulating a Failed CoincidenceResponse

          sender ! GetCoincidenceResponse(List(),Some(expectedErrorMessage))
        }

      }))


      val request = GetCoincidenceRequest("Jennifer","Aniston")

      coincidenceQueryResolver ! request

      val response = expectMsgType[GetCoincidenceResponse]
      response.error should be(Some(expectedErrorMessage))
    }

    "Properly parse a Successful GetCoinidenceQueryRequest without error " in {

      val coincidenceQueryResolver = system.actorOf(Props(new CoincidenceQueryResolver(){
        override def getCoincidence(req: CoincidenceQueryResolver.GetCoincidenceRequest, sender: ActorRef): Unit = {
          //Simulating a Failed CoincidenceResponse

          sender ! GetCoincidenceResponse(List(),None)
        }
      }))


      val request = GetCoincidenceRequest("Jennifer","Aniston")

      coincidenceQueryResolver ! request

      val response = expectMsgType[GetCoincidenceResponse]
      response.error should be(None)
    }


  }

}
