package adcc.zeus.redis

import redis.clients.jedis.{Response => JedisResponse}

case class ScalaResponse[ST](promiseOfST: () => ST) {

  def get: ST = promiseOfST()

}

object ScalaResponse {

  def wrapResponse[ST, JT](jResponse: JedisResponse[JT], converter: JT => ST):ScalaResponse[ST] =
    ScalaResponse( () => converter(jResponse.get))

}
