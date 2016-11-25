package a15_kafka_avro

import org.apache.avro.generic.GenericRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.kafka.common.serialization.StringDeserializer
import kafka.serializer.StringDecoder
object a15_StringConsumer {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[*]").
                               setAppName("KafkaReader1")
    val ssc = new StreamingContext(conf, Seconds(2))
    val kafkaParams = Map[String, String]("bootstrap.servers" -> "localhost:9092")
    val topicSet = List("processinfo_string").toSet
    
    /*
		 val directKafkaStream = KafkaUtils.createDirectStream[
     [key class], [value class], [key decoder class], [value decoder class] ](
     streamingContext, [map of Kafka parameters], [set of topics to consume])
     */
    val kafkaStream = KafkaUtils.createDirectStream[
                            String, 
                            String, 
                            StringDecoder, 
                            StringDecoder](
                        ssc, kafkaParams, topicSet)

                        
    kafkaStream.print()
    ssc.start()
    ssc.awaitTermination()
  }    
}

/*
spark-submit --class a15_kafka_avro.a15_StringConsumer ./target/scala-2.10/sparkstreamingexamples-assembly-1.0.jar
*/