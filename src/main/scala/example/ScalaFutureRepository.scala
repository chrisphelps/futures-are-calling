package example


import example.Domain._

import scala.concurrent.Future


object ScalaFutureRepository {

  def getConsumer(consumerId: ConsumerId): Future[Consumer] = {
    Domain.users.find(_.id == consumerId) match {
      case Some(user) => Future.successful(user)
      case None => Future.failed(new NoSuchElementException(s"Could not find ConsumerId $consumerId"))
    }
  }

  def getConsumers: Future[Seq[Consumer]] = {
    Future.successful(Domain.users)
  }

  def getPremises(consumerId: ConsumerId): Future[List[Premise]] = {
    Domain.premises.get(consumerId) match {
      case Some(p) => Future.successful(p)
      case None => Future.failed(new NoSuchElementException(s"Could not find Premises for ConsumerId $consumerId"))
    }
  }

  def getMeterReadings(premiseId: PremiseId): Future[List[MeterReading]] = {
    Domain.readings.get(premiseId) match {
      case Some(r) => Future.successful(r)
      case None => Future.failed(new NoSuchElementException(s"Could not find Readings for PremiseId $premiseId"))
    }
  }
}
