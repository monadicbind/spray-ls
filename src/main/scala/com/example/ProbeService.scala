package com.example

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.httpx.Json4sSupport
import org.json4s.Formats
import org.json4s.DefaultFormats
import com.example.model._
import spray.httpx.SprayJsonSupport._
import spray.util._
import spray.json._ 
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._


object ProbeLSProtocol extends DefaultJsonProtocol {
  implicit val probeFormat = jsonFormat3(Probe)
  implicit  val answerFormat = jsonFormat2(Answer)
}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ProbeQueryServiceActor extends Actor with ProbeQueryService with ProbeCommandService{

  //implicit def json4sFormats: Formats = DefaultFormats

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute~cmdRoute)
}


// this trait defines our service behavior independently from the service actor
trait ProbeQueryService extends HttpService {
  import ProbeLSProtocol._

  val myRoute =
    path("getAllProbes") {
      get {
        complete{
          val prb1 =  Probe("1","Test 1 ?" , List("ans1", "ans 2" , "ans 3", "ans 4"))
          val prb2 =  Probe("2","Test 2 ?" , List("ans1", "ans 2" , "ans 3", "ans 4"))
          val prb3 =  Probe("3","Test 3 ?" , List("ans1", "ans 2" , "ans 3", "ans 4"))
          List(prb1,prb2,prb3)
        }
      }
    } ~
    path("getProbe" / Segment) { probeId =>
      get {
        respondWithMediaType(`application/json`) {
          complete{
            val probe = Probe("1","Test ?" , List("ans1", "ans 2" , "ans 3", "ans 4"))
            probe
          }
        }
      }
    } 
}

trait ProbeCommandService extends HttpService {
  import ProbeLSProtocol._

  val cmdRoute =
    path("answerProbe"){
      respondWithMediaType(`application/json`) {
          entity(as[Answer]) { answer =>
            complete(answer.id)
        }
      }

    }
}