package net.floodlightcontroller.thirdpartyapp;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;


import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.google.gson.*;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.Ethernet;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ThirdAppConsumer{
	
	
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
	    
	    public static void main(String[] args) throws IOException, InterruptedException {
	    	
	    	ThirdAppConsumer deviceInfoConsumer = new ThirdAppConsumer("user1","DeviceInfo");
	    	
	    	ThirdAppConsumer flowEntryConsumer = new ThirdAppConsumer("user1","FlowEntry");

	    	
	    	deviceInfoConsumer.receiveMsg();
	    	flowEntryConsumer.receiveMsg();
	    	HashMap<String, HashMap<String, String>> deviceData = new HashMap<String, HashMap<String, String>>();
	    	HashMap<String, HashMap<String, String>> entryData = new HashMap<String, HashMap<String, String>>();
	        

	    	while(true) {	
	    		
	    		deviceData = deviceInfoConsumer.getRecords(deviceData);
	    		entryData = flowEntryConsumer.getRecords(entryData);
	    		
	    		HashMap<String, HashMap<String,String>> conflictByMacs = new HashMap<String, HashMap<String,String>>();
	    		
	    		for(Map.Entry<String, HashMap<String, String>> deviceEntry : deviceData.entrySet() ) 
	    		{
	    			
		    		for(Map.Entry<String, HashMap<String, String>> flowEntry : entryData.entrySet() ) 		    		
		    		{
		    			String newSrcIP="";
		    			String newDstIp="";
		    			String srcMac="";
		    			String dstMac="";
		    			
		    			deviceEntry.getKey(); //ip
		    			deviceEntry.getValue().get("mac");
		    			
		    			//System.out.println(deviceEntry.getKey().replace("\"","") +"//"+flowEntry.getValue().get("ipv4_src").replace("\"", "") );
		    			
		    			
		    			if(deviceEntry.getKey().replace("\"","").equals( flowEntry.getValue().get("ipv4_src").replace("\"", "")))
		    			{
		    				//System.out.println( "the mac for the IP " + deviceEntry.getKey() + " is " + deviceEntry.getValue().get("mac") );
		    				srcMac = deviceEntry.getValue().get("mac").replaceAll("\"", "") ;
		    				//newSrcIP = deviceEntry.getKey().replace("\"","");
		    				
		    			}
		    			
		    			if(deviceEntry.getKey().replace("\"","").equals( flowEntry.getValue().get("ipv4_dst").replace("\"", "")))
		    			{
		    				//System.out.println( "the mac for the IP " + deviceEntry.getKey() + " is " + deviceEntry.getValue().get("mac") );
		    				dstMac = deviceEntry.getValue().get("mac").replaceAll("\"", "") ;	
		    				//newDstIp = deviceEntry.getKey().replace("\"","");
		    				
		    			}
		    			
		    			
		    			if(srcMac != "" || dstMac != "") 
		    			{
		    				if(conflictByMacs.get(srcMac + ";" + dstMac) == null) 
		    				{
		    					HashMap<String, String> data = new HashMap<String, String>();
		    					
		    					data.put("name",  flowEntry.getKey().replace("\"", ""));
		    					data.put("priority",  flowEntry.getValue().get("priority").replace("\"", ""));
		    					data.put("actions",  flowEntry.getValue().get("actions").replace("\"", ""));
		    					data.put("switch",  flowEntry.getValue().get("switch").replace("\"", ""));
		    					data.put("ipv4_dst",  flowEntry.getValue().get("ipv4_dst").replace("\"", ""));
		    					data.put("ipv4_src",  flowEntry.getValue().get("ipv4_src").replace("\"", ""));
		    					
		    					conflictByMacs.put(srcMac + ";" + dstMac, data);
		    					
		    				}
		    				else 
		    				{	
		    					
		    						
		    					System.out.println( "[Info] The rules " + flowEntry.getKey().replace("\"", "") + " priority is " + flowEntry.getValue().get("priority").replace("\"", ""));
		    					System.out.println( "[Info] The rules " + conflictByMacs.get(srcMac + ";" + dstMac).get("name") + " priority is " + conflictByMacs.get(srcMac + ";" + dstMac).get("priority"));
		    					String newRule ="";

		    					
		    					if( Integer.valueOf( conflictByMacs.get(srcMac + ";" + dstMac).get("priority").replace("\"", "")).equals(Integer.valueOf (flowEntry.getValue().get("priority").replace("\"", "") )) ) //check the priority  
		    					{
		    						
		    						
		    						System.out.println( "[Warning] You have conflict rules " 
		    						+ conflictByMacs.get(srcMac + ";" + dstMac).get("name") + " and " + flowEntry.getKey() 
		    						+ "with the same priority " + flowEntry.getValue().get("priority"));
		    					
		    						//System.out.println( "action new " + flowEntry.getKey().replace("\"", "") );
		    						//if priority is the same : warning msg
		    						//or replace the lower one
		    						
		    					}else if( Integer.valueOf( conflictByMacs.get(srcMac + ";" + dstMac).get("priority").replace("\"", "")) >  Integer.valueOf (flowEntry.getValue().get("priority").replace("\"", "") ) ) {
		    						
		    						newRule  = "{"+ "\"name\":" +"\"" +  conflictByMacs.get(srcMac + ";" + dstMac).get("name").replace("\"", "") +"\"" +"," 
				    						+ "\"switch\":" + "\"" + conflictByMacs.get(srcMac + ";" + dstMac).get("switch").replace("\"", "")+"\"" +"," 
				    						+ "\"cookie\":\"0\"" + ","
				    						+ "\"priority\":" +"\""+ conflictByMacs.get(srcMac + ";" + dstMac).get("priority").replace("\"", "") +"\"" +","
				    						+ "\"ipv4_src\":" + "\""+ flowEntry.getValue().get("ipv4_src").replace("\"", "") +"\""+","
				    						+ "\"ipv4_dst\":" + "\""+ flowEntry.getValue().get("ipv4_dst").replace("\"", "") +"\""+","
				    						+ "\"eth_type\":" + "\"0x0800\"" +","
				    						+"\"active\":\"true\""+","
				    						+"\"actions\":" +"\"" + conflictByMacs.get(srcMac + ";" + dstMac).get("actions").replace("\"", "").replace("0x3D", "=") + "\"" +"}";
		    						
		    						
		    						System.out.println( "[AttackDetected] The rule " +  conflictByMacs.get(srcMac + ";" + dstMac).get("name").replace("\"", "") + " should be replaced as \n" +newRule );
		    								
		    					}else {
		    						//System.out.println( "[Info] option 3 The rules " + flowEntry.getKey().replace("\"", "") + " priority is " + flowEntry.getValue().get("priority").replace("\"", ""));
		    						//System.out.println( "[Info] option 3 The rules " + conflictByMacs.get(srcMac + ";" + dstMac).get("name").replace("\"", "") + " priority is " + conflictByMacs.get(srcMac + ";" + dstMac).get("priority").replace("\"", ""));

		    						newRule  = "{"+"\"name\":" +"\"" +  flowEntry.getKey() +"\"" +"," 
		    						+ "\"switch\":" + "\"" + flowEntry.getValue().get("switch").replace("\"", "") +"\"" +"," 
		    						+ "\"cookie\":\"0\"" + ","
		    						+ "\"priority\":" +"\""+ flowEntry.getValue().get("priority").replace("\"", "") +"\"" +","
		    						+ "\"ipv4_src\":" +"\""+ conflictByMacs.get(srcMac + ";" + dstMac).get("ipv4_src").replace("\"", "") +"\"" +","
		    						+ "\"ipv4_dst\":" + "\"" + conflictByMacs.get(srcMac + ";" + dstMac).get("ipv4_dst").replace("\"", "") +"\"" +","
		    						+ "\"eth_type\":" + "\"0x0800\"" +","
		    						+"\"active\":\"true\""+","
		    						+"\"actions\":" +"\"" +flowEntry.getValue().get("actions").replace("\"", "").replace("0x3D", "=") +"\""+ "}";

		    					
		    						
		    					}
		    					
		    					Gson gson = new GsonBuilder().setPrettyPrinting().create();
		    					JsonParser jp = new JsonParser();
		    					JsonElement je = jp.parse(newRule);
		    					String prettyJsonString = gson.toJson(je);
		    					
		    						
		    					System.out.println( "[AttackDetected] The rule " + flowEntry.getKey() + " should be replaced as ");
		    					System.out.println( prettyJsonString);

		    				}
		    			}
		    			
		    			
		    			
		    		
		    		
		    			
		    			//String srcMac = deviceEntry.getValue().get("srcMac");
		    			
		    			
		    		}
	    			
	    		}
	    		
	    		
	    	  // System.out.println(	deviceInfoConsumer.getRecords(deviceData));
	    	   //System.out.println( flowEntryConsumer.getRecords(entryData));
	    		
	    		Thread.sleep(3000);	    		
	    		
	    		//consumer.printMsg();
	    		
	    		//consumer2.printMsg();	        
			        
	    	}	    	
	    	
	    }
	    
	    private String getIpbyMac(String mac) {
	    	
	    	return null;
	    }
	    

	    
	    public ThirdAppConsumer(String userName, String topicName){
	    	
	    	this.topicName = topicName;
	    	 this.userName = userName;
	    	 this.groupId =   UUID.randomUUID().toString();
	         //this.topics = topics;

	         System.setProperty("java.security.auth.login.config", "./conf/kafka_client_jaas.conf");

	         props.put("bootstrap.servers", "10.3.218.150:9092");
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
	    	
	    	HashMap<String, String> map = new 	HashMap<String, String>();
	        ConsumerRecords<String, String> records = consumer.poll(100);
	        for (ConsumerRecord<String, String> record : records)
	        {
	        	
	        	
	        	System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
	        	
	        	
	        	long latency =  System.currentTimeMillis() - Long.valueOf(record.key());
	        	System.out.printf("messge bus latency = " + latency +"\n");
	        	
	        	map.put( record.key(), "disable");
	        	setData(map);
	        	
	        	
	        	
	        }
	    	
	        
	        
	    	 	    	
	    }
	    
	    public Map getData() {
	    	return dataMap;
	    	
	    }
	    
	    public void setData(HashMap map) {
	    	dataMap = map;
	    	
	    }
	    
	    public HashMap<String, HashMap<String, String>> getRecords(HashMap<String, HashMap<String, String>> data){
	    	 
	    	//HashMap<String, HashMap<String, String>> data = new HashMap<String, HashMap<String, String>> ();
	    	//HashMap<String, HashMap<String, String>> dataFlow = new HashMap<String, HashMap<String, String>> ();

	    	 
	    	ArrayList<String> topics = new ArrayList();
	    	topics.add(this.topicName); // = new ArrayList<String>(this.topicName);
	    	
	        ConsumerRecords<String, String> records = consumer.poll(100);
	        
	        JsonParser parser = new JsonParser(); 
	        for (ConsumerRecord<String, String> record : records)
	        {
	        	
	        	//System.out.println("DeviceInfo key= "+ record.key() +"; value = " +record.value());
	        	JsonObject json = (JsonObject) parser.parse(record.value() );
	        	HashMap<String,String> valueMap = new HashMap<String,String>();
	        	
	        	
	        	if(this.topicName.equals("DeviceInfo")) 
	        	{
	        		
	        		valueMap.put("mac", json.get("mac").toString());
	        		valueMap.put("switch", json.get("switch").toString());

	        		
	        		//valueMap.put sw + port 
	        		
	        		//valueMap.put("dstIpv4", json.get("dstIpv4").toString());
	        		//valueMap.put("srcMac", json.get("srcMac").toString());
	        		//valueMap.put("dstMac", json.get("dstMac").toString());
	        		
	        		
	        	//	System.out.println("DeviceInfo="+json);
	        		
	        	}
	        	
	        	if(this.topicName.equals("FlowEntry"))  
	        	{
	        		
	        		valueMap.put("switch", json.get("switch").toString());
	        		valueMap.put("in_port", json.get("in_port").toString());
	        		valueMap.put("ipv4_src", json.get("ipv4_src").toString());
	        		valueMap.put("ipv4_dst", json.get("ipv4_dst").toString());
	        		valueMap.put("eth_src", json.get("eth_src").toString());
	        		valueMap.put("eth_dst", json.get("eth_dst").toString());
	        		valueMap.put("eth_type", json.get("eth_type").toString());
	        		valueMap.put("priority", json.get("priority").toString());
	        		valueMap.put("actions", json.get("actions").toString());
	        		
	        		//System.out.println("FlowEntry="+json);
	        	}
	        	
	        	data.put(record.key(), valueMap);
	        	
	        	
	        	
	        //	System.out.printf("kafka: " + record.key() +"\n" );
	        //	System.out.printf("kafka: " + record.value() +"\n" );
	        	
	        	
	        	
	        	//System.out.println(json.get("srcIpv4").toString());
	        	
	        	
	                	
	     
	        }
	        
	        return data;
	        
	        
	    }
	   
	    
	  

	    public void printMsg() throws IOException{
	    	
	    	ArrayList<String> topics = new ArrayList();
	    	topics.add(this.topicName); // = new ArrayList<String>(this.topicName);
	    	
	        ConsumerRecords<String, String> records = consumer.poll(100);
	        
	        for (ConsumerRecord<String, String> record : records)
	        {
	        	   	
	        	
	        		
	        	
	        	
	        	
	        	//System.out.printf("kafka: " + record.key() +"\n" );
	        	//System.out.printf("kafka: " + record.value() +"\n" );
	        	
	        	
	        	JsonParser parser = new JsonParser(); 
	        	JsonObject json = (JsonObject) parser.parse(record.value() );
	        	//System.out.println(json.get("srcIpv4").toString());
	        	System.out.println(json);
	        	
	        	/*        

	        	if( json.get("srcIpv4").toString().replaceAll("\"", "").equals("10.0.0.1")) {
	        		System.out.println("10.0.0.1 mac is "+json.get("srcMac"));
	        		
	        		
	        	}
	        	*/	        	
	     
	        }

	    }
	    
	    public String getTopicName() {
	    	return this.topicName;
	    }
	    
	   

	   

	    public String getUserName(){

	        return this.userName;
	    }
	    
	    public void printAllMsg() {
	    	
	    }

	
		
		public Long getReceiveTime() {
			return System.currentTimeMillis();
		}

		
	        
	    

}
