package cn.adcc.akkapattern.loadgenerate.examples

import akka.actor.{Props, ActorRef, Actor}
import scala.concurrent.duration._

/**
 * Created by zhangh on 13-12-20.
 */
class WorkerActor(jobController:ActorRef) extends Actor{
  import context._
  def receive: Actor.Receive = {
    case _ =>
     println(self.path.name +" 开始时间："+System.currentTimeMillis())
    //每隔1s 发给jobcontroller
//     context.system.scheduler.scheduleOnce(2 second,jobController,"Done")
    Thread.sleep(2000)  //若workactor 做了很多job时会阻塞
    jobController ! "Done"
  }

}

object WorkerActor {
    def props(controller:ActorRef) = {
      Props(classOf[WorkerActor],controller)
    }
}
