package monitor2

import adcc.zeus.redis.RedisHelper

/**
 * @author zhangh
 * @version $Id: 14-3-11 下午2:37 
 */
object RedisMetricDemo extends App{
  val helper =  RedisHelper("192.168.243.217")
  //清空所有key
  helper.keys foreach println
  helper.keys.foreach { k =>
      helper.del(k)
  }

  helper.keys foreach println
}
