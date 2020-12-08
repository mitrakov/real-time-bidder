package com.mitrakov.sandbox.rtb.routing

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import io.circe.{Decoder, Encoder}
import io.circe.syntax.EncoderOps
import io.circe.parser.decode

object Marshallers {
  implicit def marshaller[T: Encoder]: ToEntityMarshaller[T] = {
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { t =>
      HttpEntity(t.asJson.noSpaces)
    }
  }

  implicit def unmarshaller[T: Decoder]: FromEntityUnmarshaller[T] = {
    Unmarshaller.byteStringUnmarshaller map { byteStr =>
      decode[T](byteStr.utf8String).fold(throw _, identity)
    }
  }
}
