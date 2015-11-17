package info.batey.customers.web


import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server._
import akka.pattern._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import info.batey.customers.actors.CustomerApiActor.{CustomerCount, CustomerEventCountQuery}
import info.batey.customers.actors.CustomerReportsActor.EventCountQuery
import info.batey.customers.domain.Customers.CustomerEvent
import info.batey.customers.infrastructure.CustomerAccess.EventCount
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val eventCount = jsonFormat2(EventCount)
  implicit val customerEvent = jsonFormat4(CustomerEvent)
}

trait CustomerRoute extends Directives with JsonSupport with LazyLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(10000.millis)

  val system: ActorSystem
  val api: ActorRef
  val reports: ActorRef

  val route = get {
    path("count") {
      complete {
        (api ? CustomerEventCountQuery).mapTo[CustomerCount].map(_.count.toString)
      }
    } ~
      path("group-count") {
        complete {
          (reports ? EventCountQuery).mapTo[Seq[EventCount]]
        }
      } ~
    path ("events") {
      complete("OK")
    }
  } ~
    post {
      path("event") {
        entity(as[CustomerEvent]) { event =>
          logger.info("publishing event " + event)
          system.eventStream.publish(event)
          complete("OK")
        }
      }
    }
}
