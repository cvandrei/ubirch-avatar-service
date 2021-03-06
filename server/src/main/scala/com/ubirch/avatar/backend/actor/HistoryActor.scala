package com.ubirch.avatar.backend.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.RoundRobinPool
import com.ubirch.avatar.config.Config
import com.ubirch.avatar.core.device.DeviceHistoryManager
import com.ubirch.avatar.model.actors._
import com.ubirch.avatar.model.rest.device.DeviceHistory
import com.ubirch.util.model.JsonErrorResponse

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

/**
  * author: cvandrei
  * since: 2017-02-20
  */
class HistoryActor extends Actor
  with ActorLogging {

  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {

    case byDate: HistoryByDate =>

      val sender = context.sender
      DeviceHistoryManager.byDate(byDate.deviceId, byDate.from, byDate.to) onComplete {

        case Failure(t) =>
          log.error(t, s"failed to query history by date: $byDate")
          sender ! JsonErrorResponse(errorType = "ServerError", errorMessage = "failed to query history by date")

        case Success(seq: Seq[DeviceHistory]) => sender ! HistorySeq(seq)

      }

    case before: HistoryBefore =>

      val sender = context.sender
      DeviceHistoryManager.before(before.deviceId, before.before) onComplete {

        case Failure(t) =>
          log.error(t, s"failed to query history byDate/before: $before")
          sender ! JsonErrorResponse(errorType = "ServerError", errorMessage = "failed to query history byDate/before")

        case Success(seq: Seq[DeviceHistory]) => sender ! HistorySeq(seq)

      }

    case after: HistoryAfter =>

      val sender = context.sender
      DeviceHistoryManager.after(after.deviceId, after.after) onComplete {

        case Failure(t) =>
          log.error(t, s"failed to query history byDate/after: $after")
          sender ! JsonErrorResponse(errorType = "ServerError", errorMessage = "failed to query history byDate/after")

        case Success(seq: Seq[DeviceHistory]) => sender ! HistorySeq(seq)

      }

    case day: HistoryByDay =>

      val sender = context.sender
      DeviceHistoryManager.byDay(day.deviceId, day.day) onComplete {

        case Failure(t) =>
          log.error(t, s"failed to query history byDate/day: $day")
          sender ! JsonErrorResponse(errorType = "ServerError", errorMessage = "failed to query history byDate/day")

        case Success(seq: Seq[DeviceHistory]) => sender ! HistorySeq(seq)

      }

    case _ =>
      log.error("received unknown message")
      sender ! JsonErrorResponse(errorType = "UnknownMessage", errorMessage = "unable to handle message")

  }

}

object HistoryActor {
  def props: Props = new RoundRobinPool(Config.akkaNumberOfBackendWorkers).props(Props[HistoryActor])
}