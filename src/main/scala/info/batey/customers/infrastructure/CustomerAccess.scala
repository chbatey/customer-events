package info.batey.customers.infrastructure

import com.datastax.spark.connector._
import com.typesafe.scalalogging.LazyLogging
import info.batey.customers.infrastructure.CustomerAccess.EventCount
import org.apache.spark.SparkContext
import org.apache.spark.sql.Row
import org.apache.spark.sql.cassandra.CassandraSQLContext

import scala.concurrent.Future

/**
 * Complex queries that rely on Spark
 */
trait CustomerAccess {
  def eventCount(): Future[Long]

  def eventGroup(): Future[Seq[EventCount]]
}

class SparkCustomerAccess(sc: SparkContext, ssc: CassandraSQLContext) extends CustomerAccess with LazyLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  def eventCount(): Future[Long] = {
    sc.cassandraTable("test", "events").countAsync()
  }

  //todo pure spark rdd job

  def eventGroup(): Future[Seq[EventCount]] = {
    logger.info("I should execute some Spark SQL")
    val rdd = ssc.sql("select event_type, count(*) as type_count from events group by event_type").rdd
    rdd.collectAsync()
      .map((rows: Seq[Row]) => rows
        .map((row: Row) => EventCount(
                              row.getAs[String]("eventtype"),
                              row.getAs[Long]("type_count"))))
  }
}

object CustomerAccess {
  def apply(sc: SparkContext, ssc: CassandraSQLContext): CustomerAccess = {
    new SparkCustomerAccess(sc, ssc)
  }

  case class EventCount(event: String, count: Long)
}
