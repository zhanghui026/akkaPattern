package redisclient

/**
 * @author zhangh
 * @version $Id: 14-2-24 下午2:22 
 */
object RedisUsing extends App {

  //connect to remote redis
  import com.redis._
  val redisclient = new RedisClient("192.168.243.217",6379)

  redisclient.set("key","Some value")
  println(redisclient.get[String]("key"))

}
