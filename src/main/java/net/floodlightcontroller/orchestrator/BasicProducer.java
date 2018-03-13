package net.floodlightcontroller.orchestrator;

//import util.properties packages
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

//import simple producer packages
import org.apache.kafka.clients.producer.Producer;

//import KafkaProducer packages
import org.apache.kafka.clients.producer.KafkaProducer;

//import ProducerRecord packages
import org.apache.kafka.clients.producer.ProducerRecord;



public class BasicProducer implements Runnable{
	
    Properties props = new Properties();
    Producer<String, String> producer;
    ArrayList<String> topics;
    String userName;
    String topicName;
   
    
    public BasicProducer(String userName, String topicName){
    	this.topicName = topicName;    	
    	  this.userName = userName;
          // create instance for properties to access producer configs

          System.setProperty("java.security.auth.login.config", "/home/yc/kafka/conf/"+ this.userName+"/kafka_client_jaas.conf");

          //Assign localhost id
          props.put("bootstrap.servers", "localhost:9092");

          //Set acknowledgements for producer requests.
          props.put("acks", "all");

          //If the request fails, the producer can automatically retry,
          props.put("retries", 0);

          //Specify buffer size in config
          props.put("batch.size", 16384);

          //Reduce the no of requests less than 0
          props.put("linger.ms", 1);

          //The buffer.memory controls the total amount of memory available to the producer for buffering.
          props.put("buffer.memory", 33554432);

          props.put("key.serializer",
                  "org.apache.kafka.common.serialization.StringSerializer");

          props.put("value.serializer",
                  "org.apache.kafka.common.serialization.StringSerializer");

          props.put("security.protocol", "SASL_PLAINTEXT");
          props.put("sasl.mechanism", "PLAIN");


          producer = new KafkaProducer<String, String>(props);

    	
    	
    }
    
    public void sendMsg(Map<String, String> map){

        for(Map.Entry<String, String> m: map.entrySet()){

            producer.send(new ProducerRecord<String, String>(this.topicName, m.getKey(),m.getValue()));

        }


        System.out.println("Message sent successfully");


    }

    public String getUserName(){

        return this.userName;
    }



    public void closeProducer(){
        producer.close();

    }
    
	public static <K, V> Map<K, V> createFIFOMap(final int maxEntries) {
    	
        return new LinkedHashMap<K, V>(maxEntries*10/7, 0.7f, true) {
            
        	@Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        	
        };
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
    
    

}
