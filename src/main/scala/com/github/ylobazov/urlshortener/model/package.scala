package com.github.ylobazov.urlshortener

package object model {

  sealed trait ActorRequest[R] {
    type Result = R
  }

  case class ShortenUriResponse(id: String)
  case class ShortenUriRequest(target: String) extends ActorRequest[ShortenUriResponse]

  case class GetShortenedUriResponse(target: String)
  case class GetShortenedUriRequest(id: String) extends ActorRequest[GetShortenedUriResponse]

}
