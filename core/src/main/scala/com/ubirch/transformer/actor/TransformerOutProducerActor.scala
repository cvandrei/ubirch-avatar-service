package com.ubirch.transformer.actor

import akka.actor.Actor
import akka.camel.Producer
import com.ubirch.avatar.config.Config

/**
  * Created by derMicha on 30/10/16.
  */
class TransformerOutProducerActor extends Actor with Producer {

  val accessKey = System.getenv().get(Config.awsAccessKey)

  val secretKey = System.getenv().get(Config.awsSecretAccessKey)

  override def endpointUri = s"aws-sqs://${Config.awsSqsQueueTransformerOut}?accessKey=$accessKey&secretKey=$secretKey&delaySeconds=10"

  override def oneway: Boolean = true

}
