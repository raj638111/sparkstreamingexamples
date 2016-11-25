import org.apache.spark.streaming._
val stc = new StreamingContext(sc, Seconds(5))
val lines = stc.socketTextStream("localhost", 9999)
val words = lines.flatMap(_.split(" "))
val pairs = words.map(word => (word, 1))
val wordCounts = pairs.reduceByKey(_ + _)
wordCounts.print()
/*wordCounts.foreachRDD { rdd => 
  rdd.foreach(print)    
}*/
stc.start()
stc.awaitTermination()

------- netcat (nc)

mountain@mountain:~$ nc -lk 9999
This is a test
This is anothet test

------- Output in spark-shell

-------------------------------------------
Time: 1466196225000 ms
-------------------------------------------

-------------------------------------------
Time: 1466196230000 ms
-------------------------------------------
(a,1)
(is,2)
(test,2)
(anothet,1)
(This,2)
