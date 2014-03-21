import monitor.{RedisHelper, MsgNodeLifecycleToRedis}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

/**
 * @author zhangh
 * @version $Id: 2014/3/19 10:38 
 */
val r = new MsgNodeLifecycleToRedis("sss")
r.incrMsgIn()
r.incrMsgIn()
r.incrMsgIn()
RedisHelper.keys foreach(println(_))




