package com.ubirch.avatar.core.device

import java.util.UUID

import com.typesafe.scalalogging.slf4j.LazyLogging
import com.ubirch.avatar.config.Config
import com.ubirch.avatar.model.device.{Device, DeviceDataRaw}
import com.ubirch.services.storage.DeviceDataRawStorage
import com.ubirch.util.elasticsearch.client.util.SortUtil
import com.ubirch.util.json.{Json4sUtil, MyJsonProtocol}
import com.ubirch.util.uuid.UUIDUtil
import org.elasticsearch.index.query.QueryBuilders

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionException, Future}

/**
  * author: cvandrei
  * since: 2016-09-30
  */
object DeviceDataRawManager extends MyJsonProtocol with LazyLogging {

  /**
    * Query the history of deviceDataRaw for a specified device.
    *
    * @param device device for which we would like to get raw data
    * @param from   paging parameter: skip the first x elements
    * @param size   paging parameter: return up to x elements
    * @return result list; empty if no messages were found
    * @throws ExecutionException       something went wrong (e.g. no document matching our query exists yet)
    * @throws IllegalArgumentException device.hwDeviceId is empty
    */
  def history(device: Device,
              from: Int = 0,
              size: Int = Config.esDefaultPageSize
             ): Future[Seq[DeviceDataRaw]] = {

    require(device.hwDeviceId.nonEmpty, "hwDeviceId may not be empty")

    val index = Config.esDeviceDataRawIndex
    val esType = Config.esDeviceDataRawType
    val query = Some(QueryBuilders.termQuery("a", device.hwDeviceId))
    val sort = Some(SortUtil.sortBuilder("ts", asc = false))

    DeviceDataRawStorage.getDocs(index, esType, query, Some(from), Some(size), sort).map { res =>
      res.map { jv =>
        jv.extract[DeviceDataRaw]
      }
    }

  }

  /**
    * Query one raw data object
    *
    * @param id unique which identifies one raw data object
    * @return DeviceDataRaw or None
    */
  def history(id: UUID): Future[Option[DeviceDataRaw]] = {

    require(id != null, "raw data id may not be null")

    val index = Config.esDeviceDataRawIndex
    val esType = Config.esDeviceDataRawType
    val query = Some(QueryBuilders.termQuery("id", id.toString))

    DeviceDataRawStorage.getDocs(index, esType, query).map { res =>
      res.map { jv =>
        jv.extract[DeviceDataRaw]
      }.headOption
    }
  }

  /**
    * Store a [[DeviceDataRaw]].
    *
    * @param data a device's raw data to store (messageId will be ignored)
    * @return json of what we stored
    */
  def store(data: DeviceDataRaw): Future[Option[DeviceDataRaw]] = {

    logger.debug(s"store data: $data")
    Json4sUtil.any2jvalue(data) match {

      case Some(doc) =>
        val index = Config.esDeviceDataRawIndex
        val esType = Config.esDeviceDataRawType
        val id = Some(data.id.toString)
        //TODO we should use here ES bulk client
        DeviceDataRawStorage.storeDoc(
          docIndex = index,
          docType = esType,
          docIdOpt = id,
          doc = doc
        ) map { jv =>
          Some(jv.extract[DeviceDataRaw])
        }

      case None => Future(None)

    }

  }



}
