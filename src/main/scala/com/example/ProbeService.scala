package com.example

import akka.actor.Actor
import akka.event.slf4j.SLF4JLogging
import com.example.dao.MasterLMSDAO
import com.example.model._
import com.typesafe.scalalogging.LazyLogging
import org.json4s.DefaultFormats
import spray.http.MediaTypes._
import spray.http.{StatusCodes, StatusCode}
import spray.httpx.Json4sSupport
import spray.httpx.unmarshalling._
import spray.routing._

import scala.concurrent.Future
import scala.util.{Failure, Success}


/*object ProbeLSProtocol extends DefaultJsonProtocol {
  implicit val probeFormat = jsonFormat3(Probe)
  implicit  val answerFormat = jsonFormat2(Answer)
}*/

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class ProbeQueryServiceActor extends Actor with ProbeQueryService with ProbeCommandService{

  override implicit val json4sFormats= DefaultFormats

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute~cmdRoute)
}


// this trait defines our service behavior independently from the service actor
trait ProbeQueryService extends HttpService with Json4sSupport with SLF4JLogging {

  val json4sFormats = DefaultFormats

  val lmsService = MasterLMSDAO

  implicit def executionContext = actorRefFactory.dispatcher

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
    } ~
    path("lms"){
      put{
        respondWithMediaType(`application/json`){
          entity(as[MasterLMS]){ masterLMS =>
            complete {
              log.info("New LMS Request")
              val insLMSwithId = lmsService.insert(masterLMS).mapTo[com.example.dao.MasterLMSDAO.MasterLMS]
              insLMSwithId onComplete {
                case Success(opVal) => {
                  log.info("Inserted new Master LMS record: %s".format(opVal.id))
                }
                case Failure(ex) => {
                  log.info("Exception thrown : %s".format(ex.getLocalizedMessage))
                }

              }
              insLMSwithId
            }

          }

        }
      } ~
      post{
        respondWithMediaType(`application/json`){
          entity(as[MasterLMS]) { updatedLMS => {
            log.info("Updating LMS with ID : %d".format(updatedLMS.id))
            val opVal = lmsService.update(updatedLMS.id,updatedLMS)
            onComplete(opVal) {
              case Success(respInt) => {
                log.info("Response to the Update : %s".format(respInt.toString))
                complete(opVal)
              }
              case Failure(ex) => {
                log.info("Failed"+ex.getLocalizedMessage)
                complete(StatusCodes.InternalServerError)
              }

            }

          }
          }
        }
      }

    } ~
    path("lms" / LongNumber) { lmsId =>
      get {
        respondWithMediaType(`application/json`){
          complete {
            log.info("Requesting LMS with ID : %d".format(lmsId))
            val test:Future[com.example.dao.MasterLMSDAO.MasterLMS] = lmsService.findById(lmsId).mapTo[com.example.dao.MasterLMSDAO.MasterLMS]
            test onComplete {
              case Success(opVal) => {
                log.info("LTI ID is : %s".format(opVal.ltiId))
              }
              case Failure(ex) => {
                log.info("Failed due to the following exception: TEST")
              }
            }

            test
          }
        }
      } ~
      delete{
        respondWithMediaType(`application/json`) {
          log.info("Delete Master LMS Record with id : %s".format(lmsId.toString))
          val deleteLms = lmsService.delete(lmsId)
          onComplete(deleteLms) {
            case Success(opVal) => {
              log.info("Response from db delete is : %s".format(opVal.toString))
              log.info("Successfully deleted LMS Record with id : %s".format(lmsId.toString))
              complete(StatusCodes.OK)
            }
            case Failure(ex) => {
              log.info("Failed to delete the lms record")
              log.info("Exception Message : %s".format(ex.getLocalizedMessage))
              complete(StatusCodes.InternalServerError,s"Exception Message: ${ex.getLocalizedMessage}")
            }
          }
        }
      }
    }
}

trait ProbeCommandService extends HttpService with Json4sSupport {

  val json4sFormats = DefaultFormats

  val cmdRoute =
    path("answerProbe"){
      respondWithMediaType(`application/json`) {
          entity(as[Answer]) { answer =>
            complete(answer.id)
        }
      }

    }
}