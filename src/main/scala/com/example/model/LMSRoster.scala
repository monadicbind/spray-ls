package com.example.model

import com.fasterxml.jackson.annotation.{JsonInclude, JsonIgnoreProperties}

/**
 * Created by sathish_mandapaka on 8/8/15.
 */
case class LMSRoster()

case class MasterLMS( id:Long, ltiId:String) {
  def this(ltiId:String) = this(0,ltiId)
}
