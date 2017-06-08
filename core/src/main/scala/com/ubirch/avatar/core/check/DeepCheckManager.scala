package com.ubirch.avatar.core.check

import com.ubirch.util.model.DeepCheckResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2017-06-08
  */
object DeepCheckManager {

  /**
    * Check if we can run a simple query on the database.
    *
    * @return deep check response with _status:OK_ if ok; otherwise with _status:NOK_
    */
  def connectivityCheck(): Future[DeepCheckResponse] = {
    Future(DeepCheckResponse()) // TODO implement
  }

}
