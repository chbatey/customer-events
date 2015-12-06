package info.batey.customers.stream

import java.util.UUID

import com.datastax.driver.core.utils.UUIDs
import com.datastax.spark.connector.streaming._
import info.batey.customers.Configuration
import info.batey.customers.domain.Customers.{CustomerEvent, CustomerId}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream
import StreamingContext._

class StreamProcessor(stream: DStream[CustomerEvent], config: Configuration) {

  stream.print()

  // Get the data to match the Cassandra table
  val mappedStream = stream.map(c => CustomerEventWithId(c.customerId, UUIDs.timeBased(), c.id, c.eventType, c.staffId))

  // Save raw for later batch processing
  mappedStream.saveToCassandra("test", "events")

  // Group by and then save using a Cassandra counter
  val keyValue: DStream[(String, Int)] = mappedStream.map(c => (c.eventtype, 1))
  keyValue.reduceByKey(_ + _).saveToCassandra(config.cassandraKeyspace, "event_counts")

  // join with static data

  // Stateful function to keep track of the current count with in Spark
  val runningCount: (Seq[Int], Option[Int]) => Option[Int] = (newValues, runningCount) => {
    val newCount = newValues.sum + runningCount.getOrElse(0)
    if (newCount == 0) None else Some(newCount)
  }
  keyValue.updateStateByKey(runningCount).saveToCassandra(config.cassandraKeyspace, "running_count")

  // Windowing to get the values from the last 10 Seconds moving evey 5 secons - todo TTL the data
  keyValue.reduceByKeyAndWindow((a:Int,b:Int) => a + b, Seconds(10), Seconds(5))
    .saveToCassandra(config.cassandraKeyspace, "sliding_count")

  // todo example of reduceByKeyAndWindow with inverse function

}

case class CustomerEventWithId(customerid: CustomerId, when: UUID, id: String, eventtype : String, staffid: String)

