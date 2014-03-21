package zeusMsg



import akka.actor.{DeadLetter, Actor, ActorSystem, Props}
import akka.routing.FromConfig
import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigFactory
import scala.util.Random

//初始报文
case class OriMsg(msg:String)
//PretreatmentMsg结果
case class PretreatMsg(id:Long,msgType:String,msg:String,res:String)
case class IdentifierMessage(val msgType:String, val id: Long, val s: String)
case class ResultMsg(id:Long,msg:String,res:String)
case class ParsedMsg(id:Long,msg:String,res:String,s:String) //可以解析的
case class TriggerMsg(id:Long,msg:String,res:String)
case class ResultListenerMsg(id:Long,msg:String,res:String,desc:String)
/**
 * Msg处理类 1. pretreatment 2.parserWorker  3.
 */
class MsgMaster extends Actor {
  override def receive: Actor.Receive = {
    case msg:OriMsg => {
      // 发送消息到 pretreatmentRouter
      MsgDispacher.pretreatmentRouter ! msg
    }
    case PretreatMsg(id:Long,"PARSE",msg:String,res:String) => {
      val r = Random.nextInt(3) ;
      val fakeType = "AFTN" :: "SITA" :: "WEATHTER" :: Nil;
       //Parse
      context.actorSelection("/parserRouter") ! IdentifierMessage(fakeType(r),id,msg)
    }
    case PretreatMsg(id:Long,"RESULT",msg:String,res:String) => {
      context.actorSelection("/resultRouter") ! ResultMsg(id,msg,res)
    }
    case pm:ParsedMsg =>
    {
       //Trigger
      context.actorSelection("/triggerRouter") ! TriggerMsg(pm.id,pm.msg,pm.res)
    }
    case rs:ResultMsg  =>
       context.actorSelection("/resultRouter") ! rs
    case res: ResultListenerMsg =>
       println("给出所有的ResultListenerMsg")
  }
}

class PretreatmentWorker extends Actor {
  override def receive: Actor.Receive = {
    case msg:OriMsg =>
      println("EEFFFF")
      val pretreatment = doPretreatment(msg)
      context.actorSelection("/messageRouter") ! pretreatment
  }
  def doPretreatment(msg:OriMsg) = {
    val longId = Random.nextLong()
    val msgType = if(Random.nextBoolean()) "PARSE" else "RESULT"
    PretreatMsg(longId,msgType,msg.msg,longId+"")
  }
}


class ParserWorker extends Actor {
  override def receive: Actor.Receive = {
    case im:IdentifierMessage => {
      if (im.msgType == "PARSE") {
        val rsMsg = parseMsg(im)
        context.actorSelection("/messageRouter") ! rsMsg
      } else{
        val rsMsg = resultMsg(im)
        context.actorSelection("/messageRouter") ! rsMsg
      }
    }
  }
  def parseMsg (idMsg:IdentifierMessage) = {
    ParsedMsg(idMsg.id,idMsg.s,idMsg.msgType,"Parsed")
  }
  def resultMsg(idMsg:IdentifierMessage) ={
    ResultMsg(idMsg.id,idMsg.s,"Result")
  }
}


class TriggerWorker extends Actor {
  override def receive: Actor.Receive = {
    //各种trigger
    case pm:ParsedMsg =>
      context.actorSelection("/messageRouter") !  ResultMsg(pm.id,pm.msg,"TriggerResult")
  }
}

class ResultWorker extends Actor {
  override def receive: Actor.Receive = {
    case rm:ResultMsg => {
         context.actorSelection("/messageRouter") ! ResultListenerMsg(rm.id,rm.msg,"MsgRouter","MsgListener")
    }
  }
}
/**
 * Singleton msg dispacher
 */
object MsgDispacher {
  val SYSTEM_NAME = "zeus"
  private val logger = LoggerFactory.getLogger("zeus.msg.MessageDispatcher")
  val system =  ActorSystem(SYSTEM_NAME,ConfigFactory.parseResources("akka.conf"))

  val messageRouter = system.actorOf(FromConfig.props(Props[MsgMaster]), "messageRouter") //一条消息处理过程
  val pretreatmentRouter = system.actorOf(FromConfig.props(Props[PretreatmentWorker]), "pretreatmentRouter") //一条消息处理过程
  val parserRouter = system.actorOf(FromConfig.props(Props[ParserWorker]), "parserRouter")
  val triggerRouter = system.actorOf(FromConfig.props(Props[TriggerWorker]), "triggerRouter")
  val resultRouter = system.actorOf(FromConfig.props(Props[ResultWorker]), "resultRouter")
  val listener = system.actorOf(Props(classOf[DeadLetterSubscribe]))
  system.eventStream.subscribe(listener, classOf[DeadLetter])

  val startTime = System.currentTimeMillis()

  /**
   *
   * @param msg
   */
  def dispatcher(msg: String) = {

    beforeDispacher(msg)
     // msgRouter 将msg 发给所有msgWorker
    messageRouter ! OriMsg(msg)   //并发处理OriMsg
  }


  def beforeDispacher(msg:String) = {
    //进入zookeeper的入口进行计数,发送到计数持久化
    //计数节点

  }

}
