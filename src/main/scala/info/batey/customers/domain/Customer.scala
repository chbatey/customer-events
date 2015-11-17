package info.batey.customers.domain

object Customers {
  type CustomerId =  String
  case class CustomerEvent(customerId: CustomerId, id: String, eventType : String, staffId: String)
}

