package com.github.ylobazov.urlshortener.rest

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes.{InternalServerError, PermanentRedirect}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import akka.util.Timeout
import com.github.ylobazov.urlshortener.model.{GetShortenedUriRequest, ShortenUriRequest}
import com.github.ylobazov.urlshortener.util.LoggingSupport
import java.net.{URI => JUri}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

trait UrlShortenerApi extends Directives {
  self: LoggingSupport =>

  implicit val ec: ExecutionContext
  implicit val timeout: Timeout

  val urlShortenerActor: ActorRef

  val shortenUri: Route =
    pathSingleSlash {
      post {
        extractRequest { httpRequest =>
          entity(as[String]) { uri =>
            validate(Try(new JUri(uri)).isSuccess, "Provided URI is invalid: " + uri) {
              log.info(s"HTTP_REQUEST: " + httpRequest)
              log.info(s"BODY: " + uri)
              log.info(s"Request to shorten a uri: " + uri)
              complete {
                val host = extractHostAddress(httpRequest)
                val req = ShortenUriRequest(uri)
                urlShortenerActor.ask(req).mapTo[req.Result].map(resp => "http://" + host + "/" + resp.id)
              }
            }
          }
        }
      }
    }

  private def extractHostAddress(req: HttpRequest): String = {
    val auth = req.uri.authority
    auth.host.address() + ":" + auth.port
  }

  val getToDestination: Route =
    path("""[a-zA-Z0-9]{8}""".r) { id =>
      get {
        onComplete {
          log.info(s"Request to get original uri by id=[$id]")
          val req = GetShortenedUriRequest(id)
          urlShortenerActor.ask(req).mapTo[req.Result].map(res => res.target)
        } {
          case Success(target) => redirect(Uri(target), PermanentRedirect)
          case Failure(ex) => complete((InternalServerError, s"An error occurred: ${ex.getMessage}"))
        }
      }
    }

  val urlShortenerRoute: Route = shortenUri ~ getToDestination

}
