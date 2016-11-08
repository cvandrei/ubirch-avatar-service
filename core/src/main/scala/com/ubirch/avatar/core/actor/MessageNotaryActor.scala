package com.ubirch.avatar.core.actor

import akka.actor.{Actor, ActorLogging}
import com.ubirch.avatar.model.device.DeviceDataRaw

/**
  * Created by derMicha on 28/10/16.
  */
class MessageNotaryActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case drd: DeviceDataRaw =>
      val s = sender
      log.debug(s"received message: $drd")

    case _ =>
      log.error("received unknown message")
  }

}