package com.mitrakov.sandbox.rtb.db

import cats.data.NonEmptyList
import com.mitrakov.sandbox.rtb.models.{BidRequest, Site}
import doobie.free.connection.ConnectionIO
import doobie.util.update.Update

/**
 * Statistics client that writes into "rtbstatistics" database.
 * Please note that all duplicate lookups are performed using UNIQUE constraints.
 * DDL scripts can be found in resources/schema.sql.
 */
class StatisticsClient[F[_]](db: Db[F]) {
  import doobie.implicits._
  import cats.implicits.catsSyntaxApplicativeId

  val EXCHANGE_ID = 7 // default exchange

  /**
   * Stored request data in DB, if necessary
   */
  def storeSite(request: BidRequest): F[Int] = {
    val tags = request.imp.getOrElse(Nil).map(_.tagId)

    val query = for {
      siteId <- insertSite(request.site)
      placementIds <- insertTags(siteId, tags)
      rows <- insertPlacements(siteId, placementIds)
    } yield rows

    db.run(query)
  }

  private def insertSite(site: Site): ConnectionIO[Long] = {
    sql"""INSERT INTO sites (exchange_id, exchange_site_id, domain) VALUES ($EXCHANGE_ID, ${site.id}, ${site.domain})
          ON CONFLICT (domain) DO UPDATE SET domain=EXCLUDED.domain RETURNING id""".query[Long].unique
  }

  private def insertTags(siteId: Long, tags: List[String]): ConnectionIO[List[Long]] = {
    NonEmptyList.fromList(tags) match {
      case None => List.empty[Long].pure[ConnectionIO]
      case Some(nel) =>
        val sql = s"""INSERT INTO sites_placements (site_id, tagid) VALUES ($siteId, ?)
                      ON CONFLICT (site_id, tagid) DO UPDATE SET tagid=EXCLUDED.tagid RETURNING id"""
        Update[String](sql).updateManyWithGeneratedKeys[Long]("id")(nel).compile.toList
    }
  }

  private def insertPlacements(siteId: Long, placementIds: List[Long]): ConnectionIO[Int] = {
    NonEmptyList.fromList(placementIds) match {
      case None => 0.pure[ConnectionIO]
      case Some(nel) =>
        val sql = s"""INSERT INTO segments (site_id, placement_id) VALUES ($siteId, ?)
                      ON CONFLICT (site_id, placement_id) DO UPDATE SET placement_id=EXCLUDED.placement_id"""
        Update[Long](sql).updateMany(nel)
    }
  }
}
