package net.floodlightcontroller.thirdpartyapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class AttackDetector {

	Properties props = new Properties();
	KafkaConsumer<String, String> consumer;
	String topicName;
	Map dataEntity ;
	String userName;
	String groupId;
	HashMap<String, String> dataMap = new HashMap<String,String> ();
	
	FileOutputStream fop = null;
	File file;
	
	    
	    int counter = 0;
	    
	    public static void main(String[] args) throws IOException {
	    	
	    	AttackDetector consumer = new AttackDetector("user1","PacketInMsg");
    	
	    	consumer.receiveMsg();
	    		    	
	  
	    }
	    

	    
	    public AttackDetector(String userName, String topicName){
	    	
	    	this.topicName = topicName;
	    	 this.userName = userName;
	    	 this.groupId =   UUID.randomUUID().toString();
	         //this.topics = topics;

	         System.setProperty("java.security.auth.login.config", "./conf/kafka_client_jaas.conf");

	         props.put("bootstrap.servers", "10.3.218.119:9092");
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
	         System.out.println("Working Directory = " +  System.getProperty("user.dir"));

	    	
	    }
	    
	    public void receiveMsg() {
	    	
	    	ArrayList<String> topics = new ArrayList();
	    	topics.add(this.topicName); // = new ArrayList<String>(this.topicName);
	    	
	    	for(String t : topics)
		    {	        
		        	consumer.subscribe(Arrays.asList(t));	        
		        	System.out.println("Subscribed to topic " + t);
		    }
	    	
	    	
	    	try {
	    		  while (true) {
	    		    ConsumerRecords<String, String> records = consumer.poll(1000);
	    		    for (ConsumerRecord<String, String> record : records)
	    		      System.out.println(record.offset() + ": " + record.value());
	    		    
	    		    
	    		    
	    		    
	    		    
	    		  }
	    		} finally {
	    		  consumer.close();
	    		}
	    	      
	           	 	    	
	    }
	    
	    
	    
	    
	    public Map getData() {
	    	return dataMap;
	    	
	    }
	    
	    public void setData(HashMap map) {
	    	dataMap = map;
	    	
	    }
	    
	    
	    public String getFlowEntry() {
	    	return null;
	    }
	    
	    public String getDeviceInfo() {
	    	
	    	return null;
	    }
	   
	    
	  

	   

	   

	    public String getUserName(){

	        return this.userName;
	    }
	    
	 	public Long getReceiveTime() {
			return System.currentTimeMillis();
		}

		

}
