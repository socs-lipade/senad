package net.floodlightcontroller.orchestrator;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;


import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class BasicConsumer  implements Runnable {
	
	  Properties props = new Properties();
	    KafkaConsumer<String, String> consumer;
	    String topicName;
	    Map dataEntity ;
	    String userName;
	    String groupId;
	    

	    
	    public BasicConsumer(String userName, String topicName){
	    	this.topicName = topicName;
	    	 this.userName = userName;
	    	 this.groupId =   UUID.randomUUID().toString();
	         //this.topics = topics;

	         System.setProperty("java.security.auth.login.config", "/home/yc/kafka/conf/"+this.userName+"/kafka_client_jaas.conf");

	         props.put("bootstrap.servers", "localhost:9092");
	         props.put("group.id", groupId);
	         props.put("enable.auto.commit", "true");
	         props.put("auto.commit.interval.ms", "1000");
	         props.put("session.timeout.ms", "30000");
	         props.put("key.deserializer",
	                 "org.apache.kafka.common.serialization.StringDeserializer");
	         props.put("value.deserializer",
	                 "org.apache.kafka.common.serialization.StringDeserializer");
	         props.put("security.protocol", "SASL_PLAINTEXT");
	         props.put("sasl.mechanism", "PLAIN");

	         consumer = new KafkaConsumer<String, String>(props);	
	    	
	    }
	    
	    public void receiveMsg() {
	    	
	    	ArrayList<String> topics = new ArrayList();
	    	topics.add(this.topicName); // = new ArrayList<String>(this.topicName);
	    	
	    	 for(String t : topics)
		        {	        
		        	consumer.subscribe(Arrays.asList(t));	        
		        	System.out.println("Subscribed to topic " + t);
		        }
	    	 
		        /*
		        ConsumerRecords<String, String> records = consumer.poll(100);
		        for (ConsumerRecord<String, String> record : records)
		        {
		        	System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
		        	
		        }*/
	    	
	    }
	   
	    
	  

	    public void printMsg(){
	    	
	    	ArrayList<String> topics = new ArrayList();
	    	topics.add(this.topicName); // = new ArrayList<String>(this.topicName);
	    	
	    	
	        for(String topicName : topics)
	        {	        
	        	consumer.subscribe(Arrays.asList(topicName));	        
	        	System.out.println("Subscribed to topic " + topicName);
	        }
	        
	        
	        ConsumerRecords<String, String> records = consumer.poll(100);
	        for (ConsumerRecord<String, String> record : records)
	        {
	        	System.out.printf("Receive time = %s, key = %s, value = %s\n", String.valueOf( System.currentTimeMillis()), record.key(), record.value());
	        	
	        	
	        }
            

	    }

	    public void keepDataEntity(HashMap entity){
	        this.dataEntity.putAll(entity);

	    }

	    public Map getDataEntity(){
	        return this.dataEntity;
	    }

	    public String getUserName(){

	        return this.userName;
	    }
	    
	    public void printAllMsg() {
	    	
	    }





		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
		public Long getReceiveTime() {
			return System.currentTimeMillis();
		}
	    
	        
	    

}
