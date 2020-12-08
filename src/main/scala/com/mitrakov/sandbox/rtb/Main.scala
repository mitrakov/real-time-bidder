package com.mitrakov.sandbox.rtb

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import com.mitrakov.sandbox.rtb.broker.{Auction, AuctionSecondPlacePrice, Broker, SimpleValidator, Validator}
import com.mitrakov.sandbox.rtb.campaign.{CampaignProvider, HardcodedCampaignProvider}
import com.mitrakov.sandbox.rtb.routing.HttpApp
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Main extends App with LazyLogging {
  implicit val system: ActorSystem = ActorSystem("rtb-system")

  val campaignProvider: CampaignProvider = HardcodedCampaignProvider
  val validator: Validator = new SimpleValidator()
  val auction: Auction = new AuctionSecondPlacePrice()
  val broker: Broker = new Broker(campaignProvider, validator, auction)
  val httpApp: HttpApp = new HttpApp(broker)
  val future: Future[ServerBinding] = Http().newServerAt("localhost", 8080).bind(httpApp.route)

  sys.addShutdownHook {
    import scala.concurrent.ExecutionContext.Implicits.global // this EC should be enough for shutting down
    logger.info("Shutting down the server...")
    future.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

  logger.info("Realtime Bidder started...")
  Await.result(future, Duration.Inf)
}
