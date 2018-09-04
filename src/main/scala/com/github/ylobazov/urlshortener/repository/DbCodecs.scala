package com.github.ylobazov.urlshortener.repository

import com.github.ylobazov.urlshortener.model.ShortenedUrl
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

trait DbCodecs {

  val codecRegistry = fromRegistries(fromProviders(classOf[ShortenedUrl]), DEFAULT_CODEC_REGISTRY)

}
