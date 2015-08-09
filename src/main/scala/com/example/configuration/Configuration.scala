package com.example.configuration

import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
 * Created by sathish_mandapaka on 8/8/15.
 */
trait Configuration {

  val config = ConfigFactory.load()

  lazy val dbHost = Try(config.getString("db.host")).getOrElse("localhost")
  lazy val dbPort = Try(config.getString("db.port")).getOrElse("3306")
  lazy val dbName = Try(config.getString("db.name")).getOrElse("lmsroster")
  lazy val dbUser = Try(config.getString("db.user")).toOption.orNull
  lazy val dbPassword = Try(config.getString("db.password")).toOption.orNull

}
