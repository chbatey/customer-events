package info.batey.customers

import akka.actor._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.datastax.driver.core.Cluster
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import info.batey.customers.actors.{CustomerApiActor, CustomerReportsActor}
import info.batey.customers.domain.Customers.CustomerEvent
import info.batey.customers.infrastructure.{CustomerAccess, CustomerDao}
import info.batey.customers.stream.{CustomerEventsActor, StreamProcessor}
import info.batey.customers.web.{CustomerRoute, JsonSupport}
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkEnv, SparkConf, SparkContext}

object Main {
  def main(args: Array[String]) {
    new Main()
  }
}

class Main extends CustomerRoute with JsonSupport with LazyLogging {

  val configuration = Configuration(ConfigFactory.load())

  // 1 - Create a Spark context
  val conf = new SparkConf(true).set("spark.cassandra.connection.host", configuration.cassandraHost)
  val sc = new SparkContext(configuration.sparkMaster, "customer-events", conf)
  val ssc = new CassandraSQLContext(sc)
  ssc.setKeyspace(configuration.cassandraKeyspace)

  // 2 - Get hold of the Spark Actor system
  implicit val system = SparkEnv.get.actorSystem

  // 3 - Create a streaming context
  val streamingContext = new StreamingContext(sc, Seconds(5))
  streamingContext.checkpoint("./data")
  val stream = streamingContext.actorStream[CustomerEvent](Props(classOf[CustomerEventsActor]), "events-stream")
  val streamProcessor = new StreamProcessor(stream, configuration)
  streamingContext.start()


  // 4 - NA
  val cluster = Cluster.builder().addContactPoint(configuration.cassandraHost).build()
  val session = cluster.connect(configuration.cassandraKeyspace)
  val customerAccess = CustomerAccess(sc, ssc)
  val customerDao = CustomerDao(session)
  val api = system.actorOf(Props(classOf[CustomerApiActor], customerAccess, customerDao))
  val reports = system.actorOf(Props(classOf[CustomerReportsActor], customerAccess))

  implicit val materializer = ActorMaterializer()
  implicit val executionConext = system.dispatcher

  logger.info(s"Binding to port $configuration.httpPort")
  Http().bindAndHandle(route, configuration.httpInterface, configuration.httpPort)
  logger.info(s"Bound")
  streamingContext.awaitTermination()
}