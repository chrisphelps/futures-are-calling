package example

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success


class ScalaFutureExecutionContextSpec extends FlatSpec with Matchers {

  behavior of "Scala Futures and Execution Context"

  class MyExecutionContext(name: String, underlying: ExecutionContext) extends ExecutionContext {
    override def execute(runnable: Runnable): Unit = {
      println(s"Running in MyExecutionContext $name")
      underlying.execute(runnable)
    }

    override def reportFailure(cause: Throwable): Unit = underlying.reportFailure(cause)
  }


  it should "print when it calls the execution context" in {
    val ec = implicitly[ExecutionContext]

    val myec = new MyExecutionContext("Context1", ec)

    val foo = Future {
      println("Basic print - Executing the body")
      5
    } (myec)

    Await.result(foo, 1 second)
  }

  it should "do subsequent calls in separate execution contexts" in {
    val ec = implicitly[ExecutionContext]

    val myec1 = new MyExecutionContext("Context1", ec)
    val myec2 = new MyExecutionContext("Context2", ec)

    val f = Future {
      println("Multiple EC - Executing body")
      5
    } (myec1)

    val t = f.map(i => {
      println("Multiple EC - Executing map")
      i * 2
    }) (myec2)
    .flatMap(i => {
      println("Multiple EC - Executing flatMap")
      Future.successful(i + 1)
    })(myec1)

    t.onComplete{
      case Success(v) => println(s"Multiple EC - Final value $v")
      case _ => println("Multiple EC - Failure")
    }(myec2)

    Await.result(t, 1 second)
  }


}
