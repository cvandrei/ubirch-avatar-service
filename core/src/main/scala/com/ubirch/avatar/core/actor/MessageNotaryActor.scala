package com.ubirch.avatar.core.actor

import com.ubirch.avatar.config.Config
import com.ubirch.avatar.model.device.DeviceDataRaw
import com.ubirch.avatar.util.actor.ActorNames
import com.ubirch.notary.client.NotaryClient
import com.ubirch.util.json.Json4sUtil

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.RoundRobinPool

/**
  * author: derMicha
  * since: 2016-10-28
  */
class MessageNotaryActor extends Actor
  with ActorLogging {

  private val persistenceActor = context.actorOf(new RoundRobinPool(Config.akkaNumberOfWorkers).props(Props[MessagePersistenceActor]), ActorNames.PERSISTENCE_SVC)

  override def receive: Receive = {

    case drd: DeviceDataRaw =>

      log.debug(s"received message: $drd")
      val payloadStr = Json4sUtil.jvalue2String(drd.p)

      NotaryClient.notarize(
        blockHash = payloadStr,
        dataIsHash = false
      ) match {

        case Some(resp) =>
          val txHash = resp.hash
          log.info(s"btx hash for message ${drd.id} is $txHash")
          val anchored = drd.copy(txHash = Some(txHash))
          persistenceActor ! AnchoredRawData(anchored)

        case None => log.error(s"notarize failed for: rawData.id=${drd.id}")

      }

    case _ => log.error("received unknown message")

  }

}
