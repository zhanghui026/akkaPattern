import adcc.zeus.redis.{ShardedRedis, SingleRedis}
import scala.io.Source
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol

import java.io._

import redis.clients.jedis.JedisShardInfo
import redis.clients.jedis.ShardedJedisPool
import scala.collection.JavaConversions._

object RedisTestHelper {
  val PASSWORD = "onerecruit"
  private val config = new JedisPoolConfig();
  config.setTestOnBorrow(true)

  private val shards = List.newBuilder[JedisShardInfo];
  private val shard1 = new JedisShardInfo("localhost", Protocol.DEFAULT_PORT+100);
  shard1.setPassword(PASSWORD)
  private val shard2 = new JedisShardInfo("localhost", Protocol.DEFAULT_PORT+101);
  shard2.setPassword(PASSWORD)
  
  val redis = new SingleRedis(new JedisPool(config, "localhost", Protocol.DEFAULT_PORT+100, 2000, PASSWORD))
  val redis2 = new SingleRedis(new JedisPool(config, "localhost", Protocol.DEFAULT_PORT+101, 2000, PASSWORD))
  
  val shardedRedis = new ShardedRedis(new ShardedJedisPool(config, List(shard1, shard2)))
}

trait RedisTestHelper {
  val redis = RedisTestHelper.redis
  val redis2 = RedisTestHelper.redis2
  val shardedRedis = RedisTestHelper.shardedRedis
  
  var redisIsRunning = false
  
  val redisConf = List(
      "daemonize yes",
      "pidfile /tmp/test-redis.pid",
      "port 6479",
      "timeout 300",
      "loglevel verbose",
      "logfile stdout",
      "databases 16",
      "save 900 1",
      "save 300 10",
      "save 60 10000",
      "rdbcompression yes",
      "dbfilename dump.rdb",
      "dir /tmp/test-redis",
      "appendonly no",
      "appendfsync everysec",
      "activerehashing yes",
      "requirepass "+RedisTestHelper.PASSWORD)
      
   val redisConf2 = List(
      "daemonize yes",
      "pidfile /tmp/test-redis-2.pid",
      "port 6480",
      "timeout 300",
      "loglevel verbose",
      "logfile stdout",
      "databases 16",
      "save 900 1",
      "save 300 10",
      "save 60 10000",
      "rdbcompression yes",
      "dbfilename dump.rdb",
      "dir /tmp/test-redis-2",
      "appendonly no",
      "appendfsync everysec",
      "activerehashing yes",
      "requirepass "+RedisTestHelper.PASSWORD)

  def ensureRedis() {
    try {
      redis.ping()
      redis2.ping()
      redisIsRunning = true
      return
    } catch {
      case e: Exception => {println("redis is not running, attempting to start")}
    }
    
    val tmpDir = new File("/tmp/test-redis/")
    tmpDir.mkdirs()
    val tmpDir2 = new File("/tmp/test-redis-2/")
    tmpDir2.mkdirs()
    val confFile = new File("/tmp/test-redis/redis.conf")
    confFile.createNewFile()
    printToFile(confFile)(p => {
      redisConf.foreach(p.println)
    })
    val confFile2 = new File("/tmp/test-redis/redis2.conf")
    confFile2.createNewFile()
    printToFile(confFile2)(p => {
      redisConf2.foreach(p.println)
    })
    
    var serverLoc = "/usr/bin/"
    if(! run("ls " + serverLoc).contains("redis-server")) {
      serverLoc = "/usr/local/bin/"
    }
    run(serverLoc + "redis-server /tmp/test-redis/redis.conf", false)
    run(serverLoc + "redis-server /tmp/test-redis/redis2.conf", false)
    
    val startTime = System.currentTimeMillis()
    while(System.currentTimeMillis() - startTime < 2000 && !redisIsRunning) {
      try {
        redis.ping()
        redis2.ping()
        redisIsRunning = true
        return
      } catch {
        case e: Exception => {
          e.printStackTrace()
          println("redis is not running ...")}
      }
      Thread.sleep(100)
    }
  }

  private def run(cmd: String, wait: Boolean = true): List[String] = {
    try {
      val p = Runtime.getRuntime().exec(cmd)
      if(wait) p.waitFor()
      return Source.fromInputStream(p.getInputStream()).getLines().toList
    } catch {
      case e: Throwable => e.printStackTrace()
    }
    
    List[String]()
  }
  
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
}