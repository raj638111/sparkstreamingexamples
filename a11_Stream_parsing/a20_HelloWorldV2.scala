import org.apache.spark.streaming._
val stc = new StreamingContext(sc, Seconds(3))
val lines = stc.socketTextStream("localhost", 9999)
val words = lines.flatMap(_.split(" "))
val pairs = words.map(word => (word, 1))
//val wordCounts = pairs.reduceByKey(_ + _)
val wordCounts = pairs.reduceByKeyAndWindow(((x:Int, y:Int) => x + y), 
                                        Seconds(15), Seconds(3))
wordCounts.foreachRDD { rdd => 
  println("-------------------")
  rdd.foreach(println)    
}
stc.start()
stc.awaitTermination()

------- netcat (nc)

mountain@mountain:~$ nc -lk 9999
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15

------- Output in spark-shell
-------------------
(3,1)
(5,1)
(1,1)
(6,1)
(4,1)
(2,1)
-------------------
(2,1)
(4,1)
(6,1)
(3,1)
(5,1)
(9,1)
(1,1)
(8,1)
(7,1)
-------------------
(5,1)
(4,1)
(6,1)
(2,1)
(13,1)
(8,1)
(11,1)
(3,1)
(9,1)
(12,1)
(1,1)
(7,1)
(10,1)
-------------------
(2,1)
(13,1)
(6,1)
(5,1)
(14,1)
(3,1)
(9,1)
(12,1)
(1,1)
(8,1)
(11,1)
(4,1)
(15,1)
(7,1)
(10,1)
-------------------
(5,1)
(2,1)
(13,1)
(6,1)
(14,1)
(3,1)
(4,1)
(15,1)
(9,1)
(12,1)
(1,1)
(8,1)
(11,1)
(7,1)
(10,1)
-------------------
(13,1)
(15,1)
(8,1)
(11,1)
(9,1)
(12,1)
(14,1)
(7,1)
(10,1)
-------------------
(11,1)
(13,1)
(15,1)
(12,1)
(14,1)
(10,1)
-------------------
(14,1)
(15,1)
-------------------

