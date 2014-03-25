package monitor2

import adcc.zeus.redis.RedisHelper


/**
 * @author zhangh
 * @version $Id: 14-3-12 下午5:18 
 */
/**
 * 消息结点的生存周期,只为了消息在结点内部运行的监控
 */
trait MsgNodeLifecycle {
  //标识名字
  def recordNodeName(nodename:String)
  //结点开始运行，记录结点名字，结点开始时间，结点开始时间为时间戳返回
  def recordStartTime(nodename:String,curTime:Long)
  //结点结束运行，删除结点名字，结点结束时间
  def recordStopTime(nodename:String,stopTime:Long)
  //进入报文计数加1
  def incrMsgIn(node:String)
   //出报文计数加1
  def incrMsgOut(node:String)
  //第一条msg进入时间
  def recordFirstMsgInTime(nodename:String,inTime:Long)
  //最后一条msg结束时间
  def recordLastMsgOutTime(nodename:String,lastTime:Long)
  //记录报文进入结点的时间
  def recordMsgInTime(msgMd5:String,inTime:Long)
  //将报文处理时间记录在一个bounded队列中
  def recordNMsgThroughTime(msgMd5: String, outTime: Long,inTime:Long,number:Long): Unit

  def recordTopNMsgThroughTimeMax(msgMd5:String,msg:String,outTime:Long,inTime:Long,topN:Long):Unit

}



class MsgNodeLifecycleToRedis(nodeName:String,val redisHelper:RedisHelper) extends MsgNodeLifecycle {



  import ZeusMetricKey._
  //标识名字，当结点运行了
  /**
   *  "sysnodes:[values]"
   */
  override def recordNodeName(node:String = nodeName): Unit = {
    redisHelper.appendToSet("zeus:nodes",node)
  }

  /**
   * 结点入报文计数
   * "node:[ip]:bigint"
   */
  override def incrMsgOut(node:String = nodeName): Unit = {
    redisHelper.incr(s"msgInCount:node:[$node]")
  }

  //进入报文计数加1
  override def incrMsgIn(node:String = nodeName): Unit = {
    redisHelper.incr(s"msgOutCount:node:$node]")
  }


  //结点结束运行，删除结点名字，结点结束时间
  override def recordStopTime(node: String = nodeName, stopTime: Long): Unit = {
    redisHelper.saveLong(gainNodeKeyForTime(node),"stopTime",stopTime)
  }

  //结点开始运行，记录结点名字，结点开始时间，结点开始时间为时间戳返回
  override def recordStartTime(node: String = nodeName, startTime: Long): Unit = {
    //记录结点的启动时间
    redisHelper.saveLong(gainNodeKeyForStartTime(node,startTime),"startTime",startTime)
  }

  def gainNodeKeyForStartTime(node:String = nodeName,startTime:Long) = {
     //获取系统是否是没有结点，没有结点用startTime作为参数，否则用系统的开始时间做参数
    val sysStartTime = redisHelper.gainValue("zeus:sys","startTime").getOrElse(startTime.toString)
     s"node:[$node]:sys:[$sysStartTime]"
  }

  def gainNodeKeyForEndTime(node:String = nodeName) = {
    val sysStartTime = redisHelper.gainValue("zeus:sys","startTime")
    if (sysStartTime.isDefined){
      s"node:[$node]:sys:[$sysStartTime]"
    }else{
      throw new IllegalArgumentException("错误逻辑，没有获得ZeusSys的启动时间，请重试")
    }

  }
  //最后一条msg结束时间
  override def recordLastMsgOutTime(node: String = nodeName, lastTime: Long): Unit = {
    redisHelper.saveLong(gainNodeKeyForEndTime(node),"lastMsgOutTime",lastTime)
  }

  //第一条msg进入时间
  override def recordFirstMsgInTime(node: String = nodeName, inTime: Long): Unit = {
    redisHelper.saveLong(s"node:[$node]","firstMsgInTime",inTime,true)
  }

