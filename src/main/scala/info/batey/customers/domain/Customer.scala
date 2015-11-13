package info.batey.customers.domain

import java.util.UUID

object Customers {
  type CustomerId =  String
  case class CustomerEvent(id: CustomerId, uuid: UUID, eventType: String, staff: String)
}

