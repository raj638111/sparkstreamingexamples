package a11_TCP_IP.a11_Basic

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import java.text.SimpleDateFormat
import java.util.Calendar

/*
spark-submit --master local[*] --class a11_TCP_IP.a11_Basic.a11_HelloTcp --driver-java-options -XX:MaxPermSize=300m target/scala-2.10/sparkstreamingexamples_2.10-1.0.jar 	
 */
object a11_HelloTcp {
  
  def main(args: Array[String]) {
    
    val conf = new SparkConf().setAppName("Basic_TCP_IP")
    val sc = new SparkContext(conf)

    // Set Batch Interval
    val stc = new StreamingContext(sc, Seconds(2))
    
    //  Create an Input stream that receives data from
    //  TCP/IP Socket
    val linesDstream = stc.socketTextStream("localhost", 9999)

    val minNsec = this.getMinNsec()
    
    val splittedDs = linesDstream.map{ x =>
      x.split("\\|\\|")
       .map(_.trim)
    }

    val filteredListDs = splittedDs.map{ x =>
      val retval = for { element <- x
          val keyNval = element.split("=")
          if keyNval.size >= 2
        } yield {
          val splitted = element.split("=")
          // Create Tuple of Key and Value
          splitted(0) -> splitted(1)
        }
      retval
    }
    
    val dnryList = filteredListDs.map{ x =>
      x.toMap
    }  
    
    val filteredDnryListDs = dnryList.filter{ x =>
      if (x.getOrElse("data", ()) != ())
        true
      else
        false
    }
    
    val keyvaluepairsDs = filteredDnryListDs.map { x =>
      val data = x.getOrElse("data", "")
      (data, x)
    }

    val wordsDs = keyvaluepairsDs.flatMap{ x =>
      //Underscore (_) is used as a Placeholder indicator
      val xsplitted = x._1.split(" ").map(_.trim)
      val wordNmeta = for { element <- xsplitted
        } yield {
          (element, (  1, 
                       List((x._2.getOrElse("secondscounter", ""))
                    )))
        }
      wordNmeta  
    }
    
    val reducedDs = wordsDs.reduceByKey{ (x, y) =>
      val sum = x._1 + y._1
      val timelist = x._2 ::: y._2
      (sum, timelist)
    }
    
    // Print first **10 elements of every batch of data
    //reducedDs.print()
    
    // Apply the given function to each RDD in this DStream
    reducedDs.foreachRDD{ rdd =>
       val minNSec = getMinNsec()
       //println(f"Batch at $minNSec%s")
       //println("-----------------------")
       rdd.collect().foreach(println)
       //println("")
    }
    
    //  Start the Computation & wait for it to terminate
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