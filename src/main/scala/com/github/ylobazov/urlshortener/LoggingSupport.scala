package com.github.ylobazov.urlshortener

import org.slf4j.LoggerFactory

trait LoggingSupport {
  val log = LoggerFactory.getLogger(getClass)
}
