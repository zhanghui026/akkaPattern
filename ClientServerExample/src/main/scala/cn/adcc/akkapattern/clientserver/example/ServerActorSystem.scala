package cn.adcc.akkapattern.clientserver.example

import com.typesafe.config.ConfigFactory
import akka.actor.{Props, ActorSystem}

/**
 * Created by zhangh on 13-12-19.
 */
object ServerActorSystem  {


  def main(args: Array[String]) {
    val config = ConfigFactory.load().getConfig("ServerSys")

    val system = ActorSystem("ServerSys",config)

    val serverActor = system.actorOf(Props[ServerActor],name = "serverActor")

  }
}
