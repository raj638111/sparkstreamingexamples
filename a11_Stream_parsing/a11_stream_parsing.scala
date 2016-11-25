
val inputRdd = sc.parallelize(
                    List(  "data=first data || key1=r1v1 || key2=",
                       "data=second data || key1=r2v1 || key2=r2v2",
                       "key1=r3v1"
                      ))                      

val splitted = inputRdd.map{ x =>
  x.split("\\|\\|")
   .map(_.trim)
}

splitted.collect()

val filteredList = splitted.map{ x =>
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

filteredList.collect()

val dnryList = filteredList.map{ x =>
  x.toMap
}  
dnryList.collect()

val filteredDnryList = dnryList.filter{ x =>
  if (x.getOrElse("data", ()) != ())
    true
  else
    false
}

filteredDnryList.collect()

val keyvaluepairs = filteredDnryList.map { x =>
  val data = x.getOrElse("data", "")
  (data, x)
}

keyvaluepairs.collect()

val words = keyvaluepairs.flatMap{ x =>
  //Underscore (_) is used as a Placeholder indicator
  val xsplitted = x._1.split(" ").map(_.trim)
  val wordNmeta = for { element <- xsplitted
    } yield {
      (element, (  1, 
                   List((x._2.getOrElse("key1", ""))
                )))
    }
  wordNmeta  
}

words.collect()

val reduced = words.reduceByKey{ (x, y) =>
  val sum = x._1 + y._1
  val timelist = x._2 ::: y._2
  (sum, timelist)
}

reduced.collect()

