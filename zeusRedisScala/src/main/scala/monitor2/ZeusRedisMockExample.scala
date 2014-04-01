package monitor2

import ZeusRedisMetricCenter._
import scala.util.Random

/**
 * @author zhangh
 * @version $Id: 2014/3/24 16:54 
 */
object ZeusRedisMockExample extends App{

  ZeusRedisMetricCenter.redis.keys foreach {
    s =>
      ZeusRedisMetricCenter.redis.del(s)
  }
  //结点1启动
  ZeusRedisMetricCenter.startRecording()

  println("node1 start,Thread sleep 5 second,node 2 start")
  Thread.sleep(5000)

  //结点2启动
  val node2 = "192.168.243.187"
  ZeusRedisMetricCenter.startRecording(node2)
  //
  println("node2 start right")

  //模拟上
  (1 to 100).map {
    i => {
      val i =  Random.nextInt(5)
      val msg = Random.alphanumeric.take(5+i).mkString

      // 记录msg1进入时间
      ZeusRedisMetricCenter.recordMsgIn(msg,System.currentTimeMillis())
      val milSec = Random.nextInt(10)
      // 记录msg1出时间
      ZeusRedisMetricCenter.recordMsgOut(msg,System.currentTimeMillis()+milSec)
      (msg,milSec)
    }
  }


  printSysRunningInfo()
  printRunningNodeInfo(name)
  printRunningNodeInfo(node2)
  def printSysRunningInfo(){
    //结点信息
    println("redis keys\n")
    ZeusRedisMetricCenter.allKeyTypeSet() foreach println

    println("sys startTime:" + ZeusRedisMetricCenter.gainSystemStartTime())
    println("sys running nodes:" + ZeusRedisMetricCenter.gainRunningNodeNames())
    println("sys all nodes:" + ZeusRedisMetricCenter.gainAllNodeNames())
    println("sys running second from now:" + ZeusRedisMetricCenter.gainSysSecsFromNow() + " ms")
    println("node1 startTime:"+ZeusRedisMetricCenter.gainNodeStartTime())
    println("node2 startTime:"+ZeusRedisMetricCenter.gainNodeStartTime(node2))
    //system的吞吐量

    val res = ZeusRedisMetricCenter.gainThroughoutNow
    println(res)
  }

  def printRunningNodeInfo(node:String){
    println("Running node:"+node)
    println("gain now time running node throughout")

    println("node startTime:" + ZeusRedisMetricCenter.gainNodeStartTime(node))


    println("node running second from now:" + ZeusRedisMetricCenter.gainNodeSecsFromNow(node) + " ms")
    //system的吞吐量

    val res = ZeusRedisMetricCenter.gainNodeThroughoutNow(node)
    println(res)
    //结点的吞吐量
  }

  Thread.sleep(5000)
  println("结点关闭模拟，结点1关闭，结点2开启")
  //结点进行关闭模拟
  //结点1关闭，结点2保留
  ZeusRedisMetricCenter.endRecording()

  printSysRunningInfo()
  printRunningNodeInfo(name)
  printRunningNodeInfo(node2)
  Thread.sleep(5000)
  println("结点1，结点2都关闭")
  ZeusRedisMetricCenter.endRecording(node2)
  printSysRunningInfo()
  printRunningNodeInfo(name)
  printRunningNodeInfo(node2)
  Thread.sleep(5000)
  //结点1开启，结点2关闭
  println("结点1开启，结点2仍然关闭")
  ZeusRedisMetricCenter.startRecording()
  printSysRunningInfo()
  printRunningNodeInfo(name)
  printRunningNodeInfo(node2)

  def printHisSysInfo(hisTime:Long) {
    println("sys his info")
  }
}
