package com.lunatech.imdb.service
package resolvers

import akka.actor.{ ActorRef, Props }

import scala.concurrent.Future

import CoincidenceQueryResolver._

import com.github.mauricio.async.db.QueryResult

class CoincidenceQueryResolverSpec extends TestServiceT{
  "The Coincidence Query Resolver " must {
    "Properly parse a Failed  GetCoinidenceQueryRequest with error  " in {

      val expectedErrorMessage      = "Failure occured while fetching data"
      val coincidenceQueryResolver  = system.actorOf(Props(new CoincidenceQueryResolver(){
        override def getCoincidence(req: CoincidenceQueryResolver.GetCoincidenceRequest, sender: ActorRef): Unit = {
          //Simulating a Failed CoincidenceResponse
          sender ! GetCoincidenceResponse(List(),Some(expectedErrorMessage))
        }

      }))

      val request = GetCoincidenceRequest("Jennifer","AnistonQ")

      coincidenceQueryResolver ! request

      val response = expectMsgType[GetCoincidenceResponse]
      response.error should be(Some(expectedErrorMessage))
    }

    "Respond with an error when the database query Fails  " in {

      val expectedErrorMessage      = "Failure occured while fetching data"
      val coincidenceQueryResolver  = system.actorOf(Props(new CoincidenceQueryResolver(){
        //simulate a failed database query
        override def fetchFromDb(query: => Future[QueryResult]): Future[QueryResult] = Future.failed(new Exception(expectedErrorMessage))
      }))

      val request = GetCoincidenceRequest("Jennifer","AnistonQ")
      coincidenceQueryResolver ! request

      val response = expectMsgType[GetCoincidenceResponse]
      response.error should be(Some(expectedErrorMessage))
    }

    "Respond without an error when the database query is successful  " in {

      val coincidenceQueryResolver  = system.actorOf(Props(new CoincidenceQueryResolver(){
        //simulate a successful database query
        override def fetchFromDb(query: => Future[QueryResult]): Future[QueryResult] = Future.successful(new QueryResult(1L,""))
      }))

      val request = GetCoincidenceRequest("Jennifer","Aniston")
      coincidenceQueryResolver ! request

      val response = expectMsgType[GetCoincidenceResponse]
      response.error should be(None)
    }

    "Properly parse a Successful GetCoinidenceQueryRequest without error " in {

      val coincidenceQueryResolver = system.actorOf(Props(new CoincidenceQueryResolver(){
        override def getCoincidence(req: CoincidenceQueryResolver.GetCoincidenceRequest, sender: ActorRef): Unit = {
          //Simulating a Successful CoincidenceResponse
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
