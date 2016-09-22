package com.ubirch.avatar.backend.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.ubirch.avatar.model.Welcome
import com.ubirch.util.json.MyJsonProtocol
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-09-20
  */
trait WelcomeRoute extends MyJsonProtocol {

  val route: Route = {

    get {
      complete {
        Welcome(message = "Welcome to the ubirchChainServer")
      }
    }

  }

}
