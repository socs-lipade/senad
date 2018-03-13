package net.floodlightcontroller.staticentry;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class FlowEntryProducer implements Runnable{
	
	  Properties props = new Properties();
	    Producer<String, String> producer;
	    ArrayList<String> topics;
	    String userName;
	    String topicName;
	    
	    public FlowEntryProducer(){	    	
	    	
	    	this.topicName = "FlowEntry";    	
	    	this.userName = "user1";
	    	
	         // create instance for properties to access producer configs

	          System.setProperty("java.security.auth.login.config", "./conf/kafka_client_jaas.conf");

	          //Assign localhost id
	          props.put("bootstrap.servers", "10.3.218.150:9092");

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

	    }

	    public String getUserName(){

	        return this.userName;
	    }



	    public void closeProducer(){
	        producer.close();

	    }

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	    
	    

}
