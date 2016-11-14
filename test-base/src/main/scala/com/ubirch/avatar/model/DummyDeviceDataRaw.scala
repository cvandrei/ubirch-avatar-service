package com.ubirch.avatar.model

import java.util.UUID

import com.ubirch.avatar.model.device.{Device, DeviceDataRaw}
import com.ubirch.crypto.hash.HashUtil
import com.ubirch.util.uuid.UUIDUtil

import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * author: cvandrei
  * since: 2016-10-25
  */
object DummyDeviceDataRaw {

  val random = new Random()

  def data(messageId: UUID = UUIDUtil.uuid,
           device: Device,
           pubKey: String = "pretend-to-be-a-public-key",
           timestamp: DateTime = DateTime.now,
           hashedPubKey: String = "pretend-to-be-a-public-key",
           payload: JValue = parse(s"""{"foo": ${random.nextInt(1000000)}, "bar": ${random.nextInt(1000000)}}""")
          ): DeviceDataRaw = {
    DeviceDataRaw(id = messageId, a = device.hwDeviceId, k = Some(pubKey), ts = timestamp, s = hashedPubKey, p = payload)
  }

  def dataSeries(messageId: Option[UUID] = None,
                 device: Device = DummyDevices.minimalDevice(),
                 pubKey: String = "pretend-to-be-a-public-key",
                 payload: JValue = parse("""{"foo": 23, "bar": 42}"""),
                 intervalMillis: Long = 1000 * 10, // 10s
                 timestampOffset: Long = -1000 * 60 * 60, // 1h
                 elementCount: Int = 5
                ): (Device, List[DeviceDataRaw]) = {

    val rawDataList: ListBuffer[DeviceDataRaw] = ListBuffer()
    val newestDateTime = DateTime.now(DateTimeZone.UTC).minus(timestampOffset)

    val hashedPubKey = pubKey match {
      case pk: String if pk.nonEmpty => HashUtil.sha256HexString(pk)
      case _ => "pretend-to-be-a-public-key"
    }

    val range = 0 until elementCount
    for (i <- range) {
      val timestamp = newestDateTime.minus(i * intervalMillis)
      val msgId = messageId match {
        case None => UUIDUtil.uuid
        case Some(m) => m
      }
      val deviceData = data(messageId = msgId, device = device, pubKey = pubKey, timestamp = timestamp, hashedPubKey = hashedPubKey, payload = payload)
      rawDataList.+=:(deviceData)
    }

    (device, rawDataList.toList)

  }

}
