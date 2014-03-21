package adcc.zeus.redis

import redis.clients.jedis.Response
import redis.clients.jedis.SortingParams

trait Pipeline {
  
  def del(key: String): Response[java.lang.Long]
  
  def get(key: String): Response[java.lang.String]
  
  def ttl(key: String): Response[java.lang.Long]
  
  def expire(key: String, ttl: Int): Response[java.lang.Long]
  
  def set(key: String, value: String): Response[java.lang.String]
  
  def setnx(key: String, value: String): Response[java.lang.Long]
  
  def exists(key: String): Response[java.lang.Boolean]
  
  def getset(key: String, field: String): Response[String]
  
  def hget(key: String, field: String): Response[String]
  
  def hexists(key: String, field: String): Response[java.lang.Boolean]

  def hgetAll(key: String): Response[java.util.Map[String, String]]
  
  def hincrBy(key: String, field: String, increment: Long): Response[java.lang.Long]
  
  def hset(key: String, field: String, value: String): Response[java.lang.Long]
  
  def hdel(key: String, field: String): Response[java.lang.Long]
  
  def hmget(key: String, fields: List[String]): Response[java.util.List[java.lang.String]]
  
  def hmset(key: String, details: Map[String, String]): Response[java.lang.String]
  
  def incrby(key: String, increment: Int): Response[java.lang.Long]
  
  def incr(key: String): Response[java.lang.Long]
  
  def put(key: String, values: Map[String, String]): Response[java.lang.String]
  
  def smembers(key: String): Response[java.util.Set[java.lang.String]]
  
  def scard(key: String): Response[java.lang.Long]
  
  def sadd(key: String, value: String): Response[java.lang.Long]
  
  def sismember(key: String, value: String): Response[java.lang.Boolean]
  
  def srem(key: String, value: String): Response[java.lang.Long]
  
  def lpop(key: String): Response[java.lang.String]
  
  def sort(key: String, sp: SortingParams): Response[java.util.List[java.lang.String]]
  
  def llength(key: String): Response[java.lang.Long]
  
  def lrange(key: String, start: Long, end: Long): Response[java.util.List[java.lang.String]]
  
  def rpush[T](key: String, value: T): Response[java.lang.Long]
  
  def lpush[T](key: String, value: T): Response[java.lang.Long]
  
  def ltrim(key: String, start: Long, end: Long): Response[java.lang.String]
  
  def lrem(key: String, value: String, num: Long): Response[java.lang.Long]
  
  def zadd(key: String, score: Double, value: String): Response[java.lang.Long]
  
  def zrem(key: String, member: String): Response[java.lang.Long]

  def zremrangeByScore(key: String, start: Double, end: Double): Response[java.lang.Long]

  def zremrangeByRank(key: String, start: Int, end: Int): Response[java.lang.Long]
  
  def zrevrange(key: String, start: Int, end: Int): Response[java.util.Set[java.lang.String]]
  
  def zrevrangeWithScores(key: String, start: Int, end: Int): Response[java.util.Set[redis.clients.jedis.Tuple]]
  
  def zrange(key: String, start: Int, end: Int): Response[java.util.Set[java.lang.String]]
  
  def zrangeWithScores(key: String, start: Int, end: Int): Response[java.util.Set[redis.clients.jedis.Tuple]]
  
  def zrank(key: String, member: String): Response[java.lang.Long]
  
  def zrevrank(key: String, member: String): Response[java.lang.Long]
  
  def zcard(key: String): Response[java.lang.Long]
  
  def zscore(key: String, member: String): Response[java.lang.Double]
  
  def zincrBy(key: String, increment: Double, member: String): Response[java.lang.Double]

}