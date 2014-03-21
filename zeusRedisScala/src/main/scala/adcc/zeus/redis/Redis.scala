package adcc.zeus.redis

import redis.clients.jedis.JedisCommands
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import org.apache.commons.pool.impl.GenericObjectPool

trait Redis {

  def del(key: String): Long

  def get(key: String): Option[String]

  def ttl(key: String): Long

  def expire(key: String, ttl: Int): Long

  def set[T](key: String, value: T): String

  def setnx[T](key: String, value: T): Long

  def exists(key: String): Boolean

  def getset(key: String, field: String): Option[String]

  def hget(key: String, field: String): Option[String]

  def hexists(key: String, field: String): Boolean

  def hgetAll(key: String): Map[String, String]

  def hgetMany(keys: Seq[String]): Seq[Map[String, String]]

  def hincrBy(key: String, field: String, increment: Long): Long

  def hset[T](key: String, field: String, value: T): Long

  def hsetnx[T](key: String,field:String,value:T):Long

  def hdel(key: String, field: String): Long

  def hmget(key: String, fields: Seq[String]): Seq[Option[String]]

  def hmset(key: String, details: Map[String, String]): String

  def incrby(key: String, increment: Int): Long

  def incr(key: String): Long

  def put(key: String, values: Map[String, String]): String

  def smembers(key: String): Set[String]

  def scard(key: String): Long

  def sadd(key: String, value: String): Long

  def sismember(key: String, value: String): Boolean

  def srem(key: String, value: String): Long

  def lpop(key: String): Option[String]

  def llength(key: String): Long

  def lrange(key: String, start: Long, end: Long): Seq[String]

  def ltrim(key: String, start: Long, end: Long): String

  def lrem(key: String, value: String, number: Long): Long

  def lset(key: String, index: Long, value: String): String

  def rpush[T](key: String, value: T): Long

  def lpush[T](key: String, value: T): Long

  def zadd(key: String, score: Double, value: String): Long

  def zrem(key: String, member: String): Long

  def zremrangeByScore(key: String, start: Double, end: Double): Long

  def zremrangeByRank(key: String, start: Int, end: Int): Long

  def zrevrange(key: String, start: Int, end: Int): Seq[String]

  def zrevrangeWithScores(key: String, start: Int, end: Int): Seq[(String, Double)]

  def zrange(key: String, start: Int, end: Int): Seq[String]

  def zrangeWithScores(key: String, start: Int, end: Int): Seq[(String, Double)]

  def zrank(key: String, member: String): Option[Long]

  def zrevrank(key: String, member: String): Option[Long]

  def zcard(key: String): Long

  def zscore(key: String, member: String): Option[Double]

  def zincrBy(key: String, increment: Double, member: String): Double

  def exec(task: (Pipeline) => Unit)

  def syncAndReturnAll(task: (Pipeline) => Unit): Seq[AnyRef]

  def syncAndReturnAllAs[A : Manifest](task: (Pipeline) => Unit): Seq[A] = syncAndReturnAll(task).map(value => RedisJavaMapping.as[A](value))

  def syncAndReturnResponses[T](task: (PipelineWrap) => T): T

  def syncAndReturn1[A](task:(PipelineWrap) => (ScalaResponse[A])): A = syncAndReturn(task)

  def syncAndReturn[A](task:(PipelineWrap) => (ScalaResponse[A])): A = {
    val (a) = syncAndReturnResponses(task)
    a.get
  }
  def syncAndReturn2[A, B](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B])): (A, B) = syncAndReturn(task)

  def syncAndReturn[A, B](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B])): (A, B) = {
    val (a, b) = syncAndReturnResponses(task)
    (a.get, b.get)
  }

  def syncAndReturn3[A, B, C](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C])): (A, B, C) = syncAndReturn(task)

  def syncAndReturn[A, B, C](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C])): (A, B, C) = {
    val (a, b, c) = syncAndReturnResponses(task)
    (a.get, b.get, c.get)
  }

  def syncAndReturn4[A, B, C, D](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D])): (A, B, C, D) = syncAndReturn(task)

  def syncAndReturn[A, B, C, D](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D])): (A, B, C, D) = {
    val (a, b, c, d) = syncAndReturnResponses(task)
    (a.get, b.get, c.get, d.get)
  }

  def syncAndReturn5[A, B, C, D, E](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E])): (A, B, C, D, E) = syncAndReturn(task)

  def syncAndReturn[A, B, C, D, E](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E])): (A, B, C, D, E) = {
    val (a, b, c, d, e) = syncAndReturnResponses(task)
    (a.get, b.get, c.get, d.get, e.get)
  }

  def syncAndReturn6[A, B, C, D, E, F](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E], ScalaResponse[F])): (A, B, C, D, E, F) = syncAndReturn(task)

  def syncAndReturn[A, B, C, D, E, F](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E], ScalaResponse[F])): (A, B, C, D, E, F) = {
    val (a, b, c, d, e, f) = syncAndReturnResponses(task)
    (a.get, b.get, c.get, d.get, e.get, f.get)
  }

  def syncAndReturn7[A, B, C, D, E, F, G](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E], ScalaResponse[F], ScalaResponse[G])): (A, B, C, D, E, F, G) = syncAndReturn(task)

  def syncAndReturn[A, B, C, D, E, F, G](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E], ScalaResponse[F], ScalaResponse[G])): (A, B, C, D, E, F, G) = {
    val (a, b, c, d, e, f, g) = syncAndReturnResponses(task)
    (a.get, b.get, c.get, d.get, e.get, f.get, g.get)
  }

  def syncAndReturn8[A, B, C, D, E, F, G, H](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E], ScalaResponse[F], ScalaResponse[G], ScalaResponse[H])): (A, B, C, D, E, F, G, H) = syncAndReturn(task)

  def syncAndReturn[A, B, C, D, E, F, G, H](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E], ScalaResponse[F], ScalaResponse[G], ScalaResponse[H])): (A, B, C, D, E, F, G, H) = {
    val (a, b, c, d, e, f, g, h) = syncAndReturnResponses(task)
    (a.get, b.get, c.get, d.get, e.get, f.get, g.get, h.get)
  }

  def syncAndReturn9[A, B, C, D, E, F, G, H, I](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E], ScalaResponse[F], ScalaResponse[G], ScalaResponse[H], ScalaResponse[I])): (A, B, C, D, E, F, G, H, I) = syncAndReturn(task)

  def syncAndReturn[A, B, C, D, E, F, G, H, I](task:(PipelineWrap) => (ScalaResponse[A], ScalaResponse[B], ScalaResponse[C], ScalaResponse[D], ScalaResponse[E], ScalaResponse[F], ScalaResponse[G], ScalaResponse[H], ScalaResponse[I])): (A, B, C, D, E, F, G, H, I) = {
    val (a, b, c, d, e, f, g, h, i) = syncAndReturnResponses(task)
    (a.get, b.get, c.get, d.get, e.get, f.get, g.get, h.get, i.get)
  }

  def shutdown
}

object Redis {
  def config(poolSize: Int): JedisPoolConfig = {
    val config = new JedisPoolConfig()
    config.setTestOnBorrow(true)
    config.setTestWhileIdle(true)
    config.setMaxActive(poolSize)
    config.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK)
    config
  }

  def pool(config: JedisPoolConfig, host: String, port: Int, pwd: Option[String], timeout: Int): JedisPool = {
    pwd.map(password => new JedisPool(config, host, port, timeout, password))
       .getOrElse(new JedisPool(config, host, port, timeout))
  }
}

