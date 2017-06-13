package com.ubirch.avatar.core.avatar

import com.ubirch.avatar.config.Config
import com.ubirch.avatar.model.DummyDevices
import com.ubirch.avatar.model.db.device.AvatarState
import com.ubirch.avatar.mongo.MongoSpec
import com.ubirch.util.uuid.UUIDUtil

import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-02-27
  */
class AvatarStateManagerSpec extends MongoSpec {

  private val collection = Config.mongoCollectionAvatarState

  feature("byDeviceId()") {

    scenario("deviceId does not exist") {
      AvatarStateManager.byDeviceId(UUIDUtil.uuid) map (_ should be(None))
      mongoTestUtils.countAll(collection) map (_ shouldBe 0)
    }

    scenario("deviceId exists") {

      // prepare
      val device = DummyDevices.minimalDevice()
      val deviceId = UUIDUtil.fromString(device.deviceId)
      val avatarState = AvatarState(deviceId = deviceId)

      AvatarStateManager.create(avatarState) flatMap { created =>

        // test
        AvatarStateManager.byDeviceId(deviceId) flatMap { result =>

          // verify
          result should be(created)
          mongoTestUtils.countAll(collection) map (_ shouldBe 1)

        }

      }

    }

  }

  feature("create") {

    scenario("create is successful") {

      // prepare
      val device = DummyDevices.minimalDevice()
      val deviceId = UUIDUtil.fromString(device.deviceId)
      val avatarState = AvatarState(deviceId = deviceId)

      // test
      AvatarStateManager.create(avatarState) flatMap { result =>

        // verify
        result should be(Some(avatarState))
        AvatarStateManager.byDeviceId(deviceId) map (_ should be(Some(avatarState)))
        mongoTestUtils.countAll(collection) map (_ shouldBe 1)

      }


    }

    scenario("record with same deviceId exists -> create fails") {

      // prepare
      val device = DummyDevices.minimalDevice()
      val deviceId = UUIDUtil.fromString(device.deviceId)
      val avatarState = AvatarState(deviceId = deviceId)

      AvatarStateManager.create(avatarState) flatMap { prepareResult =>

        prepareResult should be(Some(avatarState))

        // test
        AvatarStateManager.create(avatarState) flatMap { result =>

          // verify
          result should be(None)
          mongoTestUtils.countAll(collection) map (_ shouldBe 1)

        }

      }

    }

  }

  feature("update()") {

    scenario("record does not -> update fails") {

      // prepare
      val device = DummyDevices.minimalDevice()
      val deviceId = UUIDUtil.fromString(device.deviceId)
      val avatarState = AvatarState(deviceId = deviceId)

      // test
      AvatarStateManager.update(avatarState) flatMap { result =>

        // verify
        result should be(None)
        AvatarStateManager.byDeviceId(deviceId) map (_ should be(None))
        mongoTestUtils.countAll(collection) map (_ shouldBe 0)


      }

    }

    scenario("record exists -> update succeeds") {

      // prepare
      val device = DummyDevices.minimalDevice()
      val deviceId = UUIDUtil.fromString(device.deviceId)
      val avatarState = AvatarState(deviceId = deviceId)
      AvatarStateManager.create(avatarState) flatMap { createdOpt =>

        val created = createdOpt.get
        val forUpdate = created.copy(avatarLastUpdated = Some(created.avatarLastUpdated.get.plusDays(1)))

        // test
        AvatarStateManager.update(forUpdate) flatMap { result =>

          // verify
          result should be(Some(forUpdate))
          AvatarStateManager.byDeviceId(deviceId) map (_ should be(result))
          mongoTestUtils.countAll(collection) map (_ shouldBe 1)

        }


      }

    }

  }

  feature("upsert()") {

    scenario("record does not exist -> upsert succeeds") {

      // prepare
      val device = DummyDevices.minimalDevice()
      val deviceId = UUIDUtil.fromString(device.deviceId)
      val avatarState = AvatarState(deviceId = deviceId)

      // test
      AvatarStateManager.upsert(avatarState) flatMap { result =>

        // verify
        result should be(Some(avatarState))
        AvatarStateManager.byDeviceId(deviceId) map (_ should be(Some(avatarState)))
        mongoTestUtils.countAll(collection) map (_ shouldBe 1)

      }

    }

    scenario("record exists -> upsert succeeds") {

      // prepare
      val device = DummyDevices.minimalDevice()
      val deviceId = UUIDUtil.fromString(device.deviceId)
      val avatarState = AvatarState(deviceId = deviceId)
      AvatarStateManager.upsert(avatarState) flatMap { initialUpsertOpt =>

        val initialUpsert = initialUpsertOpt.get
        initialUpsert should be(avatarState)
        val toUpdate = initialUpsert.copy(avatarLastUpdated = Some(initialUpsert.avatarLastUpdated.get.plusDays(1)))

        // test
        AvatarStateManager.upsert(toUpdate) flatMap { result =>

          // verify
          result should be(Some(toUpdate))
          AvatarStateManager.byDeviceId(deviceId) map (_ should be(Some(toUpdate)))
          mongoTestUtils.countAll(collection) map (_ shouldBe 1)

        }

      }

    }

  }

}
