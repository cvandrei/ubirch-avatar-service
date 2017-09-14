package com.ubirch.avatar.util.model

import java.security._
import java.util.Base64

import com.ubirch.avatar.model.db.device.Device
import com.ubirch.crypto.hash.HashUtil
import com.ubirch.util.json.JsonFormats

import org.json4s._
import org.json4s.native.Serialization._

import net.i2p.crypto.eddsa.spec.{EdDSANamedCurveTable, EdDSAParameterSpec}
import net.i2p.crypto.eddsa.{EdDSAEngine, KeyPairGenerator}

/**
  * author: derMicha
  * since: 2016-11-02
  */
object DeviceUtil {

  implicit val formats: Formats = JsonFormats.default

  def sign(payload: JValue, device: Device): (String, String) = {

    //TODO add private key management!!!
    val sgr: Signature = new EdDSAEngine(MessageDigest.getInstance("SHA-512"))
    val spec: EdDSAParameterSpec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512)
    val kpg: KeyPairGenerator = new KeyPairGenerator

    kpg.initialize(spec, new SecureRandom(java.util.UUID.randomUUID.toString.getBytes))

    val kp: KeyPair = kpg.generateKeyPair

    val sKey: PrivateKey = kp.getPrivate
    val pKey: PublicKey = kp.getPublic

    sgr.initSign(sKey)
    val payloadStr = write(payload)
    sgr.update(payloadStr.getBytes)
    val signature: Array[Byte] = sgr.sign

    (
      Base64.getEncoder.encodeToString(pKey.getEncoded),
      Base64.getEncoder.encodeToString(signature)
    )

  }

  def createKeyPair: (PrivateKey, PublicKey) = {

    val sgr: Signature = new EdDSAEngine(MessageDigest.getInstance("SHA-512"))
    val spec: EdDSAParameterSpec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512)
    val kpg: KeyPairGenerator = new KeyPairGenerator

    kpg.initialize(spec, new SecureRandom(java.util.UUID.randomUUID.toString.getBytes))

    val kp: KeyPair = kpg.generateKeyPair

    val sKey: PrivateKey = kp.getPrivate
    val pKey: PublicKey = kp.getPublic
    (sKey, pKey)

  }

  def deviceWithDefaults(device: Device): Device = {

    // TODO automated tests
    device.copy(
      hashedHwDeviceId = HashUtil.sha512Base64(device.hwDeviceId.toLowerCase),
      hwDeviceId = device.hwDeviceId.toLowerCase,
      deviceProperties = Some(device.deviceProperties.getOrElse(
        DeviceTypeUtil.defaultProps(device.deviceTypeKey)
      )),
      deviceConfig = Some(device.deviceConfig.getOrElse(
        DeviceTypeUtil.defaultConf(device.deviceTypeKey)
      )),
      tags = if (device.tags.isEmpty) {
        DeviceTypeUtil.defaultTags(device.deviceTypeKey)
      } else {
        device.tags
      }
    )

  }

}
