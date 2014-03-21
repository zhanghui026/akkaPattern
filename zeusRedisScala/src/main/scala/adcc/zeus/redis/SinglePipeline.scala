package adcc.zeus.redis

import scala.collection.JavaConversions._
import redis.clients.jedis.SortingParams

class SinglePipeline(redis: _root_.redis.clients.jedis.Pipeline) extends Pipeline {
  
  def del(key: String) = redis.del(key)
  
  def get(key: String) = redis.get(key)
  
  def ttl(key: String) = redis.ttl(key)
  
  def expire(key: String, ttl: Int) = redis.expire(key, ttl)
  
  def set(key: String, value: String) = redis.set(key, value)
  
  def setnx(key: String, value: String) = redis.setnx(key, value)
  
  def exists(key: String) = redis.exists(key)
  
  def getset(key: String, field: String) = redis.getSet(key, field)
  
  def hget(key: String, field: String) = redis.hget(key, field)
  
  def hexists(key: String, field: String) = redis.hexists(key, field)

  def hgetAll(key: String) = redis.hgetAll(key)
  
  def hincrBy(key: String, field: String, increment: Long) = redis.hincrBy(key, field, increment)
  
  def hset(key: String, field: String, value: String) = redis.hset(key, field, value)
  
  def hdel(key: String, field: String) = redis.hdel(key, field)
  
  def hmget(key: String, fields: List[String]) = redis.hmget(key, fields: _*)
  
  def hmset(key: String, details: Map[String, String]) = redis.hmset(key, details)
  
  def incrby(key: String, increment: Int) = redis.incrBy(key, increment)
  
  def incr(key: String) = redis.incr(key)
  
  def keys(pattern: String) = redis.keys(pattern)
  
  def put(key: String, values: Map[String, String]) = redis.hmset(key, values)
  
  def smembers(key: String) = redis.smembers(key)
  
  def scard(key: String) = redis.scard(key)
  
  def sadd(key: String, value: String) = redis.sadd(key, value)
  
  def sismember(key: String, value: String) = redis.sismember(key, value)
  
  def srem(key: String, value: String) = redis.srem(key, value)
  
  def lpop(key: String) = redis.lpop(key)
  
  def llength(key: String) = redis.llen(key)
  
  def lrange(key: String, start: Long, end: Long) = redis.lrange(key, start, end)
  
  def sort(key: String, sp: SortingParams) = redis.sort(key, sp)
  
  def rpush[T](key: String, value: T) = redis.rpush(key, value.toString)
  
  def lpush[T](key: String, value: T) = redis.lpush(key, value.toString)
  
  def ltrim(key: String, start: Long, end: Long) = redis.ltrim(key, start, end)
  
  def lrem(key: String, value: String, number: Long) = redis.lrem(key, number.toInt, value)
   
  def zadd(key: String, score: Double, value: String) = redis.zadd(key, score, value)
  
  def zrem(key: String, member: String) = redis.zrem(key, member)

  def zremrangeByScore(key: String, start: Double, end: Double) = redis.zremrangeByScore(key, start, end)

  def zremrangeByRank(key: String, start: Int, end: Int) = redis.zremrangeByRank(key, start, end)
  
  def zrevrange(key: String, start: Int, end: Int) = redis.zrevrange(key, start, end)
  
  def zrevrangeWithScores(key: String, start: Int, end: Int) = redis.zrevrangeWithScores(key, start, end)
  
  def zrange(key: String, start: Int, end: Int) = redis.zrange(key, start, end)
  
  def zrangeWithScores(key: String, start: Int, end: Int) = redis.zrangeWithScores(key, start, end)
  
  def zrank(key: String, member: String) = redis.zrank(key, member)
  
  def zrevrank(key: String, member: String) = redis.zrevrank(key, member)
  
  def zcard(key: String) = redis.zcard(key)
  
  def zscore(key: String, member: String) = redis.zscore(key, member)
  
  def zincrBy(key: String, increment: Double, member: String) = redis.zincrby(key, increment, member)
  
  def zinterStore(key: String, strings: Seq[String]) = redis.zinterstore(key, strings: _*)

  def zunionStore(key: String, strings: Seq[String]) = redis.zunionstore(key, strings: _*)
}