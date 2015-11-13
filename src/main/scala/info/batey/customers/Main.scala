package info.batey.customers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import info.batey.customers.infrastructure.CustomerAccess
import info.batey.customers.web.{CustomerRoute, JsonSupport}
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]) {
    new Main()
  }
}

class Main extends CustomerRoute with JsonSupport {

  val configuration = Configuration(ConfigFactory.load())

  implicit val system = ActorSystem("customer-events")
  val conf = new SparkConf(true).set("spark.cassandra.connection.host", configuration.cassandraHost)
  val sc = new SparkContext(configuration.sparkMaster, "ScalaExchange", conf)
  val ssc = new CassandraSQLContext(sc)
  ssc.setKeyspace(configuration.cassandraKeyspace)

  val customerAccess = CustomerAccess(sc, ssc)
  implicit val materializer = ActorMaterializer()
  implicit val executionConext = system.dispatcher

  Http().bindAndHandle(route, configuration.httpInterface, configuration.httpPort)
}