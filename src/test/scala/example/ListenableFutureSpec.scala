package example

import java.util.concurrent.{Callable, Executors}

import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture, MoreExecutors}
import org.scalatest.{FlatSpec, Matchers}
import com.google.common.base.{Function => GuavaFunction}

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._


class ListenableFutureSpec extends FlatSpec with Matchers {
  val es = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(3))

  behavior of "ListenableFuture"

  def doThing(): ListenableFuture[Int] = {
    es.submit(new Callable[Int] {
      override def call() = {
        Thread.sleep(500)
        5
      }
    })
  }

  def doAnotherThing(x: Int): ListenableFuture[Int] = {
    es.submit(() => {
      Thread.sleep(300)
      x * 2
    })
  }

  it should "support map-like semantics" in {
    val f = doThing()

    // returns a ListenableFuture but doesn't chain!
    val g: ListenableFuture[Int] = Futures.transform(f, new GuavaFunction[Int, Int]() {
      override def apply(x: Int) = {
        println(s"Transform 1 in thread ${Thread.currentThread().getName}")
        x * 2
      }
    })
    
    val h: ListenableFuture[Int] = Futures.transform(g, new GuavaFunction[Int, Int]() {
      override def apply(x: Int) = {
        println(s"Transform 2 in thread ${Thread.currentThread().getName}")
        x + 1
      }
    })


    val p = Promise[Int]()

    Futures.addCallback(h, new FutureCallback[Int] {
      override def onSuccess(result: Int): Unit = {
        println(s"onSuccess in thread ${Thread.currentThread().getName}")
        p.success(result)
      }

      override def onFailure(t: Throwable): Unit = ???
    })

    Await.result(p.future, 1 second)
  }

  it should "do map-like semantics with nicer syntax" in {
    val f = doThing()

    val g: ListenableFuture[Int] = Futures.transform(f, ((x: Int) => x * 2): GuavaFunction[Int,Int] )
    val h: ListenableFuture[Int] = Futures.transform(g, ((x: Int) => x + 1): GuavaFunction[Int, Int] )

    TestUtils.awaitListenableFuture(h)
  }

  it should "do flatMap-like semantics" in {
    val f = doThing()

    val g: ListenableFuture[Int] = Futures.transformAsync(f, (x: Int) => doAnotherThing(x))

    TestUtils.awaitListenableFuture(g)
  }

  it should "recover errors" in {
    val f = es.submit[Int](() => throw new IllegalArgumentException("Error"))

    val g: ListenableFuture[Int] = Futures.catching(f, classOf[IllegalArgumentException], (x: IllegalArgumentException) => 5)

    val p = Promise[Int]()

    Futures.addCallback(g, new FutureCallback[Int] {
      override def onSuccess(result: Int): Unit = {
        println(s"onSuccess in thread ${Thread.currentThread().getName} with result $result")
        p.success(result)
      }

      override def onFailure(t: Throwable): Unit = {
        println(s"onFailure in thread ${Thread.currentThread().getName}")
      }
    })

    Await.result(p.future, 1 second)
  }
}
