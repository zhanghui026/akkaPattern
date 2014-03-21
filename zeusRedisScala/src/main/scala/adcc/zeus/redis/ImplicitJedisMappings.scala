package adcc.zeus.redis


import scala.collection.JavaConverters._
import redis.clients.jedis.{Response => JedisResponse}
import java.lang.{String => JavaString}
import redis.clients.jedis.{Tuple => JedisTuple}

object ImplicitJedisMappings {

  implicit def optionConverter[X, Y](x: X)(implicit converter: X => Y): Option[Y] =
    Option(x).map(converter)

  implicit def doubleToOption(l: java.lang.Double)(implicit toScalaDouble: java.lang.Double => Double): Option[Double] =
    Option(l).map(toScalaDouble)

  implicit def longToOption(l: java.lang.Long)(implicit toScalaLong: java.lang.Long => Long): Option[Long] =
    Option(l).map(toScalaLong)

  implicit def stringToOption(x: JavaString)(implicit converter: JavaString => String): Option[String] =
    Option(x).map(converter)

  implicit def jedisToScalaResponse[ST, JT](response: JedisResponse[JT])(implicit converter: JT => ST): ScalaResponse[ST] =
    ScalaResponse.wrapResponse[ST, JT](response, converter)

  implicit def jedisTupleToScalaTuple(j: JedisTuple)(implicit f: JavaString => String): (String, Double) = (f(j.getElement()), j.getScore())

  implicit def setToScalaSet[T](s: java.util.Set[T]): Set[T] =
    s.asScala.toSet

  implicit def mapToScalaMap[K, V](s: java.util.Map[K, V]): Map[K, V] =
    s.asScala.toMap

  implicit def listToSeq[T](s: java.util.List[T]): Seq[T] =
    s.asScala

  implicit def listToSeqOfOptions(s: java.util.List[JavaString]): Seq[Option[String]] =
    s.asScala.map(stringToOption)

}
