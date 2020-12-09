package com.mitrakov.sandbox.rtb.db

import java.util.concurrent.Executors
import cats.effect.{Async, Blocker, Bracket, ContextShift}
import com.typesafe.config.ConfigFactory
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor
import scala.concurrent.ExecutionContext

class Db[F[_]](tx: Transactor[F])(implicit ev: Bracket[F, Throwable]) {
  import doobie.implicits.toConnectionIOOps
  def run[A](program: ConnectionIO[A]): F[A] = program.transact(tx)
}

object Db {
  private val cfg = ConfigFactory.load()
  private val driver = cfg.getString("rtb.db.driver")
  private val url = cfg.getString("rtb.db.url")
  private val user = cfg.getString("rtb.db.user")
  private val password = cfg.getString("rtb.db.password")

  def makeTransactor[F[_]: Async: ContextShift]: Transactor[F] = {
    val blocker = Blocker.liftExecutionContext(ExecutionContext.fromExecutor(Executors.newCachedThreadPool()))
    Transactor.fromDriverManager[F](driver, url, user, password, blocker) // use HikariTransactor in PROD environment
  }
}
