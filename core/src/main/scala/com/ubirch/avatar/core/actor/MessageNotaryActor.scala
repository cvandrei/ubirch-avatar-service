package com.ubirch.avatar.core.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.RoundRobinPool
import com.ubirch.avatar.config.Config
import com.ubirch.avatar.util.actor.ActorNames
import com.ubirch.notary.client.NotaryClient
import com.roundeights.hasher.Implicits._
import com.ubirch.avatar.model.actors.AnchoredRawData
import com.ubirch.avatar.model.rest.device.DeviceDataRaw

import scala.language.postfixOps


/**
  * author: derMicha
  * since: 2016-10-28
  */
class MessageNotaryActor extends Actor
  with ActorLogging {

  private val persistenceActor = context.actorOf(MessagePersistenceActor.props, ActorNames.PERSISTENCE_SVC)

  override def receive: Receive = {

    case drd: DeviceDataRaw =>

      log.debug(s"received message: $drd")

      //val payloadStr = Json4sUtil.jvalue2String(drd.p)

      drd.s match {

        case Some(payloadHash) =>
          NotaryClient.notarize(
            blockHash = payloadHash.md5,
            dataIsHash = false
          ) match {

            case Some(resp) =>
              val txHash = resp.hash
              val txHashLink = resp.txHashLink
              log.info(s"btx hash for message ${drd.id} is $txHash ($txHashLink)")
              val anchored = drd.copy(
                ps = Some(payloadHash.md5),
                txHash = Some(txHash),
                txHashLink = Some(txHashLink),
                txHashLinkHtml = Some(s"<a target=_blank href='$txHashLink'>$txHash</a>")
              )
              persistenceActor ! AnchoredRawData(anchored)

            case None => log.error(s"notarize failed for: rawData.id=${drd.id}")

          }
        case _ =>
          log.error("no payload hash exist")
      }

    case _ => log.error("received unknown message")

  }

}

object MessageNotaryActor {
  def props: Props = Props[MessageNotaryActor]
}