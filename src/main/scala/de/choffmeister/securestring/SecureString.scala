package de.choffmeister.securestring

import java.util.Random
import java.util.Arrays
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.nio._

/**
 * See http://sanguinecomputing.com/a-secure-string-implementation-for-long-term-storage-of-sensitive-data-in-java/
 */
class SecureString(plain: Array[Byte]) {
  private val seed = SecureString.random.nextLong
  private val shaked = shake(plain)
  SecureString.clear(plain)

  def read[T](inner: Array[Char] => T): T = {
    val bytes = shake(shaked)
    try {
      val chars = SecureString.decode(bytes)
      try {
        inner(chars)
      } finally {
        SecureString.clear(chars)
      }
    } finally {
      SecureString.clear(bytes)
    }
  }

  def readBytes[T](inner: Array[Byte] => T): T = {
    val bytes = shake(shaked)
    try {
      inner(bytes)
    } finally {
      SecureString.clear(bytes)
    }
  }

  private def shake(bytes: Array[Byte]): Array[Byte] = {
    val rnd = new Random(SecureString.globalSeed ^ seed)
    val result = new Array[Byte](bytes.length)

    for (i <- 0 until bytes.length) {
      val int = rnd.nextInt
      val b = (((int >> 24) & 0xff) ^ ((int >> 16) & 0xff) ^ ((int >> 8) & 0xff) ^ ((int >> 0) & 0xff)).toByte

      result(i) = (bytes(i) ^ b).toByte
    }

    result
  }

  override def equals(o: Any): Boolean = o match {
    case other: SecureString =>
      read { plain1 =>
        other.read { plain2 =>
          SecureString.compare(plain1, plain2)
        }
      }
    case _ =>
      return false
  }
}

object SecureString {
  private val random = new Random()
  private val codec = charset.Charset.forName("UTF-8")

  def apply(chars: Array[Char]): SecureString = {
    val bytes = encode(chars)
    val secure = new SecureString(bytes)
    clear(chars)
    secure
  }

  private def clear(bytes: Array[Byte]) {
    Arrays.fill(bytes, 0x00.toByte)
  }

  private def clear(chars: Array[Char]) {
    Arrays.fill(chars, '\0')
  }

  private def encode(chars: Array[Char]): Array[Byte] = {
    val wrapped = CharBuffer.wrap(chars)
    val encoded = codec.encode(wrapped)
    val array = encoded.array.drop(encoded.position).take(encoded.limit - encoded.position)
    clear(encoded.array)
    array
  }

  private def decode(bytes: Array[Byte]): Array[Char] = {
    val wrapped = ByteBuffer.wrap(bytes)
    val decoded = codec.decode(wrapped)
    val array = decoded.array.drop(decoded.position).take(decoded.limit - decoded.position)
    clear(decoded.array)
    array
  }

  private def compare[T](bytes1: Array[T], bytes2: Array[T]): Boolean = {
    if (bytes1.length != bytes2.length) false
    else {
      for (i <- 0 until bytes1.length) {
        if (bytes1(i) != bytes2(i)) return false
      }
      return true
    }
  }

  /**
   * Returns a seed based on the Java VM start time and the instantiation time of a singleton
   * object. This seed is
   * (a) stable over the lifetime of the Java VM,
   * (b) not receivable from a memory dump and
   * (c) not trivial to receive from process information.
   */
  private def globalSeed: Long = ((seedObject.hashCode.toLong << 32) | (seedObject.hashCode.toLong)) ^ seedStartTime

  private def seedStartTime: Long = ManagementFactory.getRuntimeMXBean().getStartTime()

  private lazy val seedObject: Any = new Object()
}
