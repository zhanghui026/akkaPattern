package adcc.zeus.redis

import redis.clients.jedis.ShardedJedisPool
import scala.collection.JavaConversions._
import redis.clients.jedis.ShardedJedis
import redis.clients.jedis.Transaction
import redis.clients.jedis.ShardedJedisPipeline
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo
import redis.clients.util.Hashing
import adcc.zeus.redis.Redis._
import redis.clients.util.ShardInfo

class ShardedRedis(pool: ShardedJedisPool) extends Redis {

  def this(config: JedisPoolConfig, shardInfo: Iterable[JedisShardInfo], hashing: Option[Hashing]) = this(ShardedRedis.pool(config, shardInfo.toList, hashing))

  def this(shards: Iterable[Shard], poolSize: Int = 60, hashing: Option[Hashing] = None) = this(Redis.config(poolSize), Shard.toShardInfo(shards), hashing)

  def del(key: String) = this.run(redis => {redis.del(key)})

  def get(key: String) = this.run(redis => {Option(redis.get(key))})

  def ttl(key: String) = this.run(redis => {redis.ttl(key)})

  def expire(key: String, ttl: Int) = this.run(redis => {redis.expire(key, ttl)})

  def set[T](key: String, value: T) = this.run(redis => {redis.set(key, value.toString)})

  def setnx[T](key: String, value: T) = this.run(redis => {redis.setnx(key, value.toString)})

  def exists(key: String) = this.run(redis => {redis.exists(key)})

  def getset(key: String, field: String) = this.run(redis => {Option(redis.getSet(key, field))})

  def hget(key: String, field: String) = this.run(redis => {Option(redis.hget(key, field))})

  def hexists(key: String, field: String) = this.run(redis => {redis.hexists(key, field)})

  def hgetAll(key: String) = this.run(redis => {redis.hgetAll(key).toMap})

  def hgetMany(keys: Seq[String]) = {
    val results = this.syncAndReturnAll(pipeline => {keys.foreach(key => { pipeline.hgetAll(key) })})

    val seq = Seq.newBuilder[Map[String, String]]
    results.foreach( r => { seq+= r.asInstanceOf[java.util.HashMap[String, String]].toMap })
    seq.result()
  }

  def hincrBy(key: String, field: String, increment: Long) = this.run(redis => {redis.hincrBy(key, field, increment)})

  def hset[T](key: String, field: String, value: T) = this.run(redis => {redis.hset(key, field, value.toString)})
  def hsetnx[T](key: String, field: String, value: T) = this.run(redis => {redis.hsetnx(key, field, value.toString)})


  def hdel(key: String, field: String) = this.run(redis => {redis.hdel(key, field)})

  def hmget(key: String, fields: Seq[String]) = this.run(redis => {redis.hmget(key, fields : _*) map (value => Option(value))})

  def hmset(key: String, details: Map[String, String]) = this.run(redis => {redis.hmset(key, details)})

  def incrby(key: String, increment: Int) = this.run(redis => { redis.incrBy(key, increment) })

  def incr(key: String) = this.run(redis => { redis.incr(key) })

  def put(key: String, values: Map[String, String]) = this.run(redis => {redis.hmset(key, values)})

  def smembers(key: String) = this.run(redis => {redis.smembers(key).toSet})

  def scard(key: String) = this.run(redis => {redis.scard(key)})

  def sadd(key: String, value: String) = this.run(redis => {redis.sadd(key, value)})

  def srem(key: String, value: String) = this.run(redis => {redis.srem(key, value)})

  def sismember(key: String, value: String) = this.run(redis => {redis.sismember(key, value)})

  def lpop(key: String) = this.run(redis => {Option(redis.lpop(key))})

  def llength(key: String) = this.run(redis => {redis.llen(key)})

  def lset(key: String, index: Long, value: String) = this.run(redis => {redis.lset(key, index, value)})

  def lrange(key: String, start: Long, end: Long) = this.run(redis => {redis.lrange(key, start, end).toSeq})

  def rpush[T](key: String, value: T) = this.run(redis => {redis.rpush(key, value.toString)})

  def lpush[T](key: String, value: T) = this.run(redis => {redis.lpush(key, value.toString)})

