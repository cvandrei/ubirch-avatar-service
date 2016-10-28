package com.ubirch.avatar.backend.route

import com.ubirch.avatar.config.Config
import com.ubirch.avatar.core.server.util.RouteConstants
import com.ubirch.avatar.core.test.util.DeviceMessageTestUtil
import com.ubirch.avatar.model.device.DeviceMessage
import com.ubirch.avatar.model.util.{ErrorFactory, ErrorResponse}
import com.ubirch.avatar.test.base.{ElasticsearchSpec, RouteSpec}
import com.ubirch.util.uuid.UUIDUtil

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

/**
  * author: cvandrei
  * since: 2016-09-30
  */
class DeviceMessageRouteSpec extends RouteSpec
  with ElasticsearchSpec {

  private val routes = (new MainRoute).myRoute

  feature(s"GET ${RouteConstants.urlDeviceHistory(":deviceId")}") {

    scenario("deviceId exists") {

      // prepare
      val elementCount = Config.esDefaultPageSize
      val dataSeries: List[DeviceMessage] = DeviceMessageTestUtil.storeSeries(elementCount).reverse
      val deviceId = dataSeries.head.deviceId

      // test
      Get(RouteConstants.urlDeviceHistory(deviceId)) ~> routes ~> check {

        // verify
        status shouldEqual OK

        responseEntity.contentType should be(`application/json`)
        val resultSeq = responseAs[Seq[DeviceMessage]]
        resultSeq.size should be(elementCount)
        for (i <- 0 until elementCount) {
          resultSeq(i) shouldEqual dataSeries(i)
        }

        verifyCORSHeader()

      }

    }

    scenario("deviceId does not exists; Elasticsearch index does not exist either") {

      Get(RouteConstants.urlDeviceHistory("1234asdf")) ~> routes ~> check {

        status shouldEqual InternalServerError
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId does not exists; Elasticsearch index exists") {

      // prepare
      DeviceMessageTestUtil.storeSeries(1)
      val deviceId = "1234asdf"

      // test
      Get(RouteConstants.urlDeviceHistory(deviceId)) ~> routes ~> check {

        // verify
        status shouldEqual BadRequest

        val expectedError = ErrorFactory.create("QueryError", s"deviceId not found: deviceId=$deviceId, from=None, size=None")
        responseEntity.contentType should be(`application/json`)
        responseAs[ErrorResponse] shouldEqual expectedError

        verifyCORSHeader()

      }

    }

  }

  feature(s"GET ${RouteConstants.urlDeviceHistory(":deviceId")}/:from") {

    scenario("deviceId exists") {

      // prepare
      val elementCount = 3
      val from = 1
      val dataSeries: List[DeviceMessage] = DeviceMessageTestUtil.storeSeries(elementCount).reverse
      val deviceId = dataSeries.head.deviceId

      // test
      Get(RouteConstants.urlDeviceHistoryFrom(deviceId, from)) ~> routes ~> check {

        // verify
        status shouldEqual OK

        responseEntity.contentType should be(`application/json`)
        val resultSeq = responseAs[Seq[DeviceMessage]]
        resultSeq.size shouldBe 2
        for (i <- 0 until 2) {
          resultSeq(i) shouldEqual dataSeries(i + from)
        }

        verifyCORSHeader()

      }

    }

    scenario("from is negative; Elasticsearch index does not exist") {

      // prepare
      val from = -1
      val deviceId = UUIDUtil.uuidStr

      // test
      Get(RouteConstants.urlDeviceHistoryFrom(deviceId, from)) ~> Route.seal(routes) ~> check {

        // verify
        status shouldEqual MethodNotAllowed
        verifyCORSHeader(exist = false)

      }

    }

    scenario("from is negative; Elasticsearch index exists") {

      // prepare
      val messageSeries = DeviceMessageTestUtil.storeSeries(1)
      val from = -1
      val deviceId = messageSeries.head.deviceId

      // test
      Get(RouteConstants.urlDeviceHistoryFrom(deviceId, from)) ~> Route.seal(routes) ~> check {

        // verify
        status shouldEqual MethodNotAllowed
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId does not exists; Elasticsearch index does not exist either") {

      Get(RouteConstants.urlDeviceHistoryFrom("1234asdf", 5)) ~> routes ~> check {

        status shouldEqual InternalServerError
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId does not exists; Elasticsearch index exists") {

      // prepare
      DeviceMessageTestUtil.storeSeries(1)
      val deviceId = "1234asdf"
      val from = 5

      // test
      Get(RouteConstants.urlDeviceHistoryFrom(deviceId, from)) ~> routes ~> check {

        // verify
        status shouldEqual BadRequest

        val expectedError = ErrorFactory.create("QueryError", s"deviceId not found: deviceId=$deviceId, from=${Some(from)}, size=None")
        responseEntity.contentType should be(`application/json`)
        responseAs[ErrorResponse] shouldEqual expectedError

        verifyCORSHeader()

      }

    }

  }

  feature(s"GET ${RouteConstants.urlDeviceHistory(":deviceId")}/:from/:size") {

    scenario("deviceId exists; from = 0; size = elementCount") {
      val elementCount = 3
      val size = 3
      historyWithFromSizeFindAll(elementCount, size)
    }

    scenario("deviceId exists; from = 0; size > elementCount") {
      val elementCount = 3
      val size = 4
      historyWithFromSizeFindAll(elementCount, size)
    }

    scenario("deviceId exists; from < 0; size > 0; Elasticsearch index does not exist") {

      // prepare
      val from = -1
      val size = 10
      val deviceId = UUIDUtil.uuidStr

      // test
      Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> Route.seal(routes) ~> check {

        // verify
        status shouldEqual MethodNotAllowed
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId exists; from < 0; size > 0; Elasticsearch index exists") {

      // prepare
      val messageSeries = DeviceMessageTestUtil.storeSeries(1)
      val from = -1
      val size = 10
      val deviceId = messageSeries.head.deviceId

      // test
      Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> Route.seal(routes) ~> check {

        // verify
        status shouldEqual MethodNotAllowed
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId exists; from = 0; size < 0; ; Elasticsearch index does not exist") {

      // prepare
      val from = 0
      val size = -1
      val deviceId = UUIDUtil.uuidStr

      // test
      Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> Route.seal(routes) ~> check {

        // verify
        status shouldEqual MethodNotAllowed
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId exists; from = 0; size < 0; ; Elasticsearch index exists") {

      // prepare
      val messageSeries = DeviceMessageTestUtil.storeSeries(1)
      val from = 0
      val size = -1
      val deviceId = messageSeries.head.deviceId

      // test
      Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> Route.seal(routes) ~> check {

        // verify
        status shouldEqual MethodNotAllowed
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId exists; from < 0; size < 0; ; Elasticsearch index does not exist") {

      // prepare
      val from = -1
      val size = -1
      val deviceId = UUIDUtil.uuidStr

      // test
      Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> Route.seal(routes) ~> check {

        // verify
        status shouldEqual MethodNotAllowed
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId exists; from < 0; size < 0; ; Elasticsearch index exists") {

      // prepare
      val messageSeries = DeviceMessageTestUtil.storeSeries(1)
      val from = -1
      val size = -1
      val deviceId = messageSeries.head.deviceId

      // test
      Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> Route.seal(routes) ~> check {

        // verify
        status shouldEqual MethodNotAllowed
        verifyCORSHeader(exist = false)

      }

    }

    scenario("deviceId does not exist; Elasticsearch index does not exist") {

      val deviceId = "1234asdf"
      val from = 5
      val size = 7

      Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> routes ~> check {

        status shouldEqual InternalServerError
        verifyCORSHeader(exist = false)

      }
    }

    scenario("deviceId does not exists; Elasticsearch index exists") {

      // prepare
      DeviceMessageTestUtil.storeSeries(1)
      val deviceId = "1234asdf"
      val from = 0
      val size = 10

      // test
      Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> routes ~> check {

        // verify
        status shouldEqual BadRequest

        val expectedError = ErrorFactory.create("QueryError", s"deviceId not found: deviceId=$deviceId, from=${Some(from)}, size=${Some(size)}")
        responseEntity.contentType should be(`application/json`)
        responseAs[ErrorResponse] shouldEqual expectedError

        verifyCORSHeader()

      }

    }

  }

  feature(s"POST ${RouteConstants.urlDeviceHistory}") {

    ignore("insert message (messageId does not exist yet)") {
      // TODO write test
    }

    ignore("update message (messageId already exists)") {
      // TODO write test
    }

  }

  private def historyWithFromSizeFindAll(elementCount: Int, size: Int) = {

    // prepare
    val from = 0
    val dataSeries: List[DeviceMessage] = DeviceMessageTestUtil.storeSeries(elementCount).reverse
    val deviceId = dataSeries.head.deviceId

    // test
    Get(RouteConstants.urlDeviceHistoryFromSize(deviceId, from, size)) ~> routes ~> check {

      // verify
      status shouldEqual OK

      responseEntity.contentType should be(`application/json`)
      val resultSeq = responseAs[Seq[DeviceMessage]]
      resultSeq.size shouldBe dataSeries.size
      for (i <- dataSeries.indices) {
        resultSeq(i) shouldEqual dataSeries(i)
      }

      verifyCORSHeader()

    }

  }

}