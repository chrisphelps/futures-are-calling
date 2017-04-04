package example


object Domain {

  type ConsumerId = Int
  type PremiseId = Int
  type ReadId = Int

  case class Consumer(id: ConsumerId, name: String, premises: Seq[Premise])
  case class Premise(id: PremiseId, address: String, readings: Seq[MeterReading])
  case class MeterReading(id: ReadId, value: Int)


  val users = List(Consumer(1, "Homer J Simpson", List()),
                   Consumer(2, "Guy 2", List()),
                   Consumer(3, "Guy 3", List()))

  val premises = Map(1 -> List(Premise(1, "742 Evergreen Terrace", List())),
                     2 -> List(Premise(2, "SomeWhere Ave", List())))

  val readings = Map(1 -> List(MeterReading(1, 234), MeterReading(2, 341), MeterReading(3, 245)),
                     2 -> List(MeterReading(1, 123), MeterReading(2, 185))
  )

}
