package com.mitrakov.sandbox.rtb.broker

import com.mitrakov.sandbox.rtb.models.Campaign

case class Result(campaign: Campaign, price: Double)

trait Auction {
  /**
   * Starts auction for a list of validated campaigns
   */
  def runAuction(campaigns: List[Campaign]): Option[Result]
}
