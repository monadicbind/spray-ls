package com.example.dao

import akka.event.slf4j.SLF4JLogging
import com.example.configuration.Configuration
import com.example.entity.MappingMasterLMS
import slick.driver.MySQLDriver.api._
import com.example.entity._


import scala.concurrent.Future

/**
 * Created by sathish_mandapaka on 8/8/15.
 */
class MasterLMSDAO extends Configuration with SLF4JLogging{

  private val db = Database.forURL(url = "jdbc:mysql://%s:%s/%s".format(dbHost, dbPort, dbName),
  user=dbUser,  password = dbPassword, driver="com.mysql.jdbc.Driver")

  private val masterLmss = TableQuery[MappingMasterLMS]

  def filterQuery(id:Long):Query[MappingMasterLMS,MasterLMS,Seq] =
    masterLmss.filter(_.id === id)

  def findById(id:Long):Future[MasterLMS] =
    try {
      log.info("Retrieving the Master LMS with id : %s from DB".format(id.toString))
      db.run(filterQuery(id).result.head)
      //throw new Exception("Test")
    }
    //finally db.close



}
