package cn.adcc.akkapattern.clientserver.example

import akka.actor.Actor

/**
 * Created by zhangh on 13-12-19.
 */
class ServerActor extends Actor {
  def receive: Actor.Receive = {
    case msg:String => {
      println("SSSSSSSSSSSSSSSSSSSSSSSSSS")
      println(msg)
      sender ! msg+" 绑定什么了"
      }
  }
}
