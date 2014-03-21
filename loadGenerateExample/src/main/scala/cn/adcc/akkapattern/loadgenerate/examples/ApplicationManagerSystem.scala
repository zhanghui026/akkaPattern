package cn.adcc.akkapattern.loadgenerate.examples

import akka.actor.{Props, ActorSystem}
import akka.routing.{RoundRobinRouter, FromConfig}

/**
 * Created by zhangh on 13-12-20.
 * 使用Route 进行Actor分配任务，增加负载能力
 */
object ApplicationManagerSystem extends App{
  //actorSystem
  val system = ActorSystem("loadGenerate")

  val numOfJobs = 20 //* 1000000

  val jobController = system.actorOf(JobControllerActor.props(numOfJobs),"jobController")

  val workActor = WorkerActor.props(jobController).withRouter(FromConfig())


  val router = system.actorOf(workActor,"loadGenerate")
//  workActor.routerConfig match {
//    case rrr:RoundRobinRouter =>
//      println("numberofInstances:"+rrr.nrOfInstances)
//  }

   def generateLoad() = {
     (0 until numOfJobs).foreach {
       i => router ! s"Job id ${i}# send"
     }
      println("all jobs send successfully")
   }

  generateLoad()
}
