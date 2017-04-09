package example

import java.util.concurrent.Executors

import com.gilt.gfc.guava.future.GuavaFutures
import com.google.common.util.concurrent.ListenableFuture
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future
import com.gilt.gfc.guava.future.FutureConverters._

import scala.concurrent.ExecutionContext.Implicits.global


class GfcGuavaListenableFutureSpec extends FlatSpec with Matchers {
  implicit val es = Executors.newFixedThreadPool(3)

  behavior of "gfc-guava library"

  it should "create ListenableFuture" in {
    val f = GuavaFutures.future {
      Thread.sleep(500)
      5
    }

    TestUtils.awaitListenableFuture(f)
  }

  it should "convert Scala future to ListenableFuture" in {
    val f = Future {
      Thread.sleep(300)
      5
    }

    val lf = f.asListenableFuture

    TestUtils.awaitListenableFuture(lf)
  }

  it should "map and flatMap" in {
    val future = GuavaFutures.future {
      Thread.sleep(500)
      "a string"
    }

    import com.gilt.gfc.guava.future.GuavaFutures._

    // Transform using map
    val f2: ListenableFuture[Int] = future.map(s => s.length)

    // Transform using flatMap
    val f3: ListenableFuture[Int] = f2.flatMap{ (x) => Future { Thread.sleep(200); x + 3}.asListenableFuture }

    TestUtils.awaitListenableFuture(f3)
  }
}
