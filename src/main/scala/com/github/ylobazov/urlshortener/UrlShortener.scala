package com.github.ylobazov.urlshortener

import java.nio.charset.StandardCharsets

import com.google.common.hash.Hashing.murmur3_32

object UrlShortener {

  def encode(url: String): String = {
    murmur3_32(1010101010).hashString(url, StandardCharsets.UTF_8).toString
  }

}


//object Test extends App {
//  println(UrlShortener.encode("https://ain.ua/2015/02/02/programmirovanie-otstoj-a-programmisty-psixi-mnenie-insajdera"))
//  println(UrlShortener.encode("https://ain.ua/2015/02/02/programmirovanie-otstoj-a-programmisty-psixi-mnenie-insajdera"))
//  println(
//    UrlShortener.encode("https://www.google.com.ua/maps/place/Grid+Dynamics/@50.0134267,36.2479482,17z/data=!3m1!4b1!4m5!3m4!1s0x4127a0d0d01f526d:0xac6d1e09f3f5862e!8m2!3d50.0134267!4d36.2501369Э")
//  )
//  println(
//    UrlShortener.encode("https://www.google.com.ua/maps/place/Grid+Dynamics/@50.0134267,36.2479482,17z/data=!3m1!4b1!4m5!3m4!1s0x4127a0d0d01f526d:0xac6d1e09f3f5862e!8m2!3d50.0134267!4d36.2501369Э")
//  )
//
//}