package monitor2

import ZeusRedisCenter._
/**
 * @author zhangh
 * @version $Id: 2014/3/24 16:54 
 */
object ZeusRedisMockExample extends App{

  ZeusRedisCenter.redis.keys foreach {
    s =>
      ZeusRedisCenter.redis.clearKey(s)
  }
  //结点1启动
  ZeusRedisCenter.start()

  println("Thread sleep 5 second,node 2 start")
  Thread.sleep(5000)

  //结点2启动
  val node2 = "192.168.243.187"
  ZeusRedisCenter.start(node2)
  //
  println("node2 start right")

  //模拟系统的所有结点的时间
  val res1 = ZeusRedisCenter.gainSystemStartTime()
  println("sys startTime:" + res1)
  val res2 = ZeusRedisCenter.gainNodeStartTime()
  println("node1 startTime:" + res2)
  val res3 = ZeusRedisCenter.gainNodeStartTime(node = "192.168.243.187")
  println("node2 startTime:" + res3)

  //模拟结点关闭
  //结点1关闭，结点2存在
  ZeusRedisCenter.closeNode()

  printSysRunningInfo()
  def printSysRunningInfo(){
    println("sys startTime:" + ZeusRedisCenter.gainSystemStartTime())
    println("sys running nodes:" + ZeusRedisCenter.gainRunningNodeNames())
    println("sys deading nodes:" + ZeusRedisCenter.gainDeadingNodeNames())
    println("sys last time from now:" + ZeusRedisCenter.gainLastTimeFromNow())

  }


  def printHisSysInfo(hisTime:Long) {
    println("sys his info")
  }
}
