package example

import org.scalatest.{AsyncFlatSpec, Matchers}


class ScalaFutureRepositorySpec extends AsyncFlatSpec with Matchers {

  behavior of "ScalaFutureRepository"

  it should "look up multiple consumers" in {
    val result = ScalaFutureRepository.getConsumers

    result map { v => v.size should be(3)}
  }

//  it should "look up one consumer" in {
//    val result = S
//  }

}
