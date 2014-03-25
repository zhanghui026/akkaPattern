package monitor2

import adcc.zeus.redis.RedisHelper
import java.net.InetAddress

/**
 * @author zhangh
 * @version $Id: 2014/3/24 13:57 
 */
object ZeusRedisCenter {



  val redis = RedisHelper("192.168.243.217")
  val name = {
    //
    val inetAddress = InetAddress.getLocalHost
    inetAddress.toString
  }
  val nodeLife = new MsgNodeLifecycleToRedis(name,redis)
  val systemLife = new SysNodeLifecycle(redis)

  def start(node:String = name){
    //结点开始，记录结点名字，结点开始时间，以及系统开始时间
    nodeLife.recordNodeName(node)
    val time1 = System.currentTimeMillis()
    nodeLife.recordStartTime(node,time1)
    systemLife.recordStartTime(time1)
  }

  /**
   * 关闭结点，当所有节点关闭，系统关闭
   * @param node
   */
  def closeNode(node:String = name) {
      //结点关闭，从结点名字池中去除当前结点信息
    nodeLife.rmRecordNodeName(node)
    val time = System.currentTimeMillis()
    nodeLife.recordStopTime(node,time)
    //若没有开启结点，则记录结点关闭时间
    if (systemLife.hasNoNode){
       systemLife.recordEndTime(time)
    }
  }

  def gainNodeStartTime(node:String = name) = {
     nodeLife.gainStartTime(node)
  }

  def gainSystemStartTime() = {
    systemLife.gainStartTime()
  }

  def gainSystemStartTimeList() = {
    systemLife.gainStartTimeList()
  }

  def gainRunningNodeNames(): Seq[String] = {
    systemLife.gainRunningNodeNames()
  }
}
