package com.github.ylobazov.urlshortener

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.github.ylobazov.urlshortener.actor.UrlShortenerActor
import com.github.ylobazov.urlshortener.repository.{DbCodecs, UrlRepository}
import com.github.ylobazov.urlshortener.rest.UrlShortenerApi
import com.github.ylobazov.urlshortener.util.LoggingSupport
import org.mongodb.scala.MongoClient

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object Main extends App with DbCodecs with UrlShortenerApi with LoggingSupport {

  implicit val system: ActorSystem = ActorSystem("url-shortener-actor-system")
  implicit val materializer: Materializer = ActorMaterializer()

  override implicit val ec: ExecutionContext = system.dispatcher
  override implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  private val appConfig = system.extension(UrlShortenerConfig)

  private val db = MongoClient(appConfig.mongoUri).getDatabase(appConfig.dbName).withCodecRegistry(codecRegistry)
  private val urlRepo = new UrlRepository(db)

  override val urlShortenerActor = system.actorOf(UrlShortenerActor.props(urlRepo), "UrlShortenerActor")
  log.info(s"UrlShortenerActor [${urlShortenerActor.path}] is started")

  try {
    val binding = Http().bindAndHandle(urlShortenerRoute, appConfig.httpHost, appConfig.httpPort)
    log.info(s"Url Shortener HTTP service is started at [${appConfig.httpHost}] listening [${appConfig.httpPort}] port")

    sys.addShutdownHook {
      binding.flatMap(_.unbind()).onComplete { _ =>
        log.info(s"Url shortener service is stopped at [${appConfig.httpHost}]:[${appConfig.httpPort}]")
        system.terminate()
      }
    }
  } catch {
    case NonFatal(e) =>
      log.error(s"Url Shortener service start at [${appConfig.httpHost}]:[${appConfig.httpPort}] failed", e)
      system.terminate()
  }
}
