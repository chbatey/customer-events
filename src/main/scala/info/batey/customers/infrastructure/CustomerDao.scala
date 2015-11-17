package info.batey.customers.infrastructure

import com.datastax.driver.core.{ResultSet, Session}
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}
import info.batey.customers.domain.Customers._

import scala.collection.JavaConversions._
import scala.concurrent.{Future, Promise}
import scala.util.Try
import java.lang.{ Integer => JInt }

/**
 * Direct DB access not via Spark
 */
trait CustomerDao {
  def customerEvents(id: CustomerId): Future[List[CustomerEvent]]
}

class CassandraCustomerDao(session: Session, limit: Int) extends CustomerDao {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val latestEventsPs = session.prepare("select * from events where customerid = ? limit ?")

  def customerEvents(id: CustomerId): Future[List[CustomerEvent]] = {
    val response: ListenableFuture[ResultSet] = session.executeAsync(latestEventsPs.bind(id, limit: JInt))

    val promise = Promise[ResultSet]()

    Futures.addCallback(response, new FutureCallback[ResultSet] {
      def onFailure(t: Throwable): Unit = promise.failure(t)
      def onSuccess(r: ResultSet): Unit = promise.complete(Try(r))
    })

    promise.future.map(_.all()
      .map(r => CustomerEvent(r.getString("customerid"), r.getString("id"), r.getString("eventtype"), r.getString("staffid"))))
      .map(_.toList)
  }
}

object CustomerDao {
  def apply(session: Session) = {
    new CassandraCustomerDao(session, 5)
  }
}