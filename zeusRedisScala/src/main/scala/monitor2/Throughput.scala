package monitor2

/**
 * @author zhangh
 * @version $Id: 14-3-12 下午4:12 
 */



/**
 * 吞吐量类
 * @param nodeName  吞吐名称:结点名，或者Zeus系统名
 * @param startTime 启动时间(ms)：系统第一次启动时间或者结点第一次启动时间
 * @param firstMsgInTime 第一条Msg进入时间(ms)：系统或结点接收到第一条消息时间
 * @param lastMsgOutTime  最后一条Msg出去时间(ms)：系统或结点处理的最后一条消息的时间
 * @param totalMsgCount 总共吞吐的条数
 * @param curTime  当前时间(ms)
 * @param lastStackAverageRate 最近n条平均报文处理时间（ns）
 */
case class Throughput(nodeName:String = "Zeus",startTime:Long,firstMsgInTime:Long,lastMsgOutTime:Long,totalMsgCount:Long,curTime:Long,lastStackAverageRate:Long){


  /**
   * 每秒多少条报文
   * 约为，从接收到第一条进入系统的时间开始算起
   * 平均每秒处理的报文条数 =  总共吞吐的条数 / （最后一条Msg时间-第一条Msg时间）
   * @return
   */
  def msgPerSecond:Long = {
    val res =totalMsgCount / (lastMsgOutTime - firstMsgInTime) * 1000
    if ( res == 0) {
          totalMsgCount
      } else{
         res
      }
  }

  /**
   * 总运行时间：当前运行时间 - 启动时间
   * @return
   */
 def totalRunningTime:Long = curTime - startTime
}

