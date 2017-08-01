package com.ubirch.avatar.core.device

import java.util.UUID

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.avatar.config.Config
import com.ubirch.avatar.model.db.device.Device
import com.ubirch.avatar.model.rest.MessageVersion
import com.ubirch.avatar.model.rest.device.DeviceDataRaw
import com.ubirch.crypto.hash.HashUtil
import com.ubirch.util.elasticsearch.client.binary.storage.{ESBulkStorage, ESSimpleStorage}
import com.ubirch.util.elasticsearch.client.util.SortUtil
import com.ubirch.util.json.{Json4sUtil, MyJsonProtocol}
import org.apache.commons.codec.binary.Hex
import org.elasticsearch.index.query.QueryBuilders
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionException, Future}

/**
  * author: cvandrei
  * since: 2016-09-30
  */
object DeviceDataRawManager
  extends MyJsonProtocol
    with StrictLogging {

  private val index = Config.esDeviceDataRawIndex
  private val esType = Config.esDeviceDataRawType

  /**
    * Query the history of deviceDataRaw for a specified device.
    *
    * @param device device for which we would like to get raw data
    * @param from   paging parameter: skip the first x elements
    * @param size   paging parameter: return up to x elements
    * @return result list (sorted by field "ts" in ascending order); empty if no messages were found
    * @throws ExecutionException       something went wrong (e.g. no document matching our query exists yet)
    * @throws IllegalArgumentException device.hwDeviceId is empty
    */
  def history(device: Device,
              from: Int = 0,
              size: Int = Config.esDefaultPageSize
             ): Future[Seq[DeviceDataRaw]] = {

    require(device.hwDeviceId.nonEmpty, "hwDeviceId may not be empty")

    val query = Some(QueryBuilders.termQuery("a", device.hwDeviceId))
    val sort = Some(SortUtil.sortBuilder("ts", asc = false))

    ESSimpleStorage.getDocs(index, esType, query, Some(from), Some(size), sort).map { res =>
      res.map(_.extract[DeviceDataRaw])
    }

  }

  /**
    * Query one raw data object
    *
    * @param id unique which identifies one raw data object
    * @return DeviceDataRaw or None
    */
  def loadById(id: UUID): Future[Option[DeviceDataRaw]] = {

    require(id != null, "raw data id may not be null")

    val query = Some(QueryBuilders.termQuery("id", id.toString))

    ESSimpleStorage.getDocs(index, esType, query).map { res =>
      res.map(_.extract[DeviceDataRaw]).headOption
    }
  }

  /**
    * Store a [[DeviceDataRaw]].
    *
    * @param data a device's raw data to store
    * @return json of what we stored
    */
  def store(data: DeviceDataRaw): Future[Option[DeviceDataRaw]] = {

    logger.debug(s"store data: $data")
    Json4sUtil.any2jvalue(data) match {

      case Some(doc) =>
        val id = data.id.toString
        ESBulkStorage.storeDocBulk(
          docIndex = index,
          docType = esType,
          docId = id,
          doc = doc
        ) map (_.extractOpt[DeviceDataRaw])

      case None => Future(None)

    }
  }

  /**
    * evil dirty hack, works just for trackle
    *
    * @param did
    * @param vals
    */
  def create(did: String, vals: Map[DateTime, Int], mpraw: Array[Byte]): Option[DeviceDataRaw] = {
    case class pval(t: Int, ts: DateTime)

    val p = vals.keySet.map { ts =>
      pval(t = vals.get(ts).get, ts = ts)
    }

    val ddr = DeviceDataRaw(
      v = MessageVersion.v40,
      did = Some(did),
      a = HashUtil.sha512Base64(did),
      mpraw = Some(Hex.encodeHexString(mpraw)),
      p = Json4sUtil.any2jvalue(p).get,
      ts = new DateTime()
    )

    Some(ddr)
  }
}
