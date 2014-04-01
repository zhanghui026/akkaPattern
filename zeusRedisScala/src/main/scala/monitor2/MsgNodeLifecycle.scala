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
//  def recordMsgInTime(msgMd5:String,inTime:Long)
//  //将报文处理时间记录在一个bounded队列中
//  def recordNMsgThroughTime(msgMd5: String, outTime: Long,inTime:Long,number:Long): Unit
//
//  def recordTopNMsgThroughTimeMax(msgMd5:String,msg:String,outTime:Long,inTime:Long,topN:Int):Unit

}



class MsgNodeLifecycleToRedis(nodeName:String,val redisHelper:RedisHelper) extends MsgNodeLifecycle {



  //标识名字，当结点运行了
  /**
   *  在zeus:runningNodes里记录运行结点
   *  zeus:allNodes里记录结点
   */
  override def recordNodeName(node:String = nodeName): Unit = {
    redisHelper.sadd("zeus:runningNodes",node)
    redisHelper.sadd("zeus:allNodes",node)
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
    redisHelper.hsetNX(gainNodeStartTimeListKey(node),"stopTime",stopTime)
  }

  //结点开始运行，记录结点名字，结点开始时间，结点开始时间为时间戳返回
  override def recordStartTime(node: String = nodeName, startTime: Long): Unit = {
    //记录结点的启动时间
    val nodeKey =  gainNodeStartTimeListKeyMayNoStart(node,startTime)
    //在此key list中加入开启时间列表中
    redisHelper.rpush(nodeKey,startTime)
    redisHelper.hset(s"node:[$node]","startTime",startTime.toString)
  }

  def gainNodeStartTimeListKeyMayNoStart(node:String = nodeName,startTime:Long) = {
     //获取系统是否是没有结点，没有结点用startTime作为参数，否则用系统的开始时间做参数
    val sysStartTime = redisHelper.hget("zeus:sys","startTime").getOrElse(startTime.toString)
     s"node:[$node]:sys:[$sysStartTime]:sTimeList"
  }



  def gainNodeStartTimeListKey(node:String = nodeName) = {
    val sysStartTime = redisHelper.hget("zeus:sys","startTime")
    val startTime = redisHelper.hget(s"node:[$node]","startTime")
    if (sysStartTime.isDefined){
      s"node:[$node]:sys:[${sysStartTime.get}]:stime:[${startTime.get.toString}]"
    }else{
      throw new IllegalArgumentException("错误逻辑，没有获得ZeusSys的启动时间，请重试")
    }
  }


  //最后一条msg结束时间
  override def recordLastMsgOutTime(node: String = nodeName, lastTime: Long): Unit = {
    redisHelper.hsetNX(gainNodeStartTimeListKey(node),"lastMsgOutTime",lastTime)
  }

  /**
   * 判断是否有第一条消息进入了
   * @param node
   *
   * @return
   */
  def hasFirstMsgInTime(node: String = nodeName):Boolean = {
    redisHelper.hexists(s"node:[$node]","firstMsgInTime")
  }

  //第一条msg进入时间
  override def recordFirstMsgInTime(node: String = nodeName, inTime: Long): Unit = {
    redisHelper.hsetNX(s"node:[$node]","firstMsgInTime",inTime,true)
  }



  def gainMsgInTime(msgMd5:String) = {
    redisHelper.get(s"msgInTime:md5:[$msgMd5]").map {
      t => t.toLong
    }
  }



  //记录报文进入结点的时间
  def recordMsgInTime(msgMd5: String, inTime: Long,node:String = nodeName): Unit = {
    redisHelper.saveKeyValue(s"msgInTime:md5:[$msgMd5]",inTime)
  }

  def gainFirstMsgInTime(node: String = nodeName) = {
    redisHelper.hget(s"node:[$node]","firstMsgInTime") map {
      s => s.toLong
    }
  }

  def gainMsgIn(node: String = nodeName):BigInt = {
    val r = redisHelper.get(s"msgInCount:node:[$node]")
    r.map {
      d => BigInt(d)
    }.getOrElse(0)
  }


  def gainMsgOut(node: String = nodeName):BigInt = {
    val r = redisHelper.get(s"msgOutCount:node:[$node]")
    r.map {
      d => BigInt(d)
    }.getOrElse(BigInt(0))
  }

  //报文出了系统
  def delMsgInTime(msgMd5: String) = {
      redisHelper.del(s"msgInTime:md5:[$msgMd5]")
  }

  def recordTopNMsgThroughTimeMax(msgMd5: String, msg: String, outTime: Long, inTime: Long, topN: Int = 20,node:String = nodeName): Unit = {
    redisHelper.appendToTopNSortedSet(s"topNMax:node:[$node]",msgMd5+":"+msg,(outTime-inTime),topN)
  }

  def gainTopNMsg(node: String = nodeName) = {
    redisHelper.zrangeWithScores(s"topNMax:node:[$node]").map {
      s  => {
        val firstSep = s._1.indexOf(':')
        (s._1.take(firstSep),s._1.drop(firstSep+1),s._2.toLong)
      }
    }
  }

