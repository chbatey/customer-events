package info.batey.customers.web

import akka.actor.{ActorSystem, ActorRef}
import akka.http.scaladsl.model
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestProbe
import info.batey.customers.actors.CustomerApiActor.{CustomerEventCountQuery, CustomerCount}
import info.batey.customers.actors.CustomerReportsActor.EventCountQuery
import info.batey.customers.domain.Customers.CustomerEvent
import info.batey.customers.infrastructure.CustomerAccess.EventCount
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

class CustomerRouteTest extends WordSpec with Matchers
  with ScalatestRouteTest with JsonSupport with BeforeAndAfter {

  var apiProbe: TestProbe = _
  var reportsProbe: TestProbe = _
  var underTest: CustomerRoute = _

  before {
    apiProbe = TestProbe()
    reportsProbe = TestProbe()

    underTest = new CustomerRoute {
      val api: ActorRef = apiProbe.ref
      val reports: ActorRef = reportsProbe.ref
      val system: ActorSystem = ActorSystem("CustomerRouteTest")
    }
  }

  "Customer events service" should {
    "count all events" in {
      Get("/count") ~> underTest.route ~> check {
        apiProbe.expectMsg(CustomerEventCountQuery)
        apiProbe.reply(CustomerCount(1))
        responseAs[String] shouldEqual "1"
      }
    }

    "group and count events" in {
      Get("/group-count") ~> underTest.route ~> check {
        reportsProbe.expectMsg(EventCountQuery)
        reportsProbe.reply(List())
        responseAs[List[EventCount]] shouldEqual List()
      }
    }

    "store an event" in {
      val testProbe = TestProbe()
      underTest.system.eventStream.subscribe(testProbe.ref, classOf[CustomerEvent])

      Post("/event", HttpEntity(model.ContentTypes.`application/json`,
        """{"customerId":"chbatey", "id":"1", "eventType":"BUY", "staffId":"trevor" }""")) ~>
        underTest.route ~> check {
        responseAs[String] shouldEqual "OK"
        testProbe.expectMsg(CustomerEvent("chbatey", "1", "BUY", "trevor"))
      }
    }
    
    "retrieve events" in {
      Get("/events?customerId=chbatey") ~> underTest.route ~> check {
        responseAs[String] shouldEqual "OK"
      }
    }
  }
}
