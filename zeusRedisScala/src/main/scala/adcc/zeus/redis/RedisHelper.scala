package adcc.zeus.redis


import redis.clients.jedis.JedisPool


/**
 * 使用连接池操作，支持一个top

 * @version $Id: 14-3-13 下午2:36 
 */
class RedisHelper(pool:JedisPool) {

  val rc = new SingleRedis(pool)

  def sismember(key: String, value: String): Boolean = rc.sismember(key,value)

  def sadd(key: String, value: String) = {
    rc.sadd(key, value)
  }

  def smembers(key: String): Set[String] = {
    rc.smembers(key)
  }
  def srem(setKey:String,value: String) = {
    rc.srem(setKey,value)
  }

  def rpush[T](s: String, value: T) = {
    rc.rpush(s,value)
  }

  def hset(key: String = "sys",field: String, value: String) = {
    rc.hset(key, field, value)
  }

  def hsetNX(key: String, field: String, value: Long, isNx: Boolean = false) = {
    if (isNx) {
      rc.hsetnx(key, field, value)
    } else {
      rc.hset(key, field, value.toString)
    }
  }

  /**
   * 去掉field
   * @param key
   * @param field
   */
  def hdel(key:String,field:String) {
    rc.hdel(key,field)
  }


  //若没有设置，有则不修改之前设置好的value
  def saveKeyValue[T](key: String, value: T, isNx: Boolean = false) = {
    if (isNx) {
      rc.setnx(key, value)
    } else {
      rc.set(key, value)
    }
  }

  /**
   * 增加值
   * @param key
   */
  def incr(key: String) = {
    rc.incr(key)
  }



  def zrangeWithScores[T](key:String,number:Int = -1) = {
    rc.zrangeWithScores(key,0,-1)
  }


  def scard(key:String) = {
    rc.scard(key)
  }

  def shutdown = rc.shutdown

  def get(s: String) = {
    rc.get(s)
  }

  def lrangeFromZero(key: String, range: Int = -1) = {
    rc.lrange(key, 0, range)
  }

  def llength(key: String) = {
    rc.llength(key)
  }

  def keys = {
    rc.keys("*")
  }

  def clearBoundedList(key:String) = {
    rc.del(key)
  }

  def del (key:String) = {
    rc.del(key)
  }

  //获取时间
  def hget(key: String, field: String) = {
    rc.hget(key,field)
  }

  def hexists(key: String, field: String): Boolean = {
    rc.hexists(key,field)
  }

  def ktype(s: String): String = {
    rc.ktype(s)
  }

  /**
   * 添加到有bound的队列中
   * @param key
   * @param value
   * @param number
   * @tparam T
   */
  def appendToBoundedList[T](key: String, value: T, number: Long): Unit = {
    rc.exec { pipLine =>
      pipLine.rpush(key, value)
      val popNumber = (llength(key) - number).toInt
      if (popNumber > 0) {
        (0 to popNumber) foreach {
          i => pipLine.lpop(key)
        }
      }
    }
  }

  /**
   * 加入到top-n的集合中，若属于top-n则加入
   */
  def appendToTopNSortedSet[T](key: String,value:T,score:Double,number:Int)(implicit topMax:Boolean = true) = {
    rc.exec {pip =>
      val r = rc.zcard(key)
      if (r < number){ //小于计数直接加入
        pip.zadd(key, score, value.toString)
      } else {
        if (topMax) {
          //选择最小的第n个进行比较
          rc.zrangeWithScores(key, -number, -number).take(1).map {
            s =>
              if (s._2 < score) {
                pip.zremrangeByRank(key, 0, -number) //去掉TopN之前score的值
                pip.zadd(key, score, value.toString)
              }
          }
        }else{
          //选择第n个进行比较
          rc.zrangeWithScores(key, number-1, number-1).take(1).map {
            s =>
              if (s._2 > score) {
                pip.zadd(key, score, value.toString)
                pip.zremrangeByRank(key, number,-1) //去掉TopN之前score的值
              }
          }
        }
      }
    }
  }
}
object RedisHelper {

  def apply(host:String) = new RedisHelper(new JedisPool(host))
}
