package example

import java.util.concurrent.Executors

import com.twitter.util.{Await, Future, FuturePool}
import org.scalatest.{FlatSpec, Matchers}

class TwitterFuturePoolSpec extends FlatSpec with Matchers {

  behavior of "TwitterFuturePool"

  it should "execute futures in the same thread" in {
    val pool = FuturePool.unboundedPool

    println(s"Test executing in thread ${Thread.currentThread().getName}")

    val f = pool {
      println(s"Executing body in thread ${Thread.currentThread().getName}")
      // without some delay, the future is already resolved and callbacks run in the scalatest thread
      Thread.sleep(500)
      5
    }

    val t = f.map{ x =>
      println(s"Executing map in thread ${Thread.currentThread().getName}")
      x + 1
    }

    t.onSuccess{ v =>
      println(s"Executing onSuccess in thread ${Thread.currentThread().getName}")
      println(s"Final value $v")
    }

    Await.result(t)
  }


  it should "use the pool of the flatMapped future" in {
    val firstPool = FuturePool.unboundedPool
    val secondPool = FuturePool(Executors.newFixedThreadPool(3))

    val f = firstPool {
      Thread.sleep(500)
      println(s"Executing body in thread ${Thread.currentThread().getName}")
      5
    }

    val g = f.flatMap { x =>
      secondPool {
        Thread.sleep(500)
        println(s"Executing flatMap body in thread ${Thread.currentThread().getName}")
        x + 1
      }
    }

    g.onSuccess { v =>
      println(s"Executing onSuccess in thread ${Thread.currentThread().getName}")
      println(s"Final value $v")
    }

    Await.result(g)
  }

  it should "use the thread of the last to finish when joining" in {
    val pool = FuturePool.unboundedPool

    val f = pool {
      Thread.sleep(700)
      println(s"Executing first future in thread ${Thread.currentThread().getName}")
      5
    }

    val g = pool {
      Thread.sleep(300)
      println(s"Executing second future in thread ${Thread.currentThread().getName}")
      7
    }

    f.onSuccess { _ => println(s"First future done on thread ${Thread.currentThread().getName}")}
    g.onSuccess { _ => println(s"Second future done on thread ${Thread.currentThread().getName}")}

    val h = Future.join(Vector(f, g))

    h.onSuccess(_ => println(s"Joined future done on thread ${Thread.currentThread().getName}"))

    Await.result(h)
  }
}
