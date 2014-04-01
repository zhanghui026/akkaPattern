package monitor2

/**
 * @author zhangh
 * @version $Id: 14-3-12 下午4:12 
 */


trait Through

case class NoThroughput(nodeName: String = "Zeus") extends Through

/**
 * 吞吐量类
 * @param nodeName  吞吐名称:结点名，或者Zeus系统名
 * @param startTime 启动时间(ms)：系统第一次启动时间或者结点第一次启动时间
 * @param firstMsgInTime 第一条Msg进入时间(ms)：系统或结点接收到第一条消息时间
 * @param curTime  当前时间(ms)

 */
case class Throughput(nodeName: String = "Zeus", startTime: Long, firstMsgInTime: Long, msgInCount: BigInt, msgOutCount: BigInt, curTime: Long, topNMax: Seq[(String, String, Long)], average: Long) extends Through
{


  /**
   * 总运行时间：当前运行时间 - 启动时间
   * @return
   */
  def totalRunningTime: Long = curTime - startTime

  //TODO 加入print-beautiful
}

