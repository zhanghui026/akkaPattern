package monitor

import com.redis.RedisClientPool


/**
 * 使用连接池和异步Future方式进行操作
 * @author zhangh
 * @version $Id: 14-3-13 下午2:36 
 */
object RedisHelper {

  val pool = new RedisClientPool("192.168.243.217", 6379)
  //没加入timeout password
  //  val rc = new RedisClient("192.168.243.217", 6379)
//  val rc = new SingleRedis(pool)

  /**
   *
   * @param key  the list key
   * @param value
   */
  def appendToList(key: String, value: String) = {
    pool.withClient { rc =>
      rc.sadd(key, value)
    }
  }

  def saveStrField(field: String, value: String)(implicit key: String = "sys") = {
    pool.withClient {
      rc =>
        rc.hset(key, field, value)
    }
  }

  def saveLong(key: String, field: String, value: Long, isNx: Boolean = false) = {
    pool.withClient {
      rc =>
        if (isNx) {
          rc.hsetnx(key, field, value)
        } else {
          rc.hset(key, field, value.toString)
        }
    }
  }


  //若没有设置，有则不修改之前设置好的value
  def saveKeyValue[T](key: String, value: T, isNx: Boolean = false) = {
    pool.withClient {
      rc =>
        if (isNx) {
          rc.setnx(key, value)
        } else {
          rc.set(key, value)
        }
    }
  }

  /**
   * 增加值
   * @param key
   */
  def incr(key: String) = {
    pool.withClient {
      rc =>
        rc.incr(key)
    }
  }

  def appendToBoundedList[T](key: String, value: T, number: Long): Unit = {
//    println(value)
    pool.withClient {
      rc =>
      //推到队尾
        rc.rpush(key,value)
        rc.llen(key).foreach {
            l =>
              if (l > number){
                val popnum = l - number
                for (i <- 0 until popnum.toInt) {
                  rc.lpop(key)
                }
              }
          }


    }
  }

  def close = pool.close

  def getKeyValue(s: String) = {
    pool.withClient {rc =>
      rc.get(s)
    }
  }

  def gainListValues(key: String, range: Int = -1) = {
    pool.withClient { rc =>
      rc.lrange(key, 0, range)
    }
  }

  def gainBoundedListLength(key: String) = {
    pool.withClient {
      rc =>
        rc.llen(key)
    }
  }

  def keys = {
    pool.withClient { rc =>
      rc.keys("*")
    }
  }

  def clearBoundedList(key:String) = {
    pool.withClient {
      rc =>
        rc.del(key)
    }
  }
}
