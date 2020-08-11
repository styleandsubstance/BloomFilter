package com.microsoft.bloomfilter

import java.security.MessageDigest

import com.microsoft.bloomfilter.BloomFilter._

/**
  * This method uses the very fast md5 hashing algorithm on strings to convert them
  * to a list of bytes for a Bloom Filter
  * @param numBytes
  * @tparam T
  */
class Md5StringToBytes[T <: BloomFilterSize](numBytes: T) extends StringToBytes {
  val md5Hash = MessageDigest.getInstance("MD5")

  /**
    * Convert the string to a list of bytes and take the first n bytes from the list
    * based on the type parameter passed in
    * @param str
    * @return
    */
  def convert(str: String) : List[Byte] = {
    val md5 = md5Hash.digest(str.getBytes()).toList
    md5.take(numBytes.numBytes)
  }
}

object Md5StringToBytes {
  def XSMALL = new Md5StringToBytes(new TwoBytes)
  def SMALL = new Md5StringToBytes(new ThreeBytes)
  def MEDIUM = new Md5StringToBytes(new FourBytes)
  def LARGE = new Md5StringToBytes(new EightBytes)
  def XLARGE = new Md5StringToBytes(new AllBytes)
}



