package info.batey.customers.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import info.batey.customers.actors.CustomerReportsActor.EventCountQuery
import info.batey.customers.infrastructure.CustomerAccess
import info.batey.customers.infrastructure.CustomerAccess.EventCount
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.Future

class CustomerReportsActorTest extends TestKit(ActorSystem("CustomerApiActorTest"))
  with FunSpecLike with Matchers with ImplicitSender {

  describe("CustomerReports") {
    it("should do return the count") {
      val events = List(EventCount("event", 1L))
      val customerAccess = new CustomerAccess {
        def eventCount(): Future[Long] = Future.successful(1L)
        def eventGroup(): Future[Seq[EventCount]] = Future.successful(events)
      }
      val underTest = TestActorRef(Props(classOf[CustomerReportsActor], customerAccess))

      underTest ! EventCountQuery

      expectMsg(events)
    }
  }
}
