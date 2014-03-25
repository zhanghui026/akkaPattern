package monitor2

import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import scala.util.Random
import adcc.zeus.redis.RedisHelper

/**
 * @author zhangh
 * @version $Id: 2014/3/19 13:28 
 */
object RedisHelperDemo extends App {
//  val redisHelper = RedisHelper("192.168.243.217")
  val redisHelper = RedisHelper("192.168.253.128")
  def doAppendBoundListExp(){
    redisHelper.clearBoundedList("stack")
    val res = redisHelper.gainListValues("stack",4)
    println(res)

    val r1 = System.currentTimeMillis()

    val con =(1 to 3000) map { i =>
      Future{
        Thread.sleep(Random.nextInt(10))
        //      println("那里")
        redisHelper.appendToBoundedList("stack",i,1000)
      }
    }
    Future.sequence(con)
    Thread.sleep(30000)
    val length = redisHelper.gainBoundedListLength("stack")
    println("这个队列的长度是"+length)
    redisHelper.gainListValues("stack",10) foreach println
  }

  def gainTop10Max() {
    redisHelper.clearBoundedList("top10")
    val connFuture = (1 to 50).map {
      i =>
        Future {
          Thread.sleep(Random.nextInt(10))
          val y = Random.nextInt(100).toDouble

            redisHelper.appendToTopNSortedSet("top10", s"msg$i", y, 10)

          (s"msg$i",y)
        }
    }
    Future.sequence(connFuture).map(queue => queue.sortBy(e => e._2).reverse.take(10)).foreach(println(_))
    Thread.sleep(30000)
    redisHelper.gainSortedSet("top10").foreach {
      value =>
        println(value)
    }
    println(redisHelper.gainSortedSet("top10").size)
    redisHelper.clearBoundedList("top10")
  }

  def gainTop10Min() {
    redisHelper.clearBoundedList("top10Min")
    val connFuture = (1 to 110).map {
      i =>
        Future {
          Thread.sleep(Random.nextInt(10))
          val y = Random.nextInt(100).toDouble

          redisHelper.appendToTopNSortedSet("top10Min", s"msg$i", y, 10)(false)

          (s"msg$i",y)
        }
    }
    Future.sequence(connFuture).map(queue => queue.sortBy(e => e._2).take(10)).foreach(println(_))
    Thread.sleep(30000)
    redisHelper.gainSortedSet("top10Min").foreach {
      value =>
        println(value)
    }
    println(redisHelper.gainSortedSet("top10Min").size)

    redisHelper.clearBoundedList("top10Min")
  }

//  gainTop10Min()

  redisHelper.incr("now")
}
