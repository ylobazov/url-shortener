package com.github.ylobazov.urlshortener

import java.nio.charset.StandardCharsets

import akka.actor.{Actor, Props}
import com.github.ylobazov.urlshortener.model._
import com.google.common.hash.Hashing.murmur3_32

import scala.concurrent.{ExecutionContext, Future}

class UrlShortenerActor extends Actor with LoggingSupport {
  import UrlShortenerActor.storage

  import context.system

  private val config = system.extension(UrlShortenerConfig)

  override def receive: Receive = {
    case req: ActorRequest[_] => sender() ! handle(req)
    case unsupported => sender() ! new IllegalArgumentException(s"Operation unsupported: $unsupported")
  }

  def handle[R](req: ActorRequest[R]): R = {
    req match {
      case ShortenUrlRequest(target) =>
        val id = encode(target)
        storage += id -> target
        ShortenUrlResponse(id)
      case GetShortenedUrlRequest(id) =>
        val target = storage(id)
        GetShortenedUrlResponse(target)
    }
  }

  private def encode(url: String): String = {
    murmur3_32(1010101010).hashString(url, StandardCharsets.UTF_8).toString
  }

}

object UrlShortenerActor {
  import scala.collection.mutable.{HashMap => MutableHashMap}

  def props(): Props = Props(new UrlShortenerActor)
  val storage: MutableHashMap[String, String] = MutableHashMap.empty
}