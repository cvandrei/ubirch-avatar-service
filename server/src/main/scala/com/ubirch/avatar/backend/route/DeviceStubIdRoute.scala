package com.ubirch.avatar.backend.route

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.avatar.core.device.DeviceManager
import com.ubirch.avatar.model.device.DeviceInfo
import com.ubirch.avatar.util.server.RouteConstants._
import com.ubirch.util.json.MyJsonProtocol
import com.ubirch.util.rest.akka.directives.CORSDirective
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import com.ubirch.util.http.response.ResponseUtil
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

import scala.util.{Failure, Success}

/**
  * author: cvandrei
  * since: 2016-09-21
  */
trait DeviceStubIdRoute
  extends MyJsonProtocol
    with ResponseUtil
    with CORSDirective
    with StrictLogging {

  implicit val system = ActorSystem()

  val route: Route =

  // TODO authentication

    path(stub / JavaUUID) { deviceId =>
      respondWithCORS {
        get {
          onComplete(DeviceManager.stub(deviceId)) {
            case Success(resp) =>
              resp match {
                case Some(stub: DeviceInfo) =>
                  complete(stub)
                case _ =>
                  complete(requestErrorResponse(errorType = "QueryError", errorMessage = s"deviceId not found: deviceId=$deviceId"))
              }
            case Failure(t) =>
              logger.error("device creation failed", t)
              complete(serverErrorResponse(errorType = "DeviceStubError", errorMessage = t.getMessage))
          }

        }
      }
    }
}
