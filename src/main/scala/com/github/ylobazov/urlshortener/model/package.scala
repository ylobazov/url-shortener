package com.github.ylobazov.urlshortener

package object model {

  sealed trait ActorRequest[R] {
    type Result = R
  }

  case class ShortenUrlResponse(id: String)
  case class ShortenUrlRequest(target: String) extends ActorRequest[ShortenUrlResponse]

  case class GetShortenedUrlResponse(target: String)
  case class GetShortenedUrlRequest(id: String) extends ActorRequest[GetShortenedUrlResponse]

}
