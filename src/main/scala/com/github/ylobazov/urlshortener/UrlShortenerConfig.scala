package com.github.ylobazov.urlshortener

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

class UrlShortenerConfigExt(config: Config) extends Extension {
  val httpHost              : String  = config.getString("http.host")
  val httpPort              : Int     = config.getInt("http.port")
  val mongoUri              : String  = config.getString("mongo-uri")
  val dbName                : String  = config.getString("db-name")
}

object UrlShortenerConfig extends ExtensionId[UrlShortenerConfigExt] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): UrlShortenerConfigExt =
    new UrlShortenerConfigExt(system.settings.config.getConfig("com.github.ylobazov.urlshortener"))

  override def lookup(): ExtensionId[_ <: Extension] = UrlShortenerConfig

  override def get(system: ActorSystem): UrlShortenerConfigExt  = super.get(system)
}
