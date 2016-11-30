package com.ubirch.avatar.model.device

import org.joda.time.DateTime

/**
  * Created by derMicha on 07/11/16.
  */

final case class DeviceShortInfos(deviceShortInfos: Set[DeviceShortInfo])

final case class DeviceInfo(
                             deviceId: String,
                             deviceName: String,
                             deviceTypeKey: String = "unknownDeviceType",
                             deviceLastUpdated: Option[DateTime],
                             inSync: Option[Boolean]
                           )