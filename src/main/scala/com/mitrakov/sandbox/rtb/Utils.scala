package com.mitrakov.sandbox.rtb

import java.util.UUID
import scala.util.Random

object Utils {
  def uuid: String = UUID.randomUUID().toString.replaceAll("-", "")

  def random[T](lst: List[T]): Option[T] = {
    lst match {
      case Nil => None
      case nel => Some(nel(Random.nextInt(nel.size)))
    }
  }
}
