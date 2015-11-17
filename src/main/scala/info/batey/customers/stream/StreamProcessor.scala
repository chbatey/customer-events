package info.batey.customers.stream

import java.util.UUID

import com.datastax.driver.core.utils.UUIDs
import com.datastax.spark.connector.streaming._
import info.batey.customers.domain.Customers.{CustomerEvent, CustomerId}
import org.apache.spark.streaming.dstream.DStream

class StreamProcessor(stream: DStream[CustomerEvent]) {

  stream.print()

  val blah = stream.foreachRDD(f => f.collect().foreach(println))

  stream.map(c => CustomerEventWithId(c.customerId, UUIDs.timeBased(), c.id, c.eventType, c.staffId))
    .saveToCassandra("test", "events")
}

case class CustomerEventWithId(customerid: CustomerId, when: UUID, id: String, eventtype : String, staffid: String)

