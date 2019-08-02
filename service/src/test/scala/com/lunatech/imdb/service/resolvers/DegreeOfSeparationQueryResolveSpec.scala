package com.lunatech.imdb.service
package resolvers

import akka.actor.{ ActorRef, Props }

import DegreeOfSeparationQueryResolver._

import org.neo4j.driver.v1.Value

import scala.concurrent.Future

class DegreeOfSeparationQueryResolveSpec extends TestServiceT {
  "The Degree Of Separation Actor " should {

    "Respond with a failure if the DB query fails " in {
      val expectedErrorMessage            = "Failure occured while fetching data"
      val degreeOfSeparationQueryResolver = system.actorOf(Props(
        new DegreeOfSeparationQueryResolver(){
          //Simulate a failed query from DB
          override def fetchFromDb(query: => Future[Value]): Future[Value] = Future.failed(new Exception(expectedErrorMessage))
        }))
      val request  = GetDegreeOfSeparationRequest("Jennifer AnistonQ")

      degreeOfSeparationQueryResolver ! request

      val response = expectMsgType[GetDegreeOfSeparationResponse]
      response.error should be(Some(expectedErrorMessage))
    }

    "Respond with a Success if the DB query is Successful " in {
      val degreeOfSeparationQueryResolver = system.actorOf(Props(
        new DegreeOfSeparationQueryResolver(){
          override def getDegreeOfSeparation(req: GetDegreeOfSeparationRequest, sender: ActorRef): Unit = {
            sender ! GetDegreeOfSeparationResponse(Some(9),None)
          }
        }))

      val request  = GetDegreeOfSeparationRequest("Jennifer Aniston")
      degreeOfSeparationQueryResolver ! request
      val response = expectMsgType[GetDegreeOfSeparationResponse]
      response.error should be(None)

    }
  }

}
