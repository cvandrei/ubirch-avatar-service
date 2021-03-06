package com.ubirch.avatar.client.rest.config

import com.ubirch.util.config.ConfigBase

/**
  * author: cvandrei
  * since: 2017-07-25
  */
object AvatarClientConfig extends ConfigBase {

  def userToken: Option[String] = {

    val key = AvatarClientConfigKeys.USER_TOKEN

    if (config.hasPath(key)) {
      Some(config.getString(key))
    } else {
      None
    }

  }

  def avatarBaseUrl: String = config.getString(AvatarClientConfigKeys.BASE_URL)

  /**
    * @return connection timeout in milliseconds
    */
  def timeoutConnect: Int = config.getInt(AvatarClientConfigKeys.TIMEOUT_CONNECT)

  /**
    * @return REST Client read timeout in milliseconds
    */
  def timeoutRead: Int = config.getInt(AvatarClientConfigKeys.TIMEOUT_READ)

}
