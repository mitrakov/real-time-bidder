package com.mitrakov.sandbox.rtb

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

package object models {
  case class BidRequest(id: String, imp: Option[List[Impression]], site: Site, user: Option[User], device: Option[Device])
  case class BidResponse(id: String, bidRequestId: String, price: Double, adId: Option[String], banner: Option[Banner])

  case class Campaign(id: Int, country: String, targeting: Targeting, banners: List[Banner], bid: Double)
  case class Targeting(targetedSiteIds: Set[Int]) // we use HashSet for quick lookups
  case class Banner(id: Int, src: String, width: Int, height: Int)
  case class Site(id: Int, domain: String)
  case class User(id: String, geo: Option[Geo])
  case class Device(id: String, geo: Option[Geo])
  case class Geo(country: Option[String])
  case class Impression(id: String, wmin: Option[Int], wmax: Option[Int], w: Option[Int], hmin: Option[Int],
                        hmax: Option[Int], h: Option[Int], bidFloor: Option[Double], tagId: String) {
    require(List(wmin, wmax, w).flatten.nonEmpty)
    require(List(hmin, hmax, h).flatten.nonEmpty)
  }

  // circe decoders
  implicit val siteDecoder: Decoder[Site] = deriveDecoder
  implicit val geoDecoder: Decoder[Geo] = deriveDecoder
  implicit val userDecoder: Decoder[User] = deriveDecoder
  implicit val deviceDecoder: Decoder[Device] = deriveDecoder
  implicit val impressionDecoder: Decoder[Impression] = deriveDecoder
  implicit val bidRequestDecoder: Decoder[BidRequest] = deriveDecoder

  // circe encoders
  implicit val bannerEncoder: Encoder[Banner] = deriveEncoder
  implicit val bidResponseEncoder: Encoder[BidResponse] = deriveEncoder
}
