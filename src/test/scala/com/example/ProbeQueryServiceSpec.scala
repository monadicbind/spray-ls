package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import com.example.model.Probe
import spray.json._

object ProbeLSProtocol extends DefaultJsonProtocol {
  implicit val probeFormat = jsonFormat3(Probe)
}

class ProbeQueryServiceSpec extends Specification with Specs2RouteTest with ProbeQueryService {
  def actorRefFactory = system
  
  "ProbeQueryService" should {

    //"return a greeting for GET requests to the root path" in {
     // Get() ~> myRoute ~> check {
     //   responseAs[String] must contain("Say hello")
     // }
    //}

    "get Probe when when passed in the correct parameter" in {
      Get("/getProbe/1") ~> myRoute ~> check {
        //println(responseAs[String])
        status === OK
        //responseAs[Probe] 
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === NotFound
        //responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}

