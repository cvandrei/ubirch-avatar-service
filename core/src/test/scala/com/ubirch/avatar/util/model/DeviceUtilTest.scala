package com.ubirch.avatar.util.model

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.avatar.model.DummyDevices
import com.ubirch.services.util.DeviceCoreUtil
import com.ubirch.util.json.MyJsonProtocol
import org.json4s.JValue
import org.json4s.native.Serialization.{read, write}
import org.scalatest.{FeatureSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by derMicha on 18/01/17.
  */
class DeviceUtilTest extends FeatureSpec
  with Matchers
  with StrictLogging
  with MyJsonProtocol {

  val device = DummyDevices.device1

  feature("DeviceUtil") {

    scenario("sign message") {

      val payload = read[JValue](
        """{"a":"b"}""".stripMargin
      )

      val (k, s) = DeviceUtil.sign(payload, device)

      val checkedD = DeviceCoreUtil.validateSignedMessage(device.hwDeviceId, k, s, payload)
      checkedD shouldBe true
    }

    scenario("sign empty message") {

      val payload = read[JValue]("")

      val (k, s) = DeviceUtil.sign(payload, device)

      val checkedD = DeviceCoreUtil.validateSignedMessage(device.hwDeviceId, k, s, payload)
      checkedD shouldBe true

    }

  }

}