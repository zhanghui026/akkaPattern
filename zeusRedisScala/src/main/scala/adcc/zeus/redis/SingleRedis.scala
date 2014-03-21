package adcc.zeus.redis

import redis.clients.jedis.JedisPool
import scala.collection.JavaConversions._
import redis.clients.jedis.Jedis
import redis.clients.jedis.BinaryJedis
import redis.clients.jedis.Transaction
import redis.clients.jedis.JedisPoolConfig
import org.apache.commons.pool.impl.GenericObjectPool
import adcc.zeus.redis.Redis._

class SingleRedis(pool: JedisPool) extends Redis {

  def this(host: String, port: Int, pwd: Option[String] = None, timeout: Int = 3600000, poolSize: Int = 60) = this(pool(config(poolSize), host, port, pwd, timeout))

  def ping() = this.run(redis => {redis.ping()})

  def del(key: String) = this.run(redis => {redis.del(key)})

  def get(key: String) = this.run(redis => {Option(redis.get(key))})

  def ttl(key: String) = this.run(redis => {redis.ttl(key)})

  def expire(key: String, ttl: Int) = this.run(redis => {redis.expire(key, ttl)})

  def eval(script: String, keyCount: Int, args: String*): Any = this.run(redis => {redis.eval(script, keyCount, args: _*)})

  def eval(script: String): Any = eval(script, 0)

  def evalsha(script: String, keyCount: Int, args: String*): Any = this.run(redis => {redis.evalsha(script, keyCount, args: _*)})

  def evalsha(hash: String): Any = evalsha(hash, 0)

  def set[T](key: String, value: T) = this.run(redis => {redis.set(key, value.toString)})

  def setnx[T](key: String, value: T) = this.run(redis => {redis.setnx(key, value.toString)})

  def exists(key: String) = this.run(redis => {redis.exists(key)})

  def mget(keys: Seq[String]) = this.run(redis => redis.mget(keys:_*).map(Option.apply))

  def mset(keyvalues: Seq[(String, String)]) = this.run(redis => redis.mset(keyvalues.flatMap { case (k,v) => Seq(k,v) }:_*))

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

  def keys(pattern: String) = this.run(redis => {redis.keys(pattern).toSet})

  def put(key: String, values: Map[String, String]) = this.run(redis => {redis.hmset(key, values)})

  def smembers(key: String) = this.run(redis => {redis.smembers(key).toSet})

  def scard(key: String) = this.run(redis => {redis.scard(key)})

  def sadd(key: String, value: String) = this.run(redis => {redis.sadd(key, value)})

  def srem(key: String, value: String) = this.run(redis => {
    redis.srem(key, value)
  })

  def sismember(key: String, value: String) = this.run(redis => {redis.sismember(key, value)})

  def scriptLoad(script: String) = this.run(redis => {redis.scriptLoad(script)})

  def lpop(key: String) = this.run(redis => {Option(redis.lpop(key))})

  def brpop(timeout: Int, keys: Seq[String]) = this.run(redis => {redis.brpop(timeout, keys : _*).toSeq})

  def llength(key: String) = this.run(redis => {redis.llen(key)})

  def lrange(key: String, start: Long, end: Long) = this.run(redis => {redis.lrange(key, start, end).toSeq})

  def ltrim(key: String, start: Long, end: Long) = this.run(redis => {redis.ltrim(key, start, end)})

  def lset(key: String, index: Long, value: String) = this.run(redis => {redis.lset(key, index, value)})

  def lrem(key: String, value: String, number: Long) = this.run(redis => {redis.lrem(key, number, value)})

  def rpush[T](key: String, value: T) = this.run(redis => {redis.rpush(key, value.toString)})

  def lpush[T](key: String, value: T) = this.run(redis => {redis.lpush(key, value.toString)})

  def zadd(key: String, score: Double, value: String) = this.run(redis => {redis.zadd(key, score, value)})

  def zrem(key: String, member: String) = this.run(redis => {redis.zrem(key, member)})

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

  def zremrangeByScore(key: String, start: Double, end: Double) = this.run(redis => {redis.zremrangeByScore(key, start, end)})

  def zremrangeByRank(key: String, start: Int, end: Int) = this.run(redis => {redis.zremrangeByRank(key, start, end)})

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

  def zinterStore(key: String, strings: Seq[String]) = this.run(redis => {redis.zinterstore(key, strings: _*)})

  def zunionStore(key: String, strings: Seq[String]) = this.run(redis => {redis.zunionstore(key, strings: _*)})

  def flushAll = this.run(redis => {redis.flushAll()})

  def run[T](task:(Jedis) => T): T = {
    val redis = pool.getResource()

    try {
      task(redis)
    } finally {
      pool.returnResource(redis.asInstanceOf[BinaryJedis]) // Resolved an issue upgrading to Jedis 2.1.0
    }
  }

  def watch[T](keys: Seq[String], task: (Jedis) => T) = this.run(redis => {
    redis.watch(keys : _*)
    task(redis)
  })

  def exec(task:(Pipeline) => Unit) {
    this.run(redis => {

      val pipeline = redis.pipelined()

      task(new SinglePipeline(pipeline))

      pipeline.sync()
    })
  }

    def execSingle(task:(SinglePipeline) => Unit) {
    this.run(redis => {
      val pipeline = redis.pipelined()

      task(new SinglePipeline(pipeline))

      pipeline.sync()
    })
  }

  def syncAndReturnAll(task:(Pipeline) => Unit): Seq[AnyRef] = {
    this.run(redis => {
      val pipeline = redis.pipelined()

      task(new SinglePipeline(pipeline))

      pipeline.syncAndReturnAll().toSeq
    })
  }

  def syncAndReturnAllSingle(task:(SinglePipeline) => Unit): Seq[AnyRef] = {
    this.run(redis => {
      val pipeline = redis.pipelined()

      task(new SinglePipeline(pipeline))

      pipeline.syncAndReturnAll().toSeq
    })
  }

  def syncAndReturnResponses[T](task: (PipelineWrap) => T): T = {
    this.run(redis => {
      val pipeline = redis.pipelined()
      val responses = task(new PipelineWrap(new SinglePipeline(pipeline)))
      pipeline.syncAndReturnAll()
      responses
    })
  }

  def shutdown = pool.destroy()
}
