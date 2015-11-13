package info.batey.customers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import info.batey.customers.infrastructure.CustomerAccess
import info.batey.customers.web.{JsonSupport, CustomerRoute}
import org.apache.spark.sql.cassandra.CassandraSQLContext
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]) = {
    new Main()
  }
}

class Main extends CustomerRoute with JsonSupport {
  implicit val system = ActorSystem("customer-events")
  val conf = new SparkConf(true).set("spark.cassandra.connection.host", "127.0.0.1")
  val sc = new SparkContext("local[2]", "ScalaExchange", conf)
  val ssc = new CassandraSQLContext(sc)
  val keyspace = "test"
  ssc.setKeyspace(keyspace)

  val customerAccess = CustomerAccess(sc, ssc)
  implicit val materializer = ActorMaterializer()
  implicit val executionConext = system.dispatcher

  Http().bindAndHandle(route, "localhost", 8080)
}