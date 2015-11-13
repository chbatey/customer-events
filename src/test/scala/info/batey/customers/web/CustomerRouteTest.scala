package info.batey.customers.web

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import info.batey.customers.actors.CustomerApiActor.CustomerCount
import info.batey.customers.infrastructure.CustomerAccess.EventCount
import info.batey.customers.infrastructure.{CustomerAccess, SparkCustomerAccess}
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class CustomerRouteTest extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {

  val customerRote = new CustomerRoute {
    val customerAccess: CustomerAccess = new CustomerAccess {
      def eventCount(): Future[Long] = Future.successful(1L)
      def eventGroup(): Future[Seq[EventCount]] = Future.successful(List())
    }
    val system: ActorSystem = ActorSystem("customer-events")
  }

  "Customer events service" should {
    "do something" in {
      Get("/rdd") ~> customerRote.route ~> check {
        responseAs[String] shouldEqual "1"
      }
    }

    "do something else" in {
      Get("/sql") ~> customerRote.route ~> check {
        responseAs[List[EventCount]] shouldEqual List()
      }
    }
  }
}
