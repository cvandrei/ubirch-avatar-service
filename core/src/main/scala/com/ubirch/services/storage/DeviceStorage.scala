package com.ubirch.services.storage

import java.net.InetAddress

import com.ubirch.avatar.config.Config
import com.ubirch.util.elasticsearch.client.binary.ElasticsearchStorage

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress

/**
  * author: cvandrei
  * since: 2016-10-20
  */
object DeviceStorage extends ElasticsearchStorage {

  private val address = new InetSocketTransportAddress(InetAddress.getByName(Config.esHost), Config.esPort)

  override protected val esClient: TransportClient = TransportClient.builder()
    .build()
    .addTransportAddress(address)

}
