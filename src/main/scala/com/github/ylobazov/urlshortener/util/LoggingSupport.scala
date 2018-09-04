package com.github.ylobazov.urlshortener.util

import org.slf4j.LoggerFactory

trait LoggingSupport {
  val log = LoggerFactory.getLogger(getClass)
}
