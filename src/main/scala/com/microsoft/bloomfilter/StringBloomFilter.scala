package com.microsoft.bloomfilter

/**
  * A String Bloom Filter that uses Scala's Set type to store List of Bytes.
  * This is a quick implementation taking advantage of scala List's ability to determine
  * quality of two lists based on their contents.  Ex List(1, 2, 3) == List(1, 2, 3) and thus would
  * be only one entry in the set
  *
  * This is a purely functional class with no mutable state
  * @param set
  * @param byteConverter
  */
class StringBloomFilter(set: Set[List[Byte]] = Set.empty, byteConverter: StringToBytes) extends BloomFilter[String] {

  override def contains(str: String): Boolean = {
    set.contains(byteConverter.convert(str))
  }

  override def size: Int = set.size

  override def add(str: String): StringBloomFilter = {
    new StringBloomFilter(set + byteConverter.convert(str), byteConverter)
  }

  def head : List[Byte] = {
    set.head
  }
}


