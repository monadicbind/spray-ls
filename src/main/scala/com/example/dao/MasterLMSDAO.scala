package com.example.dao

import java.sql.Timestamp
import java.util.Date


import akka.event.slf4j.SLF4JLogging
import com.example.configuration.Configuration
import com.example.model
import com.typesafe.scalalogging.LazyLogging
import slick.driver.MySQLDriver.api._


import scala.concurrent.Future

/**
 * Created by sathish_mandapaka on 8/8/15.
 */
object MasterLMSDAO extends Configuration with SLF4JLogging{

  case class MasterLMS(id:Option[Long] = None,ltiId:String,createdDtTm:Option[java.util.Date],
                       updatedDtTm:Option[java.util.Date])

  implicit val DateTimeColumnType = MappedColumnType.base[java.util.Date, java.sql.Timestamp](
  {
    ud => new Timestamp(ud.getTime)
  }, {
    sd => new java.util.Date(sd.getTime)
  })

  /**
   * Mapped to the master_lms table
   */

  class MappingMasterLMS(tag:Tag) extends
  Table[MasterLMS](tag,"master_lms") {


    def id = column[Long]("id",O.PrimaryKey,O.AutoInc)
    def ltiId = column[String]("lti_id")

    def createdDtTm = column[java.util.Date]("created_dttm")
    def updatedDtTm = column[java.util.Date]("updated_dttm")

    def * = (id.? , ltiId , createdDtTm.? , updatedDtTm.?) <>
      (MasterLMS.tupled , MasterLMS.unapply)

  }

  private val db = Database.forURL(url = "jdbc:mysql://%s:%s/%s".format(dbHost, dbPort, dbName),
  user=dbUser,  password = dbPassword, driver="com.mysql.jdbc.Driver")

  private val masterLmss = TableQuery[MappingMasterLMS]

  def filterQuery(id:Long):Query[MappingMasterLMS,MasterLMS,Seq] =
    masterLmss.filter(_.id === id)

  def findById(id:Long):Future[MasterLMS] = {
    log.info("Finding by id : %s".format(id.toString))
    db.run(filterQuery(id).result.head)
  }



  def insert(lms: model.MasterLMS):Future[MasterLMS] =  {
    val test = new MasterLMS(None,lms.ltiId,Some(new Date()), Some(new Date()))
    val masterLmsWithId = (masterLmss returning masterLmss.map(_.id) into ((insLMS,id1) => insLMS.copy(id = Some(id1)))
      += test)
    db.run(masterLmsWithId)
  }

  def update(updLMSID:Long , updatedLMS: com.example.model.MasterLMS):Future[MasterLMS] = {
    db.run(filterQuery(updLMSID).map(row => (row.ltiId,row.updatedDtTm)).update(updatedLMS.ltiId,new Date()).andThen(filterQuery(updLMSID).result.head))
  }

  def delete(id:Long):Future[Int] = {
    db.run(filterQuery(id).delete)
  }


}
