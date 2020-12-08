package com.mitrakov.sandbox.rtb.broker

import com.mitrakov.sandbox.rtb.models.{BidRequest, BidResponse}
import com.mitrakov.sandbox.rtb.campaign.CampaignProvider
import com.mitrakov.sandbox.rtb.Utils.{random, uuid}

class Broker(provider: CampaignProvider, validator: Validator, auction: Auction) {
  def processRequest(request: BidRequest): Option[BidResponse] = {
    val campaigns = provider.getCampaigns
    val contenders = validator.filterCampaigns(request, campaigns)

    auction.runAuction(contenders) map { case Result(campaign, price) =>
      val banners = validator.validateBanners(campaign, request.imp.getOrElse(Nil))
      BidResponse(uuid, request.id, price, Some(campaign.id.toString), random(banners))
    }
  }
}
