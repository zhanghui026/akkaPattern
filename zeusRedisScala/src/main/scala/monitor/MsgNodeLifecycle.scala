package monitor



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



class MsgNodeLifecycleToRedis(nodeName:String) extends MsgNodeLifecycle {
  import ZeusMetricKey._

  //标识名字，当结点运行了
  /**
   *  "sysnodes:[values]"
   */
  override def recordNodeName(node:String = nodeName): Unit = {
    RedisHelper.appendToList("zeus:nodes",node)
  }

  /**
   * 结点入报文计数
   * "node:[ip]:bigint"
   */
  override def incrMsgOut(node:String = nodeName): Unit = {
    RedisHelper.incr(s"msgInCount:node:[$node]")
  }

  //进入报文计数加1
  override def incrMsgIn(node:String = nodeName): Unit = {
     RedisHelper.incr(s"msgOutCount:node:$node]")
  }


  //结点结束运行，删除结点名字，结点结束时间
  override def recordStopTime(node: String = nodeName, stopTime: Long): Unit = {
    RedisHelper.saveLong(s"node:[$node]","stopTime",stopTime)

  }

  //结点开始运行，记录结点名字，结点开始时间，结点开始时间为时间戳返回
  override def recordStartTime(node: String = nodeName, startTime: Long): Unit = {
    //记录结点的启动时间
    RedisHelper.saveLong(s"node:[$node]","startTime",startTime)
  }

  //最后一条msg结束时间
  override def recordLastMsgOutTime(node: String = nodeName, lastTime: Long): Unit = {
    RedisHelper.saveLong(s"node:[$node]","lastMsgOutTime",lastTime)
  }

  //第一条msg进入时间
  override def recordFirstMsgInTime(node: String = nodeName, inTime: Long): Unit = {
    RedisHelper.saveLong(s"node:[$node]","firstMsgInTime",inTime,true)
  }

  //记录报文在结点中处理的时间，会销毁进入时间
  override def recordNMsgThroughTime(msgMd5: String, outTime: Long,inTime:Long,number:Long =3000): Unit = {
    RedisHelper.appendToBoundedList("throughtimeList",(outTime-inTime),number)
  }

  def gainMsgInTime(msgMd5:String) = {
    RedisHelper.getKeyValue(s"msgInTime:md5:[$msgMd5]")
  }

  //记录报文进入结点的时间
  override def recordMsgInTime(msgMd5: String, inTime: Long): Unit = {
    RedisHelper.saveKeyValue(s"msgInTime:md5:[$msgMd5]",inTime)
  }

  override def recordTopNMsgThroughTimeMax(msgMd5: String, msg: String, outTime: Long, inTime: Long, topN: Long): Unit = ???
}

trait SysLifecycle {
  def recordStartTime()
  def recordStopTime()
  def incrMsgIn()
  def incrMsgOut()
  //第一条msg进入时间
  def recordFirstMsgInTime()
  //最后一条msg结束时间
  def recordLastMsgOutTime()
}

class SysNodeLifecycle extends SysLifecycle {
  override def incrMsgOut(): Unit = {
     RedisHelper.incr("msgOutCount:zeus")
  }

  override def incrMsgIn(): Unit = {
    RedisHelper.incr("msgInCount:zeus")
  }

  override def recordStopTime(): Unit = {
    RedisHelper.saveLong(s"zeus:sys","stopTime",System.currentTimeMillis())
  }

  override def recordStartTime(): Unit = {
    RedisHelper.saveLong(s"zeus:sys","startTime",System.currentTimeMillis())
  }

  //最后一条msg结束时间
  override def recordLastMsgOutTime(): Unit = {
    RedisHelper.saveLong(s"zeus:sys","lastMsgOutTime",System.currentTimeMillis())
  }

  //第一条msg进入时间
  override def recordFirstMsgInTime(): Unit ={
    RedisHelper.saveLong(s"zeus:sys","firstMsgInTime",System.currentTimeMillis())
  }
}