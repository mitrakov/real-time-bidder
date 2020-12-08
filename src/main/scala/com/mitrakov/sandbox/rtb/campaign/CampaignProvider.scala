package com.mitrakov.sandbox.rtb.campaign

import com.mitrakov.sandbox.rtb.models.Campaign

trait CampaignProvider {
  def getCampaigns: List[Campaign]
}
