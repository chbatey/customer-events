package info.batey.customers.stream

import akka.actor.Actor
import info.batey.customers.domain.Customers.CustomerEvent
import org.apache.spark.streaming.receiver.ActorHelper

class CustomerEventsActor() extends Actor with ActorHelper {

  override def preStart(): Unit = {
    log.info("Subscribing to CustomerEvent msgs")
    context.system.eventStream.subscribe(self, classOf[CustomerEvent])
  }

  def receive: Receive = {
    case e: CustomerEvent =>
      log.info(s"Received customer event $e")
      store(e)
  }
}
