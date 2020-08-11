package com.microsoft.bloomfilter

trait StringToBytes extends ToBytes[String] {

  def convert(str: String) : List[Byte]

}
