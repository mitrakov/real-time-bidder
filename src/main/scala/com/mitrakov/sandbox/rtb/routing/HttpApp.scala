package com.mitrakov.sandbox.rtb.routing

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mitrakov.sandbox.rtb.models.BidRequest
import com.mitrakov.sandbox.rtb.broker.Broker

class HttpApp(broker: Broker) {
  def route: Route = {
    import Marshallers.{marshaller, unmarshaller}

    path("health") {
      get {
        complete(StatusCodes.OK, "ok")
      }
    } ~ path("bid-request") {
      post {
        entity(as[BidRequest]) { bidRequest =>
          broker.processRequest(bidRequest) match {
            case Some(response) => complete(StatusCodes.OK, response)
            case None => complete(StatusCodes.NoContent)
          }
        }
      }
    }
  }
}
