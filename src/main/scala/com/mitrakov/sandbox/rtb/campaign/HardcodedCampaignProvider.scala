package com.mitrakov.sandbox.rtb.campaign

import com.mitrakov.sandbox.rtb.models.{Banner, Campaign, Targeting}

/**
 * Campaign provider with a list of hard-coded campaigns. For testing purposes only
 */
object HardcodedCampaignProvider extends CampaignProvider {
  override def getCampaigns: List[Campaign] = List(
    Campaign(1, "USA", Targeting(List(11)), List(Banner(111, "https://example.com/111", 320, 240)), 20)
  )
}
