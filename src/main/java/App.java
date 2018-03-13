import java.util.ArrayList;
import java.util.Properties;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.google.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class App {
	
	private static final boolean MapEntry = false;

	public SimpleConsumer getConsumer(String userName, String groupId, String topicName) throws InterruptedException{
		
		return new SimpleConsumer(userName, groupId, topicName);
			
		
	}
	
	public Device getDevice() {
		return new Device();
	}
	
	public FlowEntry getFlowEntry() {
		return new FlowEntry();
	}
	

	    class SimpleConsumer {
	    	 Properties props = new Properties();
	            KafkaConsumer<String, String> consumer;
	            ArrayList<String> topics = new ArrayList();
	            HashMap dataEntity;
	            String userName;
	            String groupId; 
	            String topic;


	            SimpleConsumer(String userName, String groupId, String topic){
	                 this.userName = userName;
	                 this.topics.add(topic);
	                 this.groupId  =groupId;
	                 this.topic = topic;

	                 System.setProperty("java.security.auth.login.config", "./conf/kafka_client_jaas.conf");

	                 props.put("bootstrap.servers", "10.3.218.150:9092");
	                 props.put("group.id", this.groupId);
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
	                 
	                 for(String topicName : topics)
		                {
		                    consumer.subscribe(Arrays.asList(topicName));
		                    System.out.println("Subscribed to topic " + topicName);
		                }

	            }
	            
	            public HashMap<String, String> getDataInMap() {
	            	HashMap data = new HashMap<String, String>();
	            	
	            	 ConsumerRecords<String, String> records = consumer.poll(100);
	                    for (ConsumerRecord<String, String> record : records) 
	                    {
	                    	// print the offset,key and value for the consumer records.
	                    	data.put( record.key(), record.value());
	                        //System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
	                    }
	                    
	                    return data;
	                        
	            	
	            }

	         

	            public void printMsg(){

		               // while (true) { }
	                    ConsumerRecords<String, String> records = consumer.poll(100);
	                    for (ConsumerRecord<String, String> record : records)
	                        // print the offset,key and value for the consumer records.
	                        System.out.printf("offset = %d, key = %s, value = %s\n",
	                                record.offset(), record.key(), record.value());

	            }

	            public void keepDataEntity(HashMap entity){
	                this.dataEntity.putAll(entity);// = new HashMap(entity);

	            }

	            public HashMap getDataEntity(){
	                return this.dataEntity;
	            }

	            public String getUserName(){

	                return this.userName;
	            }


	    }
	    
	    class Device{
	    	public Device() {
	    		
	    	}
	    	String mac;
	    	List<String> ipList;//change to list
	    	String attachedSwitch;
	    	String attachedPort;
	    }
	    
	    class FlowEntry{
	    	
	    	String mac;
	    	String ip;
	    	String attachedSwitch;
	    	String attachedPort;
	    	
	    	String action;
	    	
	    }
	    


	    public static void main(String[] args) throws InterruptedException{
	        // Prints "Hello, World" to the terminal window.
	    	System.out.println("Working Directory = " + System.getProperty("user.dir"));
	        
	    	Map<String,Device > deviceMap = new  HashMap<String,Device >();
	    	List<FlowEntry> entryList = new ArrayList<FlowEntry>();
	    	
	    	App myApp = new App();
	        
	        
	        SimpleConsumer deviceInfo=  myApp.getConsumer("app1", "gro1", "DeviceInfo");
	        
	       // SimpleConsumer entryInfo=  myApp.getConsumer("app1", "gro2", "read-static-entry-all");
	        
	       
            
	        while (true) {
	        	//deviceInfo.printMsg();
	        	//entryInfo.printMsg();
            	
            	for ( Map.Entry<String, String> m : deviceInfo.getDataInMap().entrySet()) {
            	    String k = m.getKey();
            	    String v = m.getValue();
            	    
            	    System.out.println("key" + k);
            	    System.out.println("value" + v);
            	    JsonObject deviceJsonObj = new Gson().fromJson(v.toString(), JsonObject.class);
            	    System.out.println("devices" + deviceJsonObj.get("devices").getAsJsonArray());
            	    
            	    for(JsonElement j : deviceJsonObj.get("devices").getAsJsonArray()) {
            	    	
            	    	j.getAsJsonObject().get("mac");
            	    	j.getAsJsonObject().get("ipv4");
            	    	j.getAsJsonObject().get("attachmentPoint");
            	    	System.out.println(j.getAsJsonObject().get("mac"));
            	    	System.out.println(j.getAsJsonObject().get("ipv4"));
            	    	System.out.println(j.getAsJsonObject().get("attachmentPoint"));
            	    
            	    	deviceMap.put(j.getAsJsonObject().get("mac").toString(), myApp.getDevice());
            	    }
            	    
            	    
            	    // do something with key and/or tab
            	}
            	
            	/*
            	
            	for ( Map.Entry<String, String> m : entryInfo.getDataInMap().entrySet()) {
            		 
            		String k = m.getKey();
              	    String v = m.getValue();              	  
              	    //System.out.println("value" + v);          	    
              	    JsonObject deviceJsonObj = new Gson().fromJson(v.toString(), JsonObject.class);
            		
            		
            	}*/

            	
            	
            	
            	
            	//JsonObject jobj = new Gson().fromJson(myJSONString, JsonObject.class);

            	
            	Thread.sleep(1000);
            }

	        
	        
	        //ArrayList<String> consumeTopics = new ArrayList<String>();
	        //consumeTopics.add("read-device-all");     
	        
	        

	        
	    }
	    
	    public List processDevice(Device d) {
	    	return null;
	    }
	    
	    public FlowEntry completeFlowEntry(FlowEntry f, HashMap<String, Device> deviceMap) {
	    	
        	for ( Map.Entry<String, Device> d : deviceMap.entrySet()) {
        		
        		
        		for(String hostIp : d.getValue().ipList) {
        			
        			if(hostIp.equals(f.ip)) {	    			
        				
        				f.mac=d.getValue().mac;	    			
        				f.attachedPort=d.getValue().attachedPort;	    			
        				f.attachedSwitch=d.getValue().attachedSwitch;	  				    		
        				
        			}
        		}
        		
	    		
	    	}
	    	
	    	return f;
	    	
	    }
	    
	    public void checkFlowConflict(List<FlowEntry> fel) {
	    	
	    	String conflictEntry = null;
	    	
	    	for(int i=0;i<fel.size();i++) {
	    		for(int j=i;j<fel.size();j++) {
	    			
	    			if(fel.get(i).mac.equals(fel.get(j).mac)) {
	    				System.out.println("***risk..., mac match:"+ fel.get(i).mac);
	    				
	    				if(!fel.get(i).action.equals(fel.get(j).action) ) {
	    					//report error
	    					//update flow entry
	    					
	    					
	    				}
	    				
	    				
	    				
	    			}
	    			
	    			if(fel.get(i).attachedSwitch.equals(fel.get(j).attachedSwitch) 
	    					&& fel.get(i).attachedPort.equals(fel.get(j).attachedPort)  ) {
	    				System.out.println("***risk..., switch and port match:"+ fel.get(i).attachedSwitch + fel.get(j).attachedSwitch);
	    				
	    				if(!fel.get(i).action.equals(fel.get(j).action) ) {
	    					//send a warning 
	    					
	    					
	    				}
	    				
	    				
	    				
	    				
	    			}
	    			
	    			
	    			
	    			
	    		}
	    		
	    	}
	    	
	    }
	    
	    
	    



}
