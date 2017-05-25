package com.ubirch.avatar.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2016-09-20
  */
object Config extends ConfigBase {

  def goPipelineName: String = config.getString(ConfigKeys.GOPIPELINENAME)

  def goPipelineLabel: String = config.getString(ConfigKeys.GOPIPELINELABEL)

  def goPipelineRev: String = config.getString(ConfigKeys.GOPIPELINEREV)

  /*
   * Service
   ****************************************************************/

  /**
    * @return http protocol
    */
  def protocol: String = config.getString(ConfigKeys.HTTPPROTOCOL)

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


  def enviroment: String = config.getString(ConfigKeys.ENVIROMENT)

  /*
   * Akka
   ************************************************************************************************/

  /**
    * Default actor timeout.
    *
    * @return timeout in seconds
    */
  def actorTimeout: Int = config.getInt(ConfigKeys.ACTOR_TIMEOUT)

  def akkaNumberOfWorkers: Int = config.getInt(ConfigKeys.AKKA_NUMBER_OF_WORKERS)

  /*
   * Elasticsearch
   ************************************************************************************************/

  /**
    * @return Elasticsearch DeviceData index
    */
  def esDeviceIndex: String = config.getString(ConfigKeys.ES_DEVICE_INDEX)

  /**
    * @return Elasticsearch DeviceData type
    */
  def esDeviceType: String = config.getString(ConfigKeys.ES_DEVICE_TYPE)

  /**
    * @return Elasticsearch raw device data index
    */
  def esDeviceDataRawIndex: String = config.getString(ConfigKeys.ES_DEVICE_DATA_RAW_INDEX)

  /**
    * @return Elasticsearch raw device data type
    */
  def esDeviceDataRawType: String = config.getString(ConfigKeys.ES_DEVICE_DATA_RAW_TYPE)

  /**
    * @return Elasticsearch anchored raw device data (with txHash) index
    */
  def esDeviceDataRawAnchoredIndex: String = config.getString(ConfigKeys.ES_DEVICE_DATA_RAW_ANCHORED_INDEX)

  /**
    * @return Elasticsearch anchored raw device data (with txHash) type
    */
  def esDeviceDataRawAnchoredType: String = config.getString(ConfigKeys.ES_DEVICE_DATA_RAW_ANCHORED_TYPE)

  /**
    * @return Elasticsearch device history index
    */
  def esDeviceDataHistoryIndex: String = config.getString(ConfigKeys.ES_DEVICE_HISTORY_INDEX)

  /**
    * @return Elasticsearch device history data type
    */
  def esDeviceDataHistoryType: String = config.getString(ConfigKeys.ES_DEVICE_HISTORY_TYPE)

  /**
    * @return Elasticsearch deviceType index
    */
  def esDeviceTypeIndex: String = config.getString(ConfigKeys.ES_DEVICE_TYPE_INDEX)

  /**
    * @return Elasticsearch deviceType type
    */
  def esDeviceTypeType: String = config.getString(ConfigKeys.ES_DEVICE_TYPE_TYPE)

  /**
    * @return Elasticsearch deviceType index
    */
  def esDeviceStateIndex: String = config.getString(ConfigKeys.ES_DEVICE_STATE_INDEX)

  /**
    * @return Elasticsearch deviceType type
    */
  def esDeviceStateType: String = config.getString(ConfigKeys.ES_DEVICE_STATE_TYPE)

  /**
    * @return Elasticsearch avatarState index
    */
  def esAvatarStateIndex: String = config.getString(ConfigKeys.ES_AVATAR_STATE_INDEX)

  /**
    * @return Elasticsearch avatarState type
    */
  def esAvatarStateType: String = config.getString(ConfigKeys.ES_AVATAR_STATE_TYPE)

  /**
    * @return ElasticSearch default size in regards to pagination
    */
  def esDefaultPageSize: Int = config.getInt(ConfigKeys.ES_DEFAULT_PAGE_SIZE)

  /*
   * AWS
   ************************************************************************************************/

  /**
    * @return ElasticSearch size of large pages in regards to pagination
    */
  def esLargePageSize: Int = config.getInt(ConfigKeys.ES_LARGE_PAGE_SIZE)

  /**
    * AWS local mode defines whether app is running on a locally or at AWS
    *
    * @return boolean value
    */
  def awsLocalMode: Boolean = config.getBoolean(ConfigKeys.AWS_LOCAL_MODE)

  /**
    * AWS base MQTT topic name for all AWS IoT Things
    *
    * @return boolean value
    */
  def awsTopicsBasename: String = config.getString(ConfigKeys.AWS_TOPICS_BASENAME)

  /**
    * @return boolean value
    */
  def awsStatesDesired: String = config.getString(ConfigKeys.AWS_STATES_DESIRED)

  /**
    * @return boolean value
    */
  def awsStatesReported: String = config.getString(ConfigKeys.AWS_STATES_REPORTED)

  /**
    * @return boolean value
    */
  def awsStatesDelta: String = config.getString(ConfigKeys.AWS_STATES_DELTA)

  /**
    * @return boolean value
    */
  def awsStatesTimestamp: String = config.getString(ConfigKeys.AWS_STATES_TIMESTAMP)


  def awsAccessKey: String = config.getString(ConfigKeys.AWS_ACCESS_KEY)

  def awsSecretAccessKey: String = config.getString(ConfigKeys.AWS_SECRET_ACCESS_KEY)

  def awsSqsQueueTransformer: String = config.getString(ConfigKeys.AWS_SQS_QUEUES_TRANSFORMER)

  def awsSqsQueueTransformerOut: String = config.getString(ConfigKeys.AWS_SQS_QUEUES_TRANSFORMER_OUT)

  /*
   * ubirch REST Client
   ************************************************************************************************/

  /**
    * @return REST Client connection timeout in milliseconds
    */
  def restClientTimeoutConnect: Int = config.getInt(ConfigKeys.REST_CLIENT_TIMEOUT_CONNECT)

  /**
    * @return REST Client read timeout in milliseconds
    */
  def restClientTimeoutRead: Int = config.getInt(ConfigKeys.REST_CLIENT_TIMEOUT_READ)

  /*
   * MQTT
   ************************************************************************************************/

  def mqttBrokerUrl: String = config.getString(ConfigKeys.MQTT_BROKER_URL)

  def mqttUser: String = config.getString(ConfigKeys.MQTT_USER_KEY)

  def mqttPassword: String = config.getString(ConfigKeys.MQTT_PASSWORD_KEY)

  def mqttTopicDevicesBase: String = config.getString(ConfigKeys.MQTT_QUEUES_DEVICES_BASE)

  def mqttTopicDevicesIn: String = config.getString(ConfigKeys.MQTT_QUEUES_DEVICES_IN)

  def mqttTopicDevicesOut: String = config.getString(ConfigKeys.MQTT_QUEUES_DEVICES_OUT)

  def mqttTopicDevicesProcessed: String = config.getString(ConfigKeys.MQTT_QUEUES_DEVICES_PROCESSED)

  def mqttPublishProcessed: Boolean = config.getBoolean(ConfigKeys.MQTT_PUBLISH_PROCESSED)

}
