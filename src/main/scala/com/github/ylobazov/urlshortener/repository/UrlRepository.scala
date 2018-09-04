package com.github.ylobazov.urlshortener.repository

import com.github.ylobazov.urlshortener.model.ShortenedUrl
import com.github.ylobazov.urlshortener.util.LoggingSupport
import com.mongodb.client.model.IndexOptions
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.{MongoCollection, MongoDatabase, ScalaObservable}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UrlRepository(db: MongoDatabase)(implicit ec: ExecutionContext) extends LoggingSupport {

  private val collection: MongoCollection[ShortenedUrl] = db.getCollection("urls")

  ensureIndexes()

  def insert(entity: ShortenedUrl): Future[Unit] = {
    collection.insertOne(entity).head().map(_ => ())
  }

  def findOne(key: String): Future[ShortenedUrl] = {
    collection.find(equal("key", key)).first().head()
  }

  private def ensureIndexes(): Unit = {
    val idxOpts = new IndexOptions().unique(true).background(true)
    collection.createIndex(ascending("key"), idxOpts).head().onComplete {
      case Success(idx) => log.info("Index successfully created: " + idx)
      case Failure(ex) => log.error("Index creation failed with: " + ex)
    }

  }
}
