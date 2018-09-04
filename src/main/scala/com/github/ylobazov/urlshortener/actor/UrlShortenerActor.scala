package com.github.ylobazov.urlshortener.actor

import java.nio.charset.StandardCharsets

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.github.ylobazov.urlshortener.UrlShortenerConfig
import com.github.ylobazov.urlshortener.model._
import com.github.ylobazov.urlshortener.repository.UrlRepository
import com.github.ylobazov.urlshortener.util.LoggingSupport
import com.google.common.hash.Hashing.murmur3_32

import scala.concurrent.{ExecutionContext, Future}

class UrlShortenerActor(urlRepo: UrlRepository) extends Actor with LoggingSupport {

  import context.system

  implicit val ec: ExecutionContext = system.dispatcher

  private val config = system.extension(UrlShortenerConfig)

  override def receive: Receive = {
    case req: ActorRequest[_] => handle(req).pipeTo(sender)
    case unsupported => sender() ! new IllegalArgumentException(s"Operation unsupported: $unsupported")
  }

  def handle[R](req: ActorRequest[R]): Future[R] = {
    req match {
      case ShortenUriRequest(target) =>
        log.info("ShortenUriRequest: " + target)
        val key = encode(target)

        /*The most dangerous place. Collisions are possible. made to meet the following requirement:
         4. Additionally, if a URL has already been shortened by the system,
         and it is entered a second time, the first shortened URL should be given back to the user.
        */
        urlRepo.findOne(key).fallbackTo {
          urlRepo.insert(ShortenedUrl(key, target))
        }.map(_ => ShortenUriResponse(key))

      case GetShortenedUriRequest(key) =>
        log.info("GetShortenedUriRequest: " + key)
        urlRepo.findOne(key).map(res => GetShortenedUriResponse(res.target))
    }
  }

  //Approach with hash function was chosen in order to be able to always generate the same KEY for the same URI.
  private def encode(url: String): String = {
    murmur3_32().hashString(url, StandardCharsets.UTF_8).toString
  }

}

object UrlShortenerActor {
  def props(urlRepository: UrlRepository): Props = Props(new UrlShortenerActor(urlRepository))
}