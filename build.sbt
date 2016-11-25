
lazy val root = (project in file(".")). 
  settings(
    name := "sparkstreamingexamples",
    version := "1.0",  
    // Scal Version
    scalaVersion := "2.10.6",
    // Download source code(will come in handy to refer the code in eclipse)
    EclipseKeys.withSource := true,
    libraryDependencies ++= Seq(
    	//'provided' - We do not want to ship these JARs to
    	//             worker nodes, as they will be already available
    	//             in the worked nodes
    	"org.apache.spark" % "spark-core_2.10" % "1.6.1" % "provided",
        "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.1",
        "org.apache.spark" % "spark-streaming_2.10" % "1.6.1",
        "com.yammer.metrics" % "metrics-annotation" % "2.2.0",
        "org.apache.hbase" % "hbase-common" % "1.1.2",
        "org.apache.hadoop" % "hadoop-common" % "2.7.1",
        "org.apache.hbase" % "hbase-server" % "1.1.2",
        "org.apache.hbase" % "hbase-client" % "1.1.2",
        "io.confluent" % "kafka-avro-serializer" % "3.0.0",
        "org.scalactic" %% "scalactic" % "3.0.0",
        "org.scalatest" %% "scalatest" % "3.0.0"    	
    )
 )

assemblyMergeStrategy in assembly := {
    case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
    case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
    case PathList("javax", "el", xs @ _*) => MergeStrategy.last
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case PathList("com", "google", xs @ _*) => MergeStrategy.last
    case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
    case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
    case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
    case "about.html" => MergeStrategy.rename
    case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
    case "META-INF/mailcap" => MergeStrategy.last
    case "META-INF/mimetypes.default" => MergeStrategy.last
    case "plugin.properties" => MergeStrategy.last
    case "log4j.properties" => MergeStrategy.last
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
} 
