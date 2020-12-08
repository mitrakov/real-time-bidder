package com.mitrakov.sandbox.rtb.broker

import com.mitrakov.sandbox.rtb.models.{BidRequest, Device, Impression, Site, User, Banner, Campaign}

trait Validator {
  def validateUserData(campaign: Campaign, user: Option[User], device: Option[Device]): Boolean
  def validateBanners(campaign: Campaign, impressions: List[Impression]): List[Banner]
  def validateBids(campaign: Campaign, impressions: List[Impression]): Boolean
  def validateSite(campaign: Campaign, site: Site): Boolean

  /**
   * Filters campaigns list according to the rules of an implementation class
   * @return filtered list of campaigns (may be empty)
   */
  def filterCampaigns(request: BidRequest, campaigns: List[Campaign]): List[Campaign] = {
    campaigns
      .filter(campaign => validateUserData(campaign, request.user, request.device))
      .filter(campaign => validateBanners(campaign, request.imp.getOrElse(Nil)).nonEmpty)
      .filter(campaign => validateBids(campaign, request.imp.getOrElse(Nil)))
      .filter(campaign => validateSite(campaign, request.site))
  }
}
