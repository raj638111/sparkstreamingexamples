package a15_kafka_avro

import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import io.confluent.kafka.serializers.KafkaAvroDecoder
import org.apache.avro.generic.IndexedRecord
import org.apache.avro.generic.GenericRecord

object a11_AvroConsumer {
  
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[*]").
                               setAppName("KafkaReader1")
    val ssc = new StreamingContext(conf, Seconds(2))
    val kafkaParams = Map[String, String](
                     "bootstrap.servers" -> "localhost:9092",
                     "schema.registry.url" -> "http://localhost:8081")
    val topicSet = List("testtopic").toSet
    
    /*
		 val directKafkaStream = KafkaUtils.createDirectStream[
     [key class], [value class], [key decoder class], [value decoder class] ](
     streamingContext, [map of Kafka parameters], [set of topics to consume])
     */
    /*val kafkaStream = KafkaUtils.createDirectStream[
                            String, 
                            GenericRecord, 
                            kafka.serializer.Decoder[String], 
                            kafka.serializer.Decoder[GenericRecord]](
                        ssc, kafkaParams, topicSet)*/

    /*val kafkaStream = KafkaUtils.createDirectStream[
                            String, 
                            GenericRecord, 
                            KafkaAvroDeserializer, 
                            kafka.serializer.Decoder[GenericRecord]](
                        ssc, kafkaParams, topicSet)*/
    val kafkaStream = KafkaUtils.createDirectStream[
                            Object, 
                            Object, 
                            KafkaAvroDecoder, 
                            KafkaAvroDecoder](
                        ssc, kafkaParams, topicSet)                 
    val tuple3ed = kafkaStream.map(x => convert(x))
    tuple3ed.print()
    ssc.start()
    ssc.awaitTermination()
  }    
  
  def convert(message: (Object, Object)) = {
    val (k, v) = message
    val name = k.asInstanceOf[String]
    val value = v.asInstanceOf[GenericRecord]
    //println(s"name -> ${name}")
    (  value.get("javaprocessname").toString(),
       value.get("processid").toString(), 
       value.get("cpuconsumed").toString(),
       value.get("memoryconsumed").toString())
  }
  
}


/*
spark-submit --class a15_kafka_avro.a11_AvroConsumer ./target/scala-2.10/sparkstreamingexamples-assembly-1.0.jar
*/

/*
https://github.com/ScalaConsultants/spark-kafka-avro/blob/master/src/main/scala/io/scalac/spark/Model.scala
 */
