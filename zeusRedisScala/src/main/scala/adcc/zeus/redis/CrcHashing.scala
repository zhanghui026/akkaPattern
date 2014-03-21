package com.top10.redis

import redis.clients.util.Hashing
import java.util.zip.CRC32

class CrcHashing extends Hashing {
  
  def hash(toHash: String): Long = hash(toHash.getBytes())

  def hash(toHash: Array[Byte]): Long = {
    val crc = new CRC32()
    crc.update(toHash)
    crc.getValue()
  }
}