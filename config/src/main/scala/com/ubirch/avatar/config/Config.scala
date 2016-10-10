package com.ubirch.avatar.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2016-09-20
  */
object Config extends ConfigBase {

  /*
   * SERVER RELATED
   ****************************************************************/

  /**
    * The interface the server runs on.
    *
    * @return interface
    */
  def interface: String = config.getString(ConfigKeys.HTTPINTERFACE)

  /**
    * Port the server listens on.
    *
    * @return port number
    */
  def port: Int = config.getInt(ConfigKeys.HTTPPORT)

  /**
    *
    * @return
    */
  def esHost: String = config.getString(ConfigKeys.ESHOST)

  /**
    *
    * @return
    */
  def esPort: Int = config.getInt(ConfigKeys.ESPORT)

  /*
   * ELASTIC SEARCH
   ****************************************************************/

  /**
    * @return ElasticSearch protocol
    */
  def esProtocol: String = config.getString(ConfigKeys.ES_PROTOCOL)

  /**
    * @return ElasticSearch host
    */
  def esHost: String = config.getString(ConfigKeys.ES_HOST)

  /**
    * @return ElasticSearch port
    */
  def esPort: Int = config.getInt(ConfigKeys.ES_PORT)

  /**
    * @return ElasticSearch user
    */
  def esUser: String = config.getString(ConfigKeys.ES_PORT)

  /**
    * @return ElasticSearch password
    */
  def esPassword: String = config.getString(ConfigKeys.ES_PASSWORD)

  /**
    * @return ElasticSearch index
    */
  def esIndex: String = config.getString(ConfigKeys.ES_INDEX)

  /**
    * @return ElasticSearch default size in regards to pagination
    */
  def esDefaultSize: Long = config.getLong(ConfigKeys.ES_DEFAULT_SIZE)

}
