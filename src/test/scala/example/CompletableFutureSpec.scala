package example

import java.util.concurrent.CompletableFuture

import org.scalatest.{FlatSpec, Matchers}

class CompletableFutureSpec extends FlatSpec with Matchers {

  behavior of "CompletableFuture"

  it should "work like a Promise" in {
    val cf = new CompletableFuture[Int]()

    cf.thenApply[Int](i => {
      println(s"Executing apply in thread ${Thread.currentThread().getName}")
      i * 2
    }).thenApply(i => {
      println(s"Executing second apply in thread thread ${Thread.currentThread().getName}")
      i + 1
    })

    println(s"Executing test body in thread ${Thread.currentThread().getName}")

    cf.complete(41)
  }

  it should "work like async Promise" in {
    val cf = new CompletableFuture[Int]()

    val x1: CompletableFuture[Int] = cf.thenApplyAsync((i: Int) => {
      println(s"Executing apply in thread ${Thread.currentThread().getName}")
      i * 2
    })

    x1.thenApply((i: Int) => {
      println(s"Executing second apply in thread thread ${Thread.currentThread().getName}")
      i + 1
    })

    println(s"Executing test body in thread ${Thread.currentThread().getName}")

    cf.complete(41)
  }

  it should "work like really async Promise" in {
    val cf = new CompletableFuture[Int]()

    val x1: CompletableFuture[Int] = cf.thenApply((i: Int) => {
      println(s"Executing apply in thread ${Thread.currentThread().getName}")
      i * 2
    })

    x1.thenApplyAsync((i: Int) => {
      println(s"Executing second apply in thread thread ${Thread.currentThread().getName}")
      i + 1
    })

    println(s"Executing test body in thread ${Thread.currentThread().getName}")

    cf.complete(41)
  }

  it should "do multiple applies somehow" in {
    val cf = new CompletableFuture[Int]()

    val x1: CompletableFuture[Int] = cf.thenApply((i: Int) => {
      println(s"Executing apply in thread ${Thread.currentThread().getName}")
      i * 2
    })

    val x2 = x1.thenApply((i: Int) => {
      println(s"Executing second apply in thread thread ${Thread.currentThread().getName}")
      Thread.sleep(500)
      i + 1
    })

    val x3 = x1.thenApply((i: Int) => {
      println(s"Executing third apply in thread thread ${Thread.currentThread().getName}")
      i - 1
    })

    println(s"Executing test body in thread ${Thread.currentThread().getName}")

    cf.complete(41)

    Thread.sleep(100)
    println(s"Second completed: ${x2.isDone}")
    println(s"Third completed: ${x3.isDone}")
  }


  it should "pull values from a Supplier" in {
    val cf = CompletableFuture.supplyAsync(() => {
      println(s"Executing supply in thread ${Thread.currentThread().getName}")
      Thread.sleep(500)
      42
    })

    cf.thenApply((i: Int) => {
      println(s"Executing apply in thread ${Thread.currentThread().getName}")
      i + 1
    })

    println(s"Executing test body in thread ${Thread.currentThread().getName}")

    Thread.sleep(600)
  }
}
