package com.microsoft

import com.microsoft.bloomfilter.{BloomFilter, Md5StringToBytes, StringBloomFilter}

import scala.io.Source

object MicrosoftBloomFilter {

  /**
    * Read Data from the dictionary file and build a bloom filter based on the builder passed in
    * @param path
    * @param bloomFilterBuilder
    * @return
    */
  def readData(path: String, bloomFilterBuilder: () => BloomFilter[String]) : BloomFilter[String] = {
    val emptyBloomFilter = bloomFilterBuilder()
    Source.fromResource(path).getLines().foldLeft(emptyBloomFilter)((bloomFilter, y) => {
      bloomFilter.add(y)
    })
  }

  /**
    * Utility method to take a string, remove all punctuation marks ( .,?;) and quotes
    * and then split it into actual words in the line
    * @param str
    * @return
    */
  def convertLineToListOfWords(str: String) : Array[String] = {
    //filter out any punctuation
    val noPunctuationString = str.replaceAll("""[\p{Punct}]""", "")
    //split the line into individual words
    noPunctuationString.split("\\s+")
  }


  /**
    * Simple method to count the actual words in the Moby Dick text file
    * @param path
    * @return
    */
  def numWordsInMobyDick(path: String) : Int = {
    Source.fromResource(path).getLines().foldLeft(0)((acc, y) => {
      acc + convertLineToListOfWords(y).length
    })
  }

  /**
    * Method to use a passed in bloom filter to count how many of the words
    * used in Moby Dick are actually in the bloom filter
    * @param path
    * @param bloomFilter
    * @return
    */
  def calculateNumHits(path: String, bloomFilter: BloomFilter[String]) : Int = {
    Source.fromResource(path).getLines().foldLeft(0)((acc, y) => {
      val numItemsInFilter = convertLineToListOfWords(y).map(x => {
        bloomFilter.contains(x)
      }).count( _ == true)

      acc + numItemsInFilter
    })
  }


  def main(args: Array[String]): Unit = {
    //A string bloom filter that uses all 16 bytes calculated in an MD5. DO NO USE except for testing. Could use
    val fullBloomFilter = readData("wordlist.txt", () => new StringBloomFilter(byteConverter = Md5StringToBytes.XLARGE))
    //large bloom filter uses 8 bytes per entry.  Full set would be 1.09 x 10^12
    val largeBloomFilter = readData("wordlist.txt", () => new StringBloomFilter(byteConverter = Md5StringToBytes.LARGE))
    //Medium bloom filter uses 4 bytes per entry.  Full set would be 4GB
    val mediumBloomFilter = readData("wordlist.txt", () => new StringBloomFilter(byteConverter = Md5StringToBytes.MEDIUM))
    //Small bloom filter uses 3 bytes per entry.  Full set would be 16M...appropriate for a local JVM.
    val smallBloomFilter = readData("wordlist.txt", () => new StringBloomFilter(byteConverter = Md5StringToBytes.SMALL))
    //XSmall bloom filter uses 2 bytes per entry.  Full set would be 64K...lots of false positives on large sets.
    val xSmallBloomFilter = readData("wordlist.txt", () => new StringBloomFilter(byteConverter = Md5StringToBytes.XSMALL))

    val numHitsWithFullBloomFilter = calculateNumHits("mobydick.txt", fullBloomFilter)
    val numHitsWithLargeBloomFilter = calculateNumHits("mobydick.txt", largeBloomFilter)
    val numHitsWithMediumBloomFilter = calculateNumHits("mobydick.txt", mediumBloomFilter)
    val numHitsWithSmallBloomFilter = calculateNumHits("mobydick.txt", smallBloomFilter)
    val numHitsWithXsmallBloomFilter = calculateNumHits("mobydick.txt", xSmallBloomFilter)


    println("Num words in Moby Dick file: " + numWordsInMobyDick("mobydick.txt"))
    println("Number of words in Moby Dick that actually appear in dictionary file: " + numHitsWithFullBloomFilter)
    println("Number of words in Moby Dick according to large(8 byte) filter: " + numHitsWithLargeBloomFilter
      + " false positives: " + (numHitsWithLargeBloomFilter - numHitsWithFullBloomFilter))
    println("Number of words in Moby Dick according to medium(4 byte) filter: " + numHitsWithMediumBloomFilter
      + " false positives: " + (numHitsWithMediumBloomFilter - numHitsWithFullBloomFilter))
    println("Number of words in Moby Dick according to medium(3 byte) filter: " + numHitsWithSmallBloomFilter
      + " false positives: " + (numHitsWithSmallBloomFilter - numHitsWithFullBloomFilter))
    println("Number of words in Moby Dick according to medium(2 byte) filter: " + numHitsWithXsmallBloomFilter
      + " false positives: " + (numHitsWithXsmallBloomFilter - numHitsWithFullBloomFilter))
  }

}
