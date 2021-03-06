package com.ubirch.avatar.cmd

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.avatar.client.rest.AvatarRestClient
import com.ubirch.avatar.config.Const
import com.ubirch.avatar.core.device.{DeviceManager, DeviceTypeManager}
import com.ubirch.avatar.model.{DummyDeviceDataRaw, DummyDevices}
import com.ubirch.util.json.MyJsonProtocol
import org.json4s.JValue
import org.json4s.native.Serialization.read

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by derMicha on 09/11/16.
  */
object InitData
  extends App
    with MyJsonProtocol
    with StrictLogging {

  // NOTE if true this the NotaryService will be used. it is limited by it's wallet so please be careful when activating it.
  val notaryServiceEnabled = false

  val numberOfRawMessages = 10

  DeviceTypeManager.init()


  val properties_BC: JValue = read[JValue](
    s"""
       |{
       |"${Const.BLOCKC}" : true,
       |"${Const.STOREDATA}" : true
       |}
       |""".stripMargin
  )

  val properties_NOBC: JValue = read[JValue](
    s"""
       |{
       |"${Const.BLOCKC}" : false,
       |"${Const.STOREDATA}" : true
       |}
       |""".stripMargin
  )

  val device = if (notaryServiceEnabled) {
    DummyDevices.device(
      deviceTypeKey = Const.ENVIRONMENTSENSOR,
      deviceProperties = Some(properties_BC)
    )
  } else {
    DummyDevices.device(
      deviceTypeKey = Const.ENVIRONMENTSENSOR,
      deviceProperties = Some(properties_NOBC)
    )
  }

  Await.result(DeviceManager.create(device), 5 seconds) match {
    case Some(dev) =>

      logger.info(s"created: $dev")

      Thread.sleep(5000)

      val (_, series) = DummyDeviceDataRaw.dataSeries(
        device = device,
        elementCount = numberOfRawMessages,
        intervalMillis = 1000 * 60 * 5, // 5 mins
        timestampOffset = 0
      )

      series foreach { dataRaw =>
        logger.debug("-----------------------------------------------------------------------------------------")
        try {
          val resp = AvatarRestClient.deviceUpdatePOST(dataRaw)
          // TODO migrate to AvatarSvcClientRest
          // see `AvatarSvcClientRestSpec` for example instantiating http client and materializer
          //val resp = AvatarSvcClientRest.deviceUpdatePOST(dataRaw)
          logger.debug(s"response: ${resp.body.asString}")
        }
        catch {
          case e: Exception =>
            logger.error("post failed")
        }

        Thread.sleep(500)
      }

    case None =>
      logger.error("device could not be created")
  }
}
