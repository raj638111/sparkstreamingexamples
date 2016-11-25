package a11_TCP_IP.a15_Window

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import java.text.SimpleDateFormat
import java.util.Calendar

/*
spark-submit --master local[*] --class a11_TCP_IP.a15_Window.a11_WindowExample --driver-java-options -XX:MaxPermSize=300m target/scala-2.10/sparkstreamingexamples_2.10-1.0.jar
*/
object a11_WindowExample {
  
  def main(args: Array[String]) {
    
    val conf = new SparkConf().setAppName("Window_Example")
    val sc = new SparkContext(conf)

    // Set Batch Interval
    val stc = new StreamingContext(sc, Seconds(4))
    
    // Create an Input stream that receives data from
    // TCP/IP Socket
    val linesDstream = stc.socketTextStream("localhost", 9999)

    val splittedDs = linesDstream.map{ x =>
      x.split("\\|\\|")
       .map(_.trim)
    }

    val filteredListDs = splittedDs.map{ x =>
      val retval = for { element <- x
          val keyNval = element.split("=")
          // Ensure key & value are both present
          if keyNval.size >= 2
        } yield {
          val splitted = element.split("=")
          // Create Tuple of Key and Value
          splitted(0) -> splitted(1)
        }
      retval
    }
    
    // Convert the Splitted tokens into a Map 
    val dnryListDs = filteredListDs.map{ x => x.toMap }
    
    val keyvaluepairsDs = dnryListDs.map { x =>
      val data = x.getOrElse("data", "")
      (data, x)
    }

    val wordsDs = keyvaluepairsDs.flatMap{ x =>
      val xsplitted = x._1.split(" ").map(_.trim)
      val wordNmeta = for { element <- xsplitted
        } yield {
          (element, (  1, 
                       List((x._2.getOrElse("secondscounter", ""))
                    )))
        }
      wordNmeta  
    }
    
    def reduceFn(  x:(Int, List[String]), y:(Int, List[String])  ) 
                                  : (Int, List[String]) = {
      val sum = x._1 + y._1
      val timelist = x._2 ::: y._2
      (sum, timelist)
    }
    
    // Sliding window with Window duration of 6 seconds and
    // sliding duration of 2 seconds
    // reduceFn _ : Is a Partially Applied Function
    //            : Denotes, all the arguments are passed @ runtime
    val reducedDs = wordsDs.reduceByKeyAndWindow(reduceFn _, 
                                        Seconds(16), Seconds(8))
    
    // Apply the given function to each RDD in this DStream
    reducedDs.foreachRDD{ rdd =>
       val minNSec = getMinNsec()
       //println(f"Window at $minNSec%s")
       //println("-----------------------")
       rdd.collect().foreach(println)
       //println("")
    }
    
    // Start the Computation & wait for it to terminate
    stc.start()
    stc.awaitTermination()
  }
  
  def getMinNsec() : String = {
    val now = Calendar.getInstance().getTime()
    val myFormat = new SimpleDateFormat("mm.ss.SS")
    val minNmillisec = myFormat.format(now)
    val splitted = minNmillisec.split("\\.")
    val min = splitted(0)
    val sec = splitted(1)
    val millisec = splitted(2)
    val millisecTruncated = millisec(0)
    f"[$min%2s][$sec%2s.$millisecTruncated%s]"
  }

}