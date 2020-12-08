package com.mitrakov.sandbox.rtb.broker

import com.mitrakov.sandbox.rtb.models.{Device, Impression, Site, User, Banner, Campaign}
import com.typesafe.scalalogging.LazyLogging

/**
 * Simple Validator. For testing purposes only.
 *
 * 1) publisher's bid floor should be less or equal to a campaign bid; empty bid floor means that any bid acceptable
 * 2) publisher site must be included in a campaign's targeting list
 * 3) banners are compared according to just 4 combinations: w/h, wmin/hmin, wmax/hmax, or wmin/hmin/wmax/hmax
 * 4) campaign country must be equal to a customer's country (either user or device);
 *    in other validators there might be more complex logic, e.g. 2 closest countries (US, Canada) may pass filtering
 */
class SimpleValidator extends Validator with LazyLogging {
  override def validateBids(campaign: Campaign, impressions: List[Impression]): Boolean = {
    impressions.map(_.bidFloor) exists {_ match {
      case Some(bid) => bid <= campaign.bid
      case None => true // publisher's bidFloor is None => any bid acceptable
    }}
  }

  override def validateSite(campaign: Campaign, site: Site): Boolean = {
    campaign.targeting.targetedSiteIds.contains(site.id)
  }

  override def validateBanners(campaign: Campaign, impressions: List[Impression]): List[Banner] = {
    impressions flatMap {
      case Impression(_, wmin, wmax, Some(w), hmin, hmax, Some(h), _, _)             => campaign.banners.filter(banner => banner.height == h && banner.width == w)
      case Impression(_, Some(wmin), Some(wmax), w, Some(hmin), Some(hmax), h, _, _) => campaign.banners.filter(banner => banner.height <= hmax && banner.width <= wmax && banner.height >= hmin && banner.width >= wmin)
      case Impression(_, Some(wmin), wmax, w, Some(hmin), hmax, h, _, _)             => campaign.banners.filter(banner => banner.height >= hmin && banner.width >= wmin)
      case Impression(_, wmin, Some(wmax), w, hmin, Some(hmax), h, _, _)             => campaign.banners.filter(banner => banner.height <= hmax && banner.width <= wmax)
      case impression => logger.warn(s"Incorrect impression data: $impression; campaign $campaign is filtered"); Nil
    }
  }

  override def validateUserData(campaign: Campaign, user: Option[User], device: Option[Device]): Boolean = {
    val userCountry = for {
      usr <- user
      geo <- usr.geo
      country <- geo.country
    } yield country.trim.toLowerCase

    val deviceCountry = for {
      dev <- device
      geo <- dev.geo
      country <- geo.country
    } yield country.trim.toLowerCase

    val campaignCountry = campaign.country.trim.toLowerCase

    userCountry -> deviceCountry match {
      case (Some(country1), Some(country2)) => campaignCountry == country1 || campaignCountry == country2
      case (Some(country1), None) => campaignCountry == country1
      case (None, Some(country2)) => campaignCountry == country2
      case _ => false
    }
  }
}