  def recordNMsgThroughTime(outTime: Long,inTime:Long,number:Long =3000,node:String= nodeName): Unit = {
    redisHelper.appendToBoundedList(s"throughTimeList:node:[$node]",(outTime-inTime),number)
  }

  def gainNMsgThroughTime(node: String = nodeName) = {
    redisHelper.lrangeFromZero(s"throughTimeList:node:[$node]").map(s => s.toLong)
  }

  def gainStartTime(node: String = nodeName) = {
      redisHelper.hget(s"node:[$node]","startTime") map {s => s.toLong}
  }



  //从当前运行结点中，删除结点名字
  def rmRunningNodeName(node: String = nodeName) = {
     redisHelper.srem("zeus:runningNodes",node)
  }

  /**
   * 关闭结点
   */
  def shutDown(node:String = nodeName) = {
     //将当前结点的开始时间去掉
    redisHelper.hdel(s"node:[$node]","startTime")
  }
  def isRunning(node:String = nodeName): Boolean = {
    redisHelper.sismember("zeus:runningNodes",node)
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



  def isRunning: Boolean = {
    redisHelper.scard("zeus:runningNodes") > 0
  }


  override def incrMsgOut(): Unit = {
    redisHelper.incr("msgOutCount")
  }

  override def incrMsgIn(): Unit = {
    redisHelper.incr("msgInCount")
  }


  def gainMsgIn():BigInt = {
    val r = redisHelper.get("msgInCount")
    r.map {
      d => BigInt(d)
    }.getOrElse(BigInt(0))
  }

  def gainMsgOut():BigInt = {
    val r = redisHelper.get("msgOutCount")
    r.map {
      d => BigInt(d)
    }.getOrElse(BigInt(0))
  }

  override def recordStopTime(milSec:Long = System.currentTimeMillis() ): Unit = {
    redisHelper.hsetNX(s"zeus:sys","stopTime",milSec)
  }

  override def recordStartTime(milSec:Long = System.currentTimeMillis()): Unit = {
    val isFirst= redisHelper.hget("zeus:sys","startTime").isEmpty

    redisHelper.hsetNX("zeus:sys","startTime",milSec,true) //系统的开始时间
    if(isFirst){
      redisHelper.rpush("zeus:sys:startTimeList",milSec.toString)
    }
  }
  //记录当前系统的结束时间
  def recordEndTime(milSec: Long=System.currentTimeMillis()) = {
    //记录系统的endTime
    redisHelper.rpush("zeus:sys:endTimeList",milSec.toString)  //将endtime记录到队列中
    //去掉开始key的字段
    redisHelper.hdel("zeus:sys","startTime")
  }

  //最后一条msg结束时间
  override def recordLastMsgOutTime(): Unit = {
    redisHelper.hsetNX(s"zeus:sys","lastMsgOutTime",System.currentTimeMillis())
  }

  /**
   * 判断第一条消息是否进入
   * @return
   */
  def hasFirstMsgInTime() = {
    redisHelper.hexists("zeus:sys","firstMsgInTime")
  }
  //第一条msg进入时间
  override def recordFirstMsgInTime(): Unit ={
    redisHelper.hsetNX("zeus:sys","firstMsgInTime",System.currentTimeMillis())
  }

  def gainFirstMsgInTime() = {
    redisHelper.hget("zeus:sys","firstMsgInTime").map {
      s => s.toLong
    }
  }


  def gainStartTime() = {
    redisHelper.hget("zeus:sys","startTime").map {
      s => s.toLong

    }
  }

  def gainStartTimeList() = {
    redisHelper.lrangeFromZero("zeus:sys:startTimeList").map{
      s => val milSec =  s.toLong ;new java.util.Date(milSec)
    }
  }

  def gainRunningNodeNames(): Set[String] = {
    redisHelper.smembers("zeus:runningNodes")
  }

  def gainAllNodeNames(): Set[String] = {
    redisHelper.smembers("zeus:allNodes")
  }

  //记录报文在系统中处理的时间，会销毁进入时间
  def recordNMsgThroughTime(outTime: Long,inTime:Long,number:Long =3000): Unit = {
    redisHelper.appendToBoundedList("sys:throughTimeList",(outTime-inTime),number)
  }

  def recordTopNMsgThroughTimeMax(msgMd5: String, msg: String, outTime: Long, inTime: Long, topN: Int = 20) = {
    redisHelper.appendToTopNSortedSet(s"topNMax:sys",msgMd5+":"+msg,(outTime-inTime),topN)
  }

  /**
   * 获得最大的n条记录
   * @return
   */
  def gainTopNMsg() = {
    redisHelper.zrangeWithScores("topNMax:sys").map {
      s  => {
        val firstSep = s._1.indexOf(':')
        (s._1.take(firstSep),s._1.drop(firstSep+1),s._2.toLong)
      }
    }
  }

  /**
   * 获得n条消息的平均时间
   * @return
   */
  def gainNMsgThroughTime() = {
    redisHelper.lrangeFromZero("sys:throughTimeList").map(s => s.toLong)
  }

 
}