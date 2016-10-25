package com.ubirch.avatar.core.device

import com.ubirch.avatar.config.Config
import com.ubirch.avatar.model.DeviceData
import com.ubirch.services.storage.DeviceDataStorage
import com.ubirch.util.json.MyJsonProtocol

import org.elasticsearch.index.query.QueryBuilders

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * author: cvandrei
  * since: 2016-09-30
  */
object DeviceDataManager extends MyJsonProtocol {

  def history(deviceId: String,
              from: Int = 0,
              size: Int = Config.deviceDataDbDefaultPageSize
             ): Future[Seq[DeviceData]] = {

    val query = Some(QueryBuilders.termQuery("deviceId", deviceId))
    val index = Config.deviceDataDbIndex
    val esType = deviceId

    DeviceDataStorage.getDocs(index, esType, query, Some(from), Some(size)).map { res =>
      res.map { jv =>
        jv.extract[DeviceData]
      }
    }

  }

}
