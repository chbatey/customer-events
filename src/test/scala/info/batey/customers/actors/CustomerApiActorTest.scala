package info.batey.customers.actors

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, ImplicitSender}
import info.batey.customers.actors.CustomerApiActor.{QueryFailed, CustomerCount, CustomerEventCountQuery}
import info.batey.customers.domain.Customers.{CustomerEvent, CustomerId}
import info.batey.customers.infrastructure.{CustomerDao, CustomerAccess}
import info.batey.customers.infrastructure.CustomerAccess.EventCount
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.Future

class CustomerApiActorTest extends TestKit(ActorSystem("CustomerApiActorTest"))
  with FunSpecLike with Matchers with ImplicitSender {

  val customerDao = new CustomerDao {
    def customerEvents(id: CustomerId): Future[List[CustomerEvent]] = Future.successful(List())
  }

    describe("CountCustomerEvents") {
        it("should do return the count") {
          val customerAccess = new CustomerAccess {
            def eventCount(): Future[Long] = Future.successful(1L)
            def eventGroup(): Future[Seq[EventCount]] = Future.successful(List())
          }
          val actorRef = TestActorRef(Props(classOf[CustomerApiActor], customerAccess, customerDao))

          actorRef ! CustomerEventCountQuery

          expectMsg(CustomerCount(1L))
        }


      it("should send QueryFail if fails") {
        val customerAccess = new CustomerAccess {
          def eventCount(): Future[Long] = Future.failed(new RuntimeException("Oh Dear"))
          def eventGroup(): Future[Seq[EventCount]] = Future.successful(List())
        }
        val actorRef = TestActorRef(Props(classOf[CustomerApiActor], customerAccess, customerDao))

        actorRef ! CustomerEventCountQuery

        expectMsg(QueryFailed)
      }
    }
}
