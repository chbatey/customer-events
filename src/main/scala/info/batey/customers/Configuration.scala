package info.batey.customers

import com.typesafe.config.Config

class Configuration(config: Config) {
  val sparkMaster: String       = config.getString("spark.master")
  val cassandraHost: String     = config.getString("cassandra.host")
  val cassandraKeyspace: String = config.getString("cassandra.keyspace")
  val httpInterface: String     = config.getString("http.interface")
  val httpPort: Int             = config.getInt("http.port")
}

object Configuration {
  def apply(config: Config): Configuration = new Configuration(config)
}
