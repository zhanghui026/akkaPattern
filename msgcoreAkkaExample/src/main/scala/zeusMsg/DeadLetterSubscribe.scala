package zeusMsg
import akka.actor.{ Actor, DeadLetter, Props }
import org.slf4j.LoggerFactory
import akka.event.Logging

/**
 * @author zhangh
 * @version $Id: 14-3-10 下午1:18
 */
class DeadLetterSubscribe extends Actor {
  val log = Logging(context.system,this)
  override def receive: Actor.Receive = {
    case d:DeadLetter =>
      log.error(d.sender+" send msg to "+ d.recipient + " not suc,msg Info:"+d.message.toString)

  }
}
