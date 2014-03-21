package extractor

/**
 * @author zhangh
 * @version $Id: 14-2-20 上午9:25 
 */
trait User {
  def name:String
  def score :Int
}

class FreeUser(val name:String,val score:Int,val upgradeProbability:Double) extends User
object FreeUser {

  def unapply(user:FreeUser):Option[(String,Int,Double)] = Some(user.name,user.score,user.upgradeProbability)

}
class Premiumuser(val name:String,val score:Int) extends User
object Premiumuser {
  def unapply(user:Premiumuser):Option[(String,Int)]= Some((user.name,user.score))
}

object testExtractor extends App {
//  val user:User = new Premiumuser("Dinel",3000,0.7d)
//  val st = user match {
//    case FreeUser(name,_,p) => "Hello " +name
//    case Premiumuser(name) => "welcome back,dear "+ name
//  }
//
//  println(st)
}
