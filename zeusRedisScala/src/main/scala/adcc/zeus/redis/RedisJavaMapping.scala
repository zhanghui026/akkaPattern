package adcc.zeus.redis

import scala.collection.JavaConversions._

object RedisJavaMapping {
  val STRING     = manifest[String]
  val OPT_STRING = manifest[Option[String]]
  val SEQ_STRING = manifest[Seq[String]]
  val SET_STRING = manifest[Set[String]]
  val MAP_STRING = manifest[Map[String, String]]
  val MAP_DOUBLE = manifest[Map[String, Double]]
  val LONG       = manifest[Long]
  val BOOLEAN    = manifest[Boolean]
  val OPT_DOUBLE = manifest[Option[Double]]
  val DOUBLE     = manifest[Double]
  val NOTHING    = manifest[Nothing]
  
  def as[M : Manifest](value: Any): M = {
    manifest[M] match {
      case STRING     if (value.isInstanceOf[java.lang.String]) =>
        value.asInstanceOf[String].asInstanceOf[M]
      case OPT_STRING if (value == null || value.isInstanceOf[java.lang.String]) => 
        Option(value.asInstanceOf[java.lang.String]).asInstanceOf[M]
      case LONG       if (value == null || value.isInstanceOf[java.lang.Long]) => 
        Option(value.asInstanceOf[java.lang.Long]).map(_.toLong).getOrElse(0).asInstanceOf[M]
      case BOOLEAN    if (value == null || value.isInstanceOf[java.lang.Long]) => 
        Option(value.asInstanceOf[java.lang.Long]).map(_ > 0).getOrElse(false).asInstanceOf[M]
      case BOOLEAN    if (value == null || value.isInstanceOf[java.lang.Boolean]) => 
        Option(value.asInstanceOf[java.lang.Boolean]).getOrElse(false).asInstanceOf[M]
      case DOUBLE     if (value == null || value.isInstanceOf[java.lang.Double]) => 
        Option(value.asInstanceOf[java.lang.Double]).map(_.toDouble).getOrElse(0.0d).asInstanceOf[M]
      case OPT_DOUBLE if (value == null || value.isInstanceOf[java.lang.Double]) => 
        Option(value.asInstanceOf[java.lang.Double]).map(_.toDouble).asInstanceOf[M]
      case SEQ_STRING if (value == null || value.isInstanceOf[java.util.ArrayList[_]]) => 
        Option(value.asInstanceOf[java.util.ArrayList[String]]).map(_.toSeq).getOrElse(Seq[String]()).asInstanceOf[M]
      case SEQ_STRING if (value.isInstanceOf[java.util.LinkedHashSet[_]]) => 
        Option(value.asInstanceOf[java.util.LinkedHashSet[String]]).map(_.toSeq).getOrElse(Seq[String]()).asInstanceOf[M]
      case SET_STRING if (value == null || value.isInstanceOf[java.util.HashSet[_]]) => 
        Option(value.asInstanceOf[java.util.HashSet[String]]).map(_.toSet).getOrElse(Set[String]()).asInstanceOf[M]
      case MAP_STRING if (value == null || value.isInstanceOf[java.util.Map[_, _]]) => 
        Option(value.asInstanceOf[java.util.Map[String, String]]).map(_.toMap).getOrElse(Map[String, String]()).asInstanceOf[M]
      case MAP_DOUBLE if (value == null || value.isInstanceOf[java.util.LinkedHashSet[_]]) => 
        Option(value.asInstanceOf[java.util.LinkedHashSet[_root_.redis.clients.jedis.Tuple]]).map(_.toSeq.map(e => e.getElement() -> e.getScore()).toMap).getOrElse(Map[String, Double]()).asInstanceOf[M]
      case NOTHING    => Nil.asInstanceOf[M]
      case m          => throw new InvalidMappingException(m, Option(value).map(_.getClass))
    }
  }
  
}

case class InvalidMappingException(requestedType: Manifest[_], valueType: Option[Class[_]]) extends RuntimeException("Cannot map type of "+requestedType+" with value of type: "+valueType)