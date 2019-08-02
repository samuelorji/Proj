package com.lunatech.imdb.service.resolvers

import akka.actor.{ActorRef, Props}
import com.github.mauricio.async.db.QueryResult
import com.lunatech.imdb.service.TestServiceT
import com.lunatech.imdb.service.resolvers.TypeCastQueryResolver.{CheckIfTypeCastedRequest, CheckIfTypeCastedResponse}

import scala.concurrent.Future

class TypeCastQueryResolverSpec extends TestServiceT {

  "The Coincidence Query Resolver " must {
    "Properly parse a Failed  GetCoinidenceQueryRequest with error  " in {

      val expectedErrorMessage      = "Failure occured while fetching data"
      val typecastQueryResolver     = system.actorOf(Props(new TypeCastQueryResolver(){
        override def getTypeCastStatus(req: TypeCastQueryResolver.CheckIfTypeCastedRequest, sender: ActorRef): Unit = {
          //simulate a Failed Response
          sender ! CheckIfTypeCastedResponse(false,Some(expectedErrorMessage))
        }
      }))

      val request = CheckIfTypeCastedRequest("Jennifer Anisto")

      typecastQueryResolver ! request

      val response = expectMsgType[CheckIfTypeCastedResponse]
      response.error should be(Some(expectedErrorMessage))
    }

    "Respond with an error if the database query Fails " in {

      val expectedErrorMessage      = "Failure occured while fetching data"
      val typecastQueryResolver     = system.actorOf(Props(new TypeCastQueryResolver(){

        //simulate a failed database query with an error
        override def fetchFromDb(query: => Future[QueryResult]): Future[QueryResult] = Future.failed(new Exception(expectedErrorMessage))
      }))

      val request = CheckIfTypeCastedRequest("Jennifer Anisto")

      typecastQueryResolver ! request

      val response = expectMsgType[CheckIfTypeCastedResponse]
      response.error should be(Some(expectedErrorMessage))
    }


    "Respond without an error if the database query is successful " in {

      val typecastQueryResolver = system.actorOf(Props(new TypeCastQueryResolver(){
        //simulate a successful query without an error
        override def fetchFromDb(query: => Future[QueryResult]): Future[QueryResult] = Future.successful(new QueryResult(1L,""))
      }))
      val request = CheckIfTypeCastedRequest("Jennifer Aniston")

      typecastQueryResolver ! request

      val response = expectMsgType[CheckIfTypeCastedResponse]
      response.error should be(None)
    }

    "Properly parse a Successful GetCoinidenceQueryRequest without error  " in {
      val typecastQueryResolver = system.actorOf(Props(new TypeCastQueryResolver(){
        override def getTypeCastStatus(req: TypeCastQueryResolver.CheckIfTypeCastedRequest, sender: ActorRef): Unit = {
          //simulate a Successful Response
          sender ! CheckIfTypeCastedResponse(false,None)
        }
      }))

      val request = CheckIfTypeCastedRequest("Jennifer Anisto")

      typecastQueryResolver ! request

      val response = expectMsgType[CheckIfTypeCastedResponse]
      response.error should be(None)
    }


  }

}
