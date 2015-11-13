package info.batey.customers.actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import info.batey.customers.actors.CustomerReportsActor.EventCountQuery
import info.batey.customers.infrastructure.CustomerAccess

class CustomerReportsActor(customerAccess: CustomerAccess) extends Actor with ActorLogging {

  import context.dispatcher

  def receive: Receive = {
    case EventCountQuery =>
      customerAccess.eventGroup() pipeTo sender()
  }
}

object CustomerReportsActor {
  case object EventCountQuery
}
