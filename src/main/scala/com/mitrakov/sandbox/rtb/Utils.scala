package com.mitrakov.sandbox.rtb

import java.util.UUID
import com.typesafe.scalalogging.LazyLogging
import scala.util.Random

object Utils {
  def uuid: String = UUID.randomUUID().toString.replaceAll("-", "")

  def random[T](lst: List[T]): Option[T] = {
    lst match {
      case Nil => None
      case nel => Some(nel(Random.nextInt(nel.size)))
    }
  }

  implicit class LoggerOps(private val self: Boolean) extends LazyLogging {
    def logIfFalse(s: String): Boolean = { if (!self) logger.info(s); self }
  }

  implicit class LoggerUps[T](private val self: List[T]) extends LazyLogging {
    def logIfFalse(s: String): List[T] = { if (self.isEmpty) logger.info(s); self }
  }
}
