import java.security.MessageDigest

import com.microsoft.{MicrosoftBloomFilter, bloomfilter}
import com.microsoft.bloomfilter.{BloomFilter, Md5StringToBytes, StringBloomFilter}
import org.scalatest._

import scala.io.Source

class BloomFilterTests extends FlatSpec {

  val testWord = "hello"
  val secondWord = "dictionary"

  "A StringBloomFilter" should "contain the md5 hash" in {

    val bloomFilter = new StringBloomFilter(byteConverter = Md5StringToBytes.XLARGE)
    val updatedBloomFilter = bloomFilter.add(testWord)

    val md5Hash = MessageDigest.getInstance("MD5")



    assert(updatedBloomFilter.head.size == new bloomfilter.BloomFilter.AllBytes().numBytes)
    assert(updatedBloomFilter.head == md5Hash.digest(testWord.getBytes()).toList)
    assert(updatedBloomFilter.contains(testWord))
    assert(updatedBloomFilter.size == 1)
    assert(updatedBloomFilter.contains(secondWord) == false)
  }

  it should "add in a second entry" in {
    val bloomFilter = new StringBloomFilter(byteConverter = Md5StringToBytes.XLARGE)
    val updatedBloomFilter = bloomFilter.add(testWord)
    val secondUpdatedBloomFilter = updatedBloomFilter.add(secondWord)

    assert(secondUpdatedBloomFilter.size == 2)
    assert(secondUpdatedBloomFilter.contains(testWord))
    assert(secondUpdatedBloomFilter.contains(secondWord))
  }

  it should "not exceed the max capacity" in {

    val xSmallBloomFilter = MicrosoftBloomFilter.readData("wordlist.txt",
      () => new StringBloomFilter(byteConverter = Md5StringToBytes.XSMALL))
    assert(xSmallBloomFilter.size == 65170)
  }

  it should "return true for all known inputs" in {

    val xSmallBloomFilter = MicrosoftBloomFilter.readData("wordlist.txt",
      () => new StringBloomFilter(byteConverter = Md5StringToBytes.XSMALL))

    assert(Source.fromResource("wordlist.txt").getLines().map(y => {
      xSmallBloomFilter.contains(y)
    }).forall(_ == true))
  }

  it should "have false positives on a small Bloom Filter" in {

    val xSmallBloomFilter = MicrosoftBloomFilter.readData("wordlist.txt",
      () => new StringBloomFilter(byteConverter = Md5StringToBytes.XSMALL))
    assert(xSmallBloomFilter.contains(testWord))
    assert(xSmallBloomFilter.contains("asasdkjalkdfjalskdfja"))
  }
}
