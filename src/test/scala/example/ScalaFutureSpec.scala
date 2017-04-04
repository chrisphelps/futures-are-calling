package example


import org.scalatest.{FlatSpec, Matchers}
import example.ScalaFutureRepository._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ScalaFutureSpec extends FlatSpec with Matchers {

  behavior of "Scala Future"

  it should "chain synchronous operations" in {
    getConsumer(1).map { _.name }
  }

  it should "chain asynchronous operations" in {
    getConsumer(1).flatMap(c => getPremises(c.id))
  }

  it should "chain asynchronous operations with syntax support" in {
    for {
      c <- getConsumer(1)
      ps <- getPremises(c.id)
      rs <- getMeterReadings(ps.head.id)
    } yield rs.foldRight(0)((r, acc) => acc + r.value)
  }

  it should "combine multiple futures" in {

  }

  it should "recover errors" in {
  }

  it should "take the first result" in {
  }

  it should "lift a synchronous value into the future" in {
    def myFunction(x: Int) = x + 5 + 7 + 21

    Future.successful(myFunction(5))
  }

  it should "start a new computation in the future" in {
    def myFunction(x: Int) = x + 5 + 7 + 21

    Future {
      myFunction(5)
    }
  }

  it should "lift an error into the future" in {
    Future.failed(new IllegalStateException("Something went wrong"))
  }
}
