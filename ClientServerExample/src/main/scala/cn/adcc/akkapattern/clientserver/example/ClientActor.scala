package cn.adcc.akkapattern.clientserver.example

import akka.actor._
import akka.remote.RemoteScope

import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.Await
import akka.util.Timeout


/**
 * Created by zhangh on 13-12-19.
 */
class ClientActor extends Actor with ActorLogging {
  implicit val timeout = Timeout(4 second)
  //first option
  var serverActor = context.actorSelection("akka.tcp://ServerSys@192.168.243.9:2552/user/serverActor")
  //second option
//  val addr = Address("akka","ServerSys","127.0.0.1",2552)
//
//val serverActor2 = context.actorOf(Props[ServerActor].withDeploy(Deploy(scope = RemoteScope(addr))))
  //defined via application.conf
//  val serverActor3 = context.actorOf(Props[ServerActor],name = "remoteServerActor")

  def receive: Actor.Receive = {
    case message:String =>

      val future = (serverActor ? message).mapTo[String]
      val result = Await.result(future,5 seconds)
      log.info("从服务其接受到的消息：-> {}",result)
  }
}
