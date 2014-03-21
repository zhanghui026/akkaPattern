package monitor

/**
 * @author zhangh
 * @version $Id: 14-3-11 下午2:37 
 */
object RedisMetricDemo extends App{

  val node = new MsgNodeLifecycleToRedis("node1")
  node.incrMsgIn()
}
