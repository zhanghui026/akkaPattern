package monitor2

import adcc.zeus.redis.RedisHelper
import java.net.InetAddress
import org.apache.commons.codec.digest.DigestUtils

/**
 * @author zhangh
 * @version $Id: 2014/3/24 13:57 
 */
object ZeusRedisMetricCenter {



  val redis = RedisHelper("192.168.243.217")
  val name = {
    //
    val inetAddress = InetAddress.getLocalHost
    inetAddress.toString
  }
  val nodeLife = new MsgNodeLifecycleToRedis(name,redis)
  val systemLife = new SysNodeLifecycle(redis)

  val averageMsgCount = 1000
  val topN = 10

  def startRecording(node:String = name){
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
  def endRecording(node:String = name) {
      //结点关闭，从结点名字池中去除当前结点信息
    nodeLife.rmRunningNodeName(node)
    val time = System.currentTimeMillis()
    nodeLife.recordStopTime(node,time) //记录结点关闭时间
    //去掉结点名称
    nodeLife.shutDown(node)
    //若没有开启结点，则记录结点关闭时间
    if (!systemLife.isRunning){
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

  /**
   * 获得运行的结点名
   * @return
   */
  def gainRunningNodeNames(): Set[String] = {
    systemLife.gainRunningNodeNames()
  }

  def gainNodeSecsFromNow(node: String): Any = {
    nodeLife.gainStartTime().map {
      stime => System.currentTimeMillis() - stime
    }.getOrElse(0)
  }
  /**
   * 获得所有结点名
   * @return
   */
  def gainAllNodeNames(): Set[String] = {
    systemLife.gainAllNodeNames()
  }

  /**
   * 获得当前系统的运行时间
   * @return
   */
  def gainSysSecsFromNow() = {
    systemLife.gainStartTime().map {
      stime => System.currentTimeMillis() - stime
    }.getOrElse(0)
  }

  /**
   * 获得所有的keys
   * @return
   */
  def allKeys(): Set[String] = {
    redis.keys
  }

  def allKeyTypeSet():Set[(String,String)] = {
    redis.keys.map{
      k => (k,redis.ktype(k))
    }
  }





  //获得msg的md5
  def md5(msg:String) =  DigestUtils.md5Hex(msg)
  //记录msg-in
  def recordMsgIn(msg:String,msgInTime:Long,node:String = name){
    //判断是否是首个结点报文
    if(!nodeLife.hasFirstMsgInTime(node:String)){
       //将第一个消息放入node中
      nodeLife.recordFirstMsgInTime(node,msgInTime)
    }

    if (!systemLife.hasFirstMsgInTime()){
        //将第一个消息放入到sys中
       systemLife.recordFirstMsgInTime()
    }
    //记录结点自增
    nodeLife.incrMsgIn(node)
    systemLife.incrMsgIn()

    //node 结点记录信息
    nodeLife.recordMsgInTime(md5(msg),msgInTime,node)
    nodeLife.incrMsgIn(node)

  }

  //记录msg-out
  def recordMsgOut(msg:String,outTime:Long,node:String = name){
    val msgId = md5(msg)
    val inTime = nodeLife.gainMsgInTime(msgId)
    if (inTime.isDefined) {
      //删除该报文进入时间
      nodeLife.delMsgInTime(msgId)
      //记录结点自增
      nodeLife.incrMsgOut(node)
      systemLife.incrMsgOut()

      // msg 最大耗时操作
      nodeLife.recordNMsgThroughTime(outTime,inTime.get,averageMsgCount,node)

      systemLife.recordNMsgThroughTime(outTime,inTime.get,averageMsgCount)

      //记录topMax
      nodeLife.recordTopNMsgThroughTimeMax(msgId,msg,outTime,inTime.get,topN,node)
      systemLife.recordTopNMsgThroughTimeMax(msgId,msg,outTime,inTime.get,topN)
    }

  }

  /**
   * 获得系统的吞吐量
   */
  def gainThroughoutNow: Through = {
    if (systemLife.isRunning) {
      //得到in的个数
    val inCount = systemLife.gainMsgIn()
     //得到out的个数
    val outCount = systemLife.gainMsgOut()
    val sysStartTime = gainSystemStartTime()
    val firstMsgIn = systemLife.gainFirstMsgInTime()
     // 最近topN条记录以及时间消耗
    val topNMax = systemLife.gainTopNMsg()

    val timesList = systemLife.gainNMsgThroughTime()
    // 最近吞吐的平均
    val average = if (timesList.size > 0) timesList.sum / timesList.size  else 0
      if (inCount == BigInt(0)){
        NoThroughput("Zeus")
      }else {
        Throughput("Zeus", sysStartTime.get, firstMsgIn.get, inCount, outCount, System.currentTimeMillis(), topNMax, average)
      }
     } else {
        NoThroughput("Zeus")
      }
  }

  /**
   * 获得某个节点的吞吐量
   * @param node
   * @return
   */
  def gainNodeThroughoutNow(node:String):Through = {
    if (nodeLife.isRunning(node)) {
    //得到in的个数
    val inCount = nodeLife.gainMsgIn(node)
    //得到out的个数
    val outCount = nodeLife.gainMsgOut(node)
    val startTime = gainNodeStartTime(node)
    val firstMsgIn = nodeLife.gainFirstMsgInTime(node)
    // 最近topN条记录以及时间消耗
    val topNMax = nodeLife.gainTopNMsg(node)

    val timesList = nodeLife.gainNMsgThroughTime(node)
    // 最近吞吐的平均
    val average = if (timesList.size > 0) timesList.sum / timesList.size  else 0
     if (inCount == BigInt(0)) {
       NoThroughput(node)
     } else {
       Throughput(node, startTime.get, firstMsgIn.get, inCount, outCount, System.currentTimeMillis(), topNMax, average)
     }
     } else {
      NoThroughput(node)
    }
  }


  def gainSysMsgInCount = {
      systemLife.gainMsgIn()
  }

  def gainSysMsgOutCount = {
     systemLife.gainMsgOut()
  }




}
