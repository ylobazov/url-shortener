package com.github.ylobazov.urlshortener.rest

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestKit
import akka.util.Timeout
import com.github.ylobazov.urlshortener.UrlShortenerConfig
import com.github.ylobazov.urlshortener.actor.UrlShortenerActor
import com.github.ylobazov.urlshortener.repository.{DbCodecs, UrlRepository}
import org.mongodb.scala.MongoClient

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class UrlShortenerApiIT extends BaseSpec with UrlShortenerApi with DbCodecs with ScalatestRouteTest {

  private val appConfig = system.extension(UrlShortenerConfig)
  private val db = MongoClient(appConfig.mongoUri).getDatabase("test").withCodecRegistry(codecRegistry)
  private val urlRepo = new UrlRepository(db)

  override val ec: ExecutionContext = executor

  override implicit val timeout: Timeout = Timeout(10, TimeUnit.SECONDS)

  override val urlShortenerActor = system.actorOf(UrlShortenerActor.props(urlRepo), "TestUrlShortenerActor")

  val testUrl = "https://ain.ua/2015/02/02/programmirovanie-otstoj-a-programmisty-psixi-mnenie-insajdera"
  val expectedAlias = "8d6c8e61"

  val testUrl2 = "https://www.google.com.ua/maps/place/Grid+Dynamics/@50.0134267,36.2479482,17z/data=!3m1!4b1!4m5!3m4!1s0x4127a0d0d01f526d:0xac6d1e09f3f5862e!8m2!3d50.0134267!4d36.2501369"

  "API" should "allow to generate an alias for the URL" in {
    Post("/").withEntity(HttpEntity(ContentTypes.`text/plain(UTF-8)`, testUrl2)) ~>
      urlShortenerRoute ~> check {
      handled shouldBe true
      val response = entityAs[String]
      response.split("/").takeRight(1) foreach { alias =>
        alias should not be empty
        alias shouldNot equal("00000000")
      }
    }
  }

  "API" should "always generate the same alias for the same link" in {
    for (i <- 0 until 10) {
      Post("/").withEntity(HttpEntity(ContentTypes.`text/plain(UTF-8)`, testUrl)) ~>
        urlShortenerRoute ~> check {
        handled shouldBe true
        val response = entityAs[String]
        response.split("/").takeRight(1).foreach { alias =>
          alias should equal(expectedAlias)
        }
      }
    }
  }

  "API" should "redirect to the original URL" in {
    Get(s"/$expectedAlias") ~>
      urlShortenerRoute ~> check {
      status shouldBe StatusCodes.PermanentRedirect
    }
  }

  "API" should "return NotFound for non existing alias" in {
    Get(s"/00000000") ~>
      urlShortenerRoute ~> check {
      status shouldBe StatusCodes.NotFound
    }
  }

  override protected def beforeAll(): Unit = {
    db.drop()

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

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

}
