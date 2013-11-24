package de.choffmeister.securestring

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class SecureStringSpec extends Specification {
  "clear input arrays" in {
    val chars = "test".toCharArray
    chars.mkString === "test"
    val ss1 = SecureString(chars)
    chars.mkString === "\0\0\0\0"
    ss1.read(_.mkString === "test")

    val bytes = "test2".getBytes("UTF-8")
    val ss2 = new SecureString(bytes)
    bytes.toList === List[Byte](0, 0, 0, 0, 0)
    ss2.read(_.mkString === "test2")
  }

  "detect equality" in {
    val ss1 = SecureString("test".toCharArray)
    val ss2 = SecureString("test".toCharArray)
    val ss3 = SecureString("test1".toCharArray)
    val ss4 = SecureString("test2".toCharArray)

    ss1 === ss2
    ss1 !== ss3
    ss3 !== ss4
    ss1 !== new Object()
    ss1 !== 1
    ss1 !== "test"
  }

  "allow temporary reading" in {
    val ss1 = SecureString("test1".toCharArray)
    val ss2 = SecureString("test2".toCharArray)

    ss1.read(_ === "test1".toCharArray)
    ss2.read(_ === "test2".toCharArray)
  }
}
