package example

import com.twitter.util.{Await, FuturePool, Promise}
import org.scalatest.{FlatSpec, Matchers}

class TwitterCancellationSpec extends FlatSpec with Matchers {

  behavior of "Twitter Future"

  it should "cancel a running future" in {
    val pool = FuturePool.unboundedPool

    val myfuture = pool {
      for (_ <- 1 to 10) {
        println("Executing in pool")
        Thread.sleep(100)
      }
      5
    }

    myfuture.onSuccess{ _ => println("Successfully finished") }
    myfuture.onFailure{ t => println(s"Failed with ${t.getMessage}") }

    myfuture.raise(new IllegalStateException("Continuing is illegal"))

    Await.result(myfuture)
  }


  it should "ignore cancellation request" in {
    val pool = FuturePool.unboundedPool

    val promise = new Promise[Int]()

    val myfuture = pool {
      for (_ <- 1 to 10) {
        println(s"Executing in pool - cancellation: ${promise.isInterrupted}")
        Thread.sleep(100)
      }
      promise.setValue(5)
    }

    promise.onSuccess{ _ => println("Successfully finished") }
    promise.onFailure{ t => println(s"Failed with ${t.getMessage}") }

    Thread.sleep(300)
    
    promise.raise(new IllegalStateException("Continuing is illegal"))

    Await.result(myfuture)
  }


  it should "cooperate to cancel request" in {
    val pool = FuturePool.unboundedPool

    val promise = new Promise[Int]()

    val myfuture = pool {
      var cancelled = false
      for (_ <- 1 to 10) {
        if (!cancelled) {
          println(s"Executing in pool - cancellation: ${promise.isInterrupted}")
          promise.isInterrupted match {
            case Some(t) => promise.setException(t)
              cancelled = true
            case None => Thread.sleep(100)
          }
        }
      }
      if (!cancelled)
        promise.setValue(5)
    }

    promise.onSuccess{ _ => println("Successfully finished") }
    promise.onFailure{ t => println(s"Failed with ${t.getMessage}") }

    Thread.sleep(300)

    promise.raise(new IllegalStateException("Continuing is illegal"))

    Await.result(myfuture)
  }
}
