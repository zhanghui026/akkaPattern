package config

import com.typesafe.config.ConfigFactory
import java.util.concurrent.TimeUnit

/**
 * @author zhangh
 * @version $Id: 14-2-25 下午3:38 
 */
object ConfigSample extends App {
  val str =
    """
      | static.trans = true
      |
      |
      |
      |standard-timeout = 10ms
      |
      |#
      |# nishi
      |
      |standard-timeout2 = 20ms
      |
      |foo.timeout = 1234${standard-timeout}
      |
      |bar.timeout = 1231${standard-timeout}lll #this is comments
      |
      |
      |within = 5 minuts
      |
      |direct-buffer-size = 128 KiE
      |server1.ip = "192.168.243.216"
      |server2.ip = 192.168.241.102
      |
      |mq {
      |  zeus {
      |    ip = ${server1.ip}
      |    port = 61613
      |    username = admin
      |    password = password
      |  }
      |  aims {
      |    ip = ${server1.ip}
      |    username = admin
      |    port = 61613
      |    fds = /queue/fds
      |    fdm = /topic/fdm
      |    password = password
      |  }
      |  # Gateway mq
      |  gateway {
      |    down = /queue/aftndown
      |    ip =  ${server2.ip}
      |    up = /queue/aftnup
      |    port = 61616
      |    protocol = tcp
      |  }
      |}
      |
    """.stripMargin
  val config = ConfigFactory.parseString(str)
//  println(config.getDuration("within",TimeUnit.MINUTES))
  val res = config.getString("direct-buffer-size")
  println(res)

  println("finish how it")
}
