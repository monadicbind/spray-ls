package com.example.entity

import slick.driver.MySQLDriver.api._



/**
 * This is the MasterLMS table that refers to the master_lms table in the DB.
 * Created by sathish_mandapaka on 8/8/15.
 */
case class MasterLMS(id:Option[Long] = None,key:String,secret:String,name:String,
                     ltiId:String,createdDtTm:Option[java.util.Date],
                      updatedDtTm:Option[java.util.Date])

/**
 * Mapped to the master_lms table
 */

class MappingMasterLMS(tag:Tag) extends
Table[MasterLMS](tag,"master_lms") {

    implicit val DateTimeColumnType = MappedColumnType.base[java.util.Date, java.sql.Date](
    {
      ud => new java.sql.Date(ud.getTime)
    }, {
      sd => new java.util.Date(sd.getTime)
    })
    def id = column[Long]("id",O.PrimaryKey,O.AutoInc)
    def key = column[String]("key")
    def secret = column[String]("secret")
    def name = column[String]("name")
    def ltiId = column[String]("lti_id")
    def createdDtTm = column[java.util.Date]("created_dttm")
    def updatedDtTm = column[java.util.Date]("updated_dttm")

    //def columns = (key , secret , name , ltiId , createdDtTm , updatedDtTm)

    //def forInsert = columns <> (s => MasterLMS(s.,s._2,s._3,s._4,s._5,s._6) , (s:MasterLMS) => Some((s.key,s.secret,s.name,s.ltiId,s.createdDtTm,s.updatedDtTm)))

    def * = (id.? , key , secret , name  , ltiId , createdDtTm.? , updatedDtTm.?) <>
      ((MasterLMS.apply _).tupled , MasterLMS.unapply _)

}