  //记录报文在结点中处理的时间，会销毁进入时间
  override def recordNMsgThroughTime(msgMd5: String, outTime: Long,inTime:Long,number:Long =3000): Unit = {
    redisHelper.appendToBoundedList("throughtimeList",(outTime-inTime),number)
  }

  def gainMsgInTime(msgMd5:String) = {
    redisHelper.getKeyValue(s"msgInTime:md5:[$msgMd5]")
  }

  //记录报文进入结点的时间
  override def recordMsgInTime(msgMd5: String, inTime: Long): Unit = {
    redisHelper.saveKeyValue(s"msgInTime:md5:[$msgMd5]",inTime)
  }

  override def recordTopNMsgThroughTimeMax(msgMd5: String, msg: String, outTime: Long, inTime: Long, topN: Long): Unit = ???


  def gainStartTime(node: String = nodeName) = {
      redisHelper.gainValue(s"node:[$node]","startTime").map {
        s =>
          val times = s.toLong
          new java.util.Date(times)
      }
  }
  //从当前运行结点中，删除结点名字
  def rmRecordNodeName(node: String = nodeName) = {
     redisHelper.removeSetValue("zeus:nodes",node)
  }
}

trait SysLifecycle {
  def recordStartTime(milSec:Long)
  def recordStopTime(milSec:Long)
  def incrMsgIn()
  def incrMsgOut()
  //第一条msg进入时间
  def recordFirstMsgInTime()
  //最后一条msg结束时间
  def recordLastMsgOutTime()

}

class SysNodeLifecycle(val redisHelper:RedisHelper) extends SysLifecycle {


  def hasNoNode: Boolean = {
    redisHelper.gainSetSize("")
  }


  override def incrMsgOut(): Unit = {
    redisHelper.incr("msgOutCount:zeus")
  }

  override def incrMsgIn(): Unit = {
    redisHelper.incr("msgInCount:zeus")
  }

  override def recordStopTime(milSec:Long = System.currentTimeMillis() ): Unit = {
    redisHelper.saveLong(s"zeus:sys","stopTime",milSec)
  }

  override def recordStartTime(milSec:Long = System.currentTimeMillis()): Unit = {
    val isFirst= redisHelper.gainValue("zeus:sys","startTime").isEmpty

    redisHelper.saveLong("zeus:sys","startTime",milSec,true) //系统的开始时间
    if(isFirst){
      redisHelper.appendToList("zeus:sys:startTimeList",milSec.toString)
    }
  }
  //记录当前系统的结束时间
  def recordEndTime(milSec: Long=System.currentTimeMillis()) = {
    //记录系统的endTime
    redisHelper.appendToList("zeus:sys:endTimeList",milSec.toString)  //将endtime记录到队列中
    //去掉开始key的字段
    redisHelper.removeMapField("zeus:sys","startTime")
  }

  //最后一条msg结束时间
  override def recordLastMsgOutTime(): Unit = {
    redisHelper.saveLong(s"zeus:sys","lastMsgOutTime",System.currentTimeMillis())
  }

  //第一条msg进入时间
  override def recordFirstMsgInTime(): Unit ={
    redisHelper.saveLong(s"zeus:sys","firstMsgInTime",System.currentTimeMillis())
  }

  def gainStartTime() = {
    redisHelper.gainValue("zeus:sys","startTime").map {
      s =>
        val times = s.toLong
        new java.util.Date(times)
    }
  }

  def gainStartTimeList() = {
    redisHelper.gainListValues("zeus:sys:startTimeList").map{
      s => val milSec =  s.toLong ;new java.util.Date(milSec)
    }
  }

  def gainRunningNodeNames(): Set[String] = {
    redisHelper.gainSet("zeus:nodes")
  }

  def gainDeadRunningNodeNames(): Set[String] = {

  }
}