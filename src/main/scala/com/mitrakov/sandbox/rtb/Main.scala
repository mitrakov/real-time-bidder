package com.mitrakov.sandbox.rtb

import java.util.concurrent.Executors
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import cats.effect.{ContextShift, IO}
import com.mitrakov.sandbox.rtb.broker.{Auction, AuctionSecondPlacePrice, Broker, SimpleValidator, Validator}
import com.mitrakov.sandbox.rtb.campaign.{CampaignProvider, HardcodedCampaignProvider}
import com.mitrakov.sandbox.rtb.db.{Db, StatisticsClient}
import com.mitrakov.sandbox.rtb.routing.HttpApp
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import doobie.util.transactor.Transactor
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

// added to Wiki
object Main extends App with LazyLogging {
  implicit val system: ActorSystem = ActorSystem("rtb-system")

  val cfg = ConfigFactory.load()
  val host = cfg.getString("rtb.http.host")
  val port = cfg.getInt("rtb.http.port")

  val campaignProvider: CampaignProvider = HardcodedCampaignProvider
  val validator: Validator = new SimpleValidator()
  val auction: Auction = new AuctionSecondPlacePrice()
  val statistics: StatisticsClient[IO] = buildStatisticsClient()
  val broker: Broker = new Broker(campaignProvider, validator, auction, statistics)
  val httpApp: HttpApp = new HttpApp(broker)
  val future: Future[ServerBinding] = Http().newServerAt(host, port).bind(httpApp.route)

  sys.addShutdownHook {
    import scala.concurrent.ExecutionContext.Implicits.global // this EC should be enough for shutting down
    logger.info("Shutting down the server...")
    future.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

  logger.info(s"Realtime Bidder started on $host:$port")
  Await.result(future, Duration.Inf)

  private def buildStatisticsClient(): StatisticsClient[IO] = {
    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.fromExecutor(Executors.newCachedThreadPool()))
    val transactor: Transactor[IO] = Db.makeTransactor
    val db: Db[IO] = new Db(transactor)
    new StatisticsClient(db)
  }
}
