package com.github.ylobazov.urlshortener

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object Main extends App with UrlShortenerApi with LoggingSupport {

  implicit val system: ActorSystem = ActorSystem("url-shortener-actor-system")
  private val appConfig = system.extension(UrlShortenerConfig)

  //  override implicit val mongoUri: URI = loanAppConfig.mongoUri
  override implicit val ec: ExecutionContext = system.dispatcher
  override implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  //  private val applicationRepository = ApplicationMongoRepository(ExecutionContext.fromExecutor(null))

  implicit val materializer: Materializer = ActorMaterializer()

  override val urlShortenerActor = system.actorOf(UrlShortenerActor.props, "UrlShortenerActor")
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
