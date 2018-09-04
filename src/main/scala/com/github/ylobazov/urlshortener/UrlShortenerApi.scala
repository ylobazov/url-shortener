package com.github.ylobazov.urlshortener

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes.{InternalServerError, PermanentRedirect}
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout
import com.github.ylobazov.urlshortener.model._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait UrlShortenerApi extends Directives {
  self: LoggingSupport =>

  implicit val ec: ExecutionContext
  implicit val timeout: Timeout

  val urlShortenerActor: ActorRef

  val shortenUrl =
    pathSingleSlash {
      post {
        entity(as[String]) { url =>
          log.info(s"Request to shorten a url")
          complete {
            val req = ShortenUrlRequest(url)
            urlShortenerActor.ask(req).mapTo[req.Result].map(_.id)
          }
        }
      }
    }

  val getToDestination =
    path(Segment) { id =>
      get {
        onComplete {
          log.info(s"Request to get original url by id=[$id]")
          val req = GetShortenedUrlRequest(id)
          urlShortenerActor.ask(req).mapTo[req.Result].map(_.target)
        } {
          case Success(target) => redirect(Uri(target), PermanentRedirect)
          case Failure(ex) => complete((InternalServerError, s"An error occurred: ${ex.getMessage}"))
        }
      }
    }


  val urlShortenerRoute: Route = shortenUrl ~ getToDestination

}