  def ltrim(key: String, start: Long, end: Long) = this.run(redis => {redis.ltrim(key, start, end)})

  def lrem(key: String, value: String, number: Long) = this.run(redis => {redis.lrem(key, number, value)})

  def zadd(key: String, score: Double, value: String) = this.run(redis => {redis.zadd(key, score, value)})

  def zrem(key: String, member: String) = this.run(redis => {redis.zrem(key, member)})

  def zremrangeByScore(key: String, start: Double, end: Double) = this.run(redis => {redis.zremrangeByScore(key, start, end)})

  def zremrangeByRank(key: String, start: Int, end: Int) = this.run(redis => {redis.zremrangeByRank(key, start, end)})

  def zrevrange(key: String, start: Int, end: Int) = this.run(redis => {redis.zrevrange(key, start, end).toSeq})

  def zrevrangeWithScores(key: String, start: Int, end: Int) = this.run(redis => {redis.zrevrangeWithScores(key, start, end).toSeq.map(t => (t.getElement(), t.getScore))})

  def zrange(key: String, start: Int, end: Int) = this.run(redis => {redis.zrange(key, start, end).toSeq})

  def zrangeWithScores(key: String, start: Int, end: Int) = this.run(redis => {redis.zrangeWithScores(key, start, end).toSeq.map(t => (t.getElement(), t.getScore))})

  def zrank(key: String, member: String) = this.run(redis => {
    redis.zrank(key, member) match {
      case rank: java.lang.Long => Some(rank)
      case _ => None
    }
  })

  def zrevrank(key: String, member: String) = this.run(redis => {
    redis.zrevrank(key, member) match {
      case rank: java.lang.Long => Some(rank)
      case _ => None
    }
  })

  def zcard(key: String) = this.run(redis => {redis.zcard(key)})

  def zscore(key: String, member: String) = this.run(redis => {
    redis.zscore(key, member) match {
      case score: java.lang.Double => Some(score)
      case _ => None
    }
  })

  def zincrBy(key: String, increment: Double, member: String) = this.run(redis => {redis.zincrby(key, increment, member)})

  def run[T](task:(ShardedJedis) => T): T = {
    val redis = pool.getResource()

    try {
      task(redis)
    } finally {
      pool.returnResource(redis)
    }
  }

  def exec(task:(Pipeline) => Unit) {
    this.run(redis => {
      val pipeline = shardedJedisPipeline(redis)
      task(new ShardedPipeline(pipeline))
      pipeline.sync()
    })
  }

  def syncAndReturnAll(task:(Pipeline) => Unit): Seq[AnyRef] = {
    this.run(redis => {
      val pipeline = shardedJedisPipeline(redis)
      task(new ShardedPipeline(pipeline))
      pipeline.syncAndReturnAll().toSeq
    })
  }

  def syncAndReturnResponses[T](task: (PipelineWrap) => T): T = {
    this.run(redis => {
      val pipeline = shardedJedisPipeline(redis)
      val responses = task(new PipelineWrap(new ShardedPipeline(pipeline)))
      pipeline.syncAndReturnAll()
      responses
    })
  }

  protected def shardedJedisPipeline(redis: ShardedJedis): ShardedJedisPipeline = {
    val pipeline = new ShardedJedisPipeline()
    pipeline.setShardedJedis(redis)
    pipeline
  }

  def shutdown = pool.destroy()
}

case class Shard(host: String, port: Int, pwd: Option[String] = None, timeout: Int = 3600000) {
  def toShardInfo(): JedisShardInfo = {
    val info = new JedisShardInfo(host, port, timeout, host+":"+port)
    pwd.foreach(password => info.setPassword(password))
    info
  }
}

object Shard {
  def toShardInfo(shards: Iterable[Shard]): Seq[JedisShardInfo] = shards.map(_.toShardInfo).toSeq
}

object ShardedRedis {
  def pool(config: JedisPoolConfig, shardInfo: Iterable[JedisShardInfo], hashing: Option[Hashing]): ShardedJedisPool = {
    hashing.map(hash => new ShardedJedisPool(config, shardInfo.toList, hash))
           .getOrElse(new ShardedJedisPool(config, shardInfo.toList))
  }
}
