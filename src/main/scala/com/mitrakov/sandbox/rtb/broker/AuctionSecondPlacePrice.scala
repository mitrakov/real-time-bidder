package com.mitrakov.sandbox.rtb.broker

import com.mitrakov.sandbox.rtb.models.Campaign
import com.typesafe.scalalogging.LazyLogging

/**
 * Simple auction that takes the winner by largest bid, and set the price as a second-place bid
 */
class AuctionSecondPlacePrice extends Auction with LazyLogging {
  val PRICE_FOR_SINGLE_WINNER_COEFF = 0.9 // for single participant auctions

  def runAuction(campaigns: List[Campaign]): Option[Result] = {
    logger.info(s"Survived campaigns: ${campaigns.mkString("\n", "\n", "\n")}")
    campaigns.sortWith(_.bid > _.bid) match {
      case Nil => None
      case winner :: Nil => Some(Result(winner, winner.bid * PRICE_FOR_SINGLE_WINNER_COEFF))
      case winner :: second :: _ => Some(Result(winner, second.bid)) // set second-place bid as a price for winner
    }
  }
}
