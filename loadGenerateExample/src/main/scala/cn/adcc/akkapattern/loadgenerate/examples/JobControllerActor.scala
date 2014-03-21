package cn.adcc.akkapattern.loadgenerate.examples

import akka.actor.{Props, Actor}



/**
 * Created by zhangh on 13-12-20.
 */

object JobControllerActor {

  def props(numOfMsg:Int):Props = Props(classOf[JobControllerActor],numOfMsg)
}
class JobControllerActor(numOfMsg:Int = 0) extends Actor{

  var count = 0
  val startTime = System.currentTimeMillis()

  /**
   *
   * @return
   */
  def receive: Actor.Receive = {
    case "Done" => {
      println("收到时间 "+sender.path.name +":"+System.currentTimeMillis())
      count = count +1
      if(count == numOfMsg) {
        val now = System.currentTimeMillis()
        println(s"All messages processed in ${(now-startTime )/ 1000} seconds")
        println(s"Total num of msgs processed ${count}")
        context.system.shutdown()
      }
    }
    case _ => println("sss")
  }

}
