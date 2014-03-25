package monitor2

import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import scala.util.Random

/**
 * @author zhangh
 * @version $Id: 2014/3/19 13:28 
 */
object RedisHelperDemo extends App {
  RedisHelper.clearBoundedList("stack")
  val res = RedisHelper.gainListValues("stack",4)
  println(res)

  val r1 = System.currentTimeMillis()

  val con =(1 to 3000) map { i =>
    Future{
      Thread.sleep(Random.nextInt(10))
//      println("那里")
      RedisHelper.appendToBoundedList("stack",i,1000)
    }
  }
  Future.sequence(con)


//  val r2 = System.currentTimeMillis()
//  println("time:"+(r2-r1))
//
//  //RedisHelper.appendToBoundedList("stack","g",4)
//  //RedisHelper.appendToBoundedList("stack","l",4)
//  //RedisHelper.appendToBoundedList("stack","m",4)
//  //RedisHelper.appendToBoundedList("stack","n",4)
//  //
//  //RedisHelper.appendToBoundedList("stack","a",4)
//  //RedisHelper.appendToBoundedList("stack","b",4)
//  //RedisHelper.appendToBoundedList("stack","c",4)
//  //RedisHelper.appendToBoundedList("stack","d",4)
//  RedisHelper.gainBoundedListLength("stack")
//  val r3 = System.currentTimeMillis()
//  println(r3-r2)
//  RedisHelper.gainListValues("stack",10) foreach(x => println("value"+x))
//
//
//  val r4 = System.currentTimeMillis()
//  println(r4- r3)
  Thread.sleep(30000)
  val length = RedisHelper.gainBoundedListLength("stack")
  println("这个队列的长度是"+length)
  RedisHelper.gainListValues("stack",10) foreach println

}
