package com.github.ylobazov.urlshortener

import org.mongodb.scala.bson.ObjectId

package object model {

  sealed trait ActorRequest[R] {
    type Result = R
  }

  case class ShortenUriResponse(id: String)
  case class ShortenUriRequest(target: String) extends ActorRequest[ShortenUriResponse]

  case class GetShortenedUriResponse(target: String)
  case class GetShortenedUriRequest(id: String) extends ActorRequest[GetShortenedUriResponse]

  case class ShortenedUrl(_id :ObjectId, key: String, target: String)
  object ShortenedUrl{
    def apply(key: String, target: String): ShortenedUrl = new ShortenedUrl(new ObjectId(), key, target)
  }

}
