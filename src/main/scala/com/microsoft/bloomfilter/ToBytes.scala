package com.microsoft.bloomfilter

/**
  * Interface for a certain type to coverted to a list of bytes
  * @tparam T
  */
trait ToBytes[T] {

  def convert(str: T) : List[Byte]

}
