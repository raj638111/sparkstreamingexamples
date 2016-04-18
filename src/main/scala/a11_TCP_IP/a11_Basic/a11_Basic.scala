package a11_TCP_IP.a11_Basic

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext

object a11_Basic {
  def main(args: Array[String]) {
    
    val conf = new SparkConf().setAppName("Basic_TCP_IP")
    val sc = new SparkContext(conf)
    
    //  Streaming context with Batch interval of 
    //  1 seconds
    val stc = new StreamingContext(sc, Seconds(1))
    
    //  Create an Input stream that receives data from
    //  TCP/IP Socket
    val linesDstream = stc.socketTextStream("localhost", 9999)
    
    //  Split line into words
    val wordsDstream = linesDstream.flatMap(_.split(" "))
    
    //  Add count as '1' for each word
    val pairsDstream = wordsDstream.map(word => (word, 1))
    
    //  Reduce same words and calculate their count
    val wordCountDstream = pairsDstream.reduceByKey(_ + _)
    
    //  Print the counts in DStream
    wordCountDstream.print()
    
    //  Start the Computation & wait for it to terminate
    stc.start()
    stc.awaitTermination()
  }
}