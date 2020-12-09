package com.mitrakov.sandbox.rtb.broker

import cats.effect.IO
import com.mitrakov.sandbox.rtb.models.{BidRequest, BidResponse}
import com.mitrakov.sandbox.rtb.campaign.CampaignProvider
import com.mitrakov.sandbox.rtb.Utils.{random, uuid}
import com.mitrakov.sandbox.rtb.db.StatisticsClient
import com.typesafe.scalalogging.LazyLogging
import scala.util.Failure

/**
 * Main class that processes incoming requests
 */
class Broker(provider: CampaignProvider, validator: Validator, auction: Auction, stat: StatisticsClient[IO]) extends LazyLogging {
  /**
   * Handles incoming request; returns `Some` if the winner found, or `None` otherwise
   */
  def processRequest(request: BidRequest): Option[BidResponse] = {
    writeToStatistics(request)

    val campaigns = provider.getCampaigns
    val contenders = validator.filterCampaigns(request, campaigns)

    auction.runAuction(contenders) map { case Result(campaign, price) =>
      val banners = validator.validateBanners(campaign, request.imp.getOrElse(Nil))
      BidResponse(uuid, request.id, price, Some(campaign.id.toString), random(banners))
    }
  }

  private def writeToStatistics(request: BidRequest): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global // this EC should be enough for "onComplete"

    stat.storeSite(request).unsafeToFuture() onComplete { // in pure IO stack no need in "unsafeToFuture"
      case Failure(exception) => logger.error(s"Error in Statistics", exception)
      case _ =>
    }
  }
}
