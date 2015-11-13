package info.batey.customers.web


import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server._
import akka.pattern._
import akka.util.Timeout
import info.batey.customers.actors.CustomerApiActor.{CustomerCount, CustomerEventCountQuery}
import info.batey.customers.actors.CustomerReportsActor.EventCountQuery
import info.batey.customers.actors.{CustomerReportsActor, CustomerApiActor}
import info.batey.customers.infrastructure.CustomerAccess
import info.batey.customers.infrastructure.CustomerAccess.EventCount
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(EventCount)
}

trait CustomerRoute extends Directives with JsonSupport {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(10.seconds)

  val system: ActorSystem
  val customerAccess: CustomerAccess

  val route =
    get {
      path("events/all/count") {
        complete {
          val requestActor = system.actorOf(Props(classOf[CustomerApiActor], customerAccess))
          (requestActor ? CustomerEventCountQuery).mapTo[CustomerCount].map(_.count.toString)
        }
      } ~
      path("events/group/count") {
        complete {
          val requestActor = system.actorOf(Props(classOf[CustomerReportsActor], customerAccess))
          (requestActor ? EventCountQuery).mapTo[Seq[EventCount]]
        }
      }
    }
}
