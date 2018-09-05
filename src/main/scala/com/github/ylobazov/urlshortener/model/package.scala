package com.github.ylobazov.urlshortener

import org.mongodb.scala.bson.ObjectId

package object model {

  sealed trait ActorRequest[R] {
    type Result = R
  }

  case class ShortenUriResponse(key: String)
  case class ShortenUriRequest(target: String) extends ActorRequest[ShortenUriResponse]

  case class GetShortenedUriResponse(target: Option[String])
  case class GetShortenedUriRequest(key: String) extends ActorRequest[GetShortenedUriResponse]

  case class ShortenedUrl(_id :ObjectId, key: String, target: String)
  object ShortenedUrl{
    def apply(key: String, target: String): ShortenedUrl = new ShortenedUrl(new ObjectId(), key, target)
  }

}
