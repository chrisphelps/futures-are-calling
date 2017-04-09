package example

import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._


object TestUtils {

  def awaitListenableFuture[T](future: ListenableFuture[T]) = {
    val p = Promise[T]()

    Futures.addCallback(future, new FutureCallback[T] {
      override def onSuccess(result: T): Unit = {
        println("Future resolved")
        p.success(result)
      }
      override def onFailure(t: Throwable): Unit = ???
    })

    Await.result(p.future, 1 second)
  }
}
