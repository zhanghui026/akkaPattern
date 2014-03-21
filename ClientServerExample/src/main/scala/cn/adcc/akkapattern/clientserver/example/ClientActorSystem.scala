package cn.adcc.akkapattern.clientserver.example

import com.typesafe.config.ConfigFactory
import akka.actor.{Props, ActorSystem}

/**
 * Created by zhangh on 13-12-19.
 */
object ClientActorSystem {

  def main(args: Array[String]) {
    val config = ConfigFactory.load().getConfig("remotelookup")
    val system = ActorSystem("WorkerSys",config)
    val clientActor = system.actorOf(Props[ClientActor],name = "clientActor")

    clientActor ! "Hi there"

//    val serverActor = system.actorSelection("akka.tcp://ServerSys@192.168.243.9:2552/user/serverActor")
//    serverActor ! "hi"
    Thread.sleep(4000)

    system.shutdown()
  }

}
