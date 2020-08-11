package com.microsoft.bloomfilter

/**
  * Interface for a bloom filter that can be implemented for different types
  * @tparam T
  */
trait BloomFilter[T] {

  /**
    * Test if the passed in element is contained in the bloom filter.  May return false positives
    * depending on the size of the bloom filter and the quality of the hashing algorithm
    * @param value
    * @return
    */
  def contains(value: T) : Boolean

  /**
    * Num elements in the bloom filter
    * @return
    */
  def size : Int

  /**
    * Add an element to the bloom filter
    * @param value
    * @return
    */
  def add(value: T): BloomFilter[T]
}

object BloomFilter {
  sealed abstract trait BloomFilterSize {
    val numBytes: Int
  }

  class TwoBytes extends BloomFilterSize {
    val numBytes: Int = 2
  }
  class ThreeBytes extends BloomFilterSize {
    val numBytes: Int = 3
  }
  class FourBytes extends BloomFilterSize {
    val numBytes: Int = 4
  }
  class EightBytes extends BloomFilterSize {
    val numBytes: Int = 8
  }
  class AllBytes extends BloomFilterSize {
    val numBytes: Int = 16
  }


}
