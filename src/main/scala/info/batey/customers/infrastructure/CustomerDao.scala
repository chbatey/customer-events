package info.batey.customers.infrastructure

import info.batey.customers.domain.Customers._

import scala.concurrent.Future

/**
 * Direct DB access not via Spark
 */
trait CustomerDao {
  def customerEvents(id: CustomerId): Future[List[CustomerEvent]]
}

class CassandraCustomerDao extends CustomerDao {
  def customerEvents(id: CustomerId): Future[List[CustomerEvent]] = {
    ???
  }
}
