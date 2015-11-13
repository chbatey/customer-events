package info.batey.customers.actors

import akka.actor.{ActorLogging, Actor}
import info.batey.customers.actors.CustomerApiActor.{QueryFailed, CustomerCount, CustomerEventCountQuery}
import info.batey.customers.infrastructure.CustomerAccess

import scala.util.Failure

class CustomerApiActor(customerAccess: CustomerAccess) extends Actor with ActorLogging {

  import context.dispatcher

  def receive: Receive = {
    case CustomerEventCountQuery =>
      log.info("Do cool spark stuff")
      val client = sender()
      customerAccess.eventCount().onComplete {
        case scala.util.Success(value: Long) =>
          client ! CustomerCount(value)
        case Failure(_) =>
          client ! QueryFailed
      }
  }
}

object CustomerApiActor {
  case object CustomerEventCountQuery
  case class CustomerCount(count: Long)
  case object QueryFailed
}

