package info.batey.customers.infrastructure

import com.datastax.driver.core.{Cluster, Session}
import info.batey.customers.domain.Customers.CustomerEvent
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.concurrent.Future

//todo add cassandra unit
class CassandraCustomerDaoTest extends FunSpec with BeforeAndAfterAll
  with ScalaFutures with Matchers with BeforeAndAfter {

  var session: Session = _
  var cluster: Cluster = _

  override protected def beforeAll(): Unit = {
    cluster = Cluster.builder().addContactPoint("localhost").build()
    cluster.connect().execute("CREATE KEYSPACE IF NOT EXISTS uevents WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 }")
    session = cluster.connect("test")
    session.execute("CREATE TABLE IF NOT EXISTS events (customerId text, when timeuuid, id text, eventType text, staffid text,  PRIMARY KEY (customerid, when, id))")
  }

  override protected def afterAll(): Unit = {
    session.close()
    cluster.close()
  }

  before {
    session.execute("truncate events")
  }

  describe("Something") {
    it("should definitely do something") {

      session.execute("insert into events (customerid, when , id , eventtype , staffid ) VALUES ( 'chbatey', now(), '1', 'BUY', 'trevor' )")
      val underTest = CustomerDao(session)

      val events: Future[List[CustomerEvent]] = underTest.customerEvents("chbatey")

      whenReady(events) { result =>
        result should equal(List(CustomerEvent("chbatey", "1", "BUY", "trevor")))
      }
    }
  }

}
