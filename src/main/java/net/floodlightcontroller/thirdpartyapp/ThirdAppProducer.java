package net.floodlightcontroller.thirdpartyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;



public class ThirdAppProducer implements Runnable{
	
    Properties props = new Properties();
    Producer<String, String> producer;
    ArrayList<String> topics;
    String userName;
    String topicName;
    
    public static void main(String[] args) {
    	ThirdAppProducer myProducer = new ThirdAppProducer("user1","PacketInMsg");
    	
    	HashMap<String, String> map;
    	String firewallRule="";
    	while(true) {
    		map = new HashMap<String,String>();
    	
    		map.put(String.valueOf(System.currentTimeMillis()), firewallRule);
    		//myProducer.sendMsg(map);	
    		
    	}
    	
    	
    }
   
    
    public ThirdAppProducer(String userName, String topicName){
    	
    	this.topicName = topicName;    	
    	 this.userName = userName;
    	 run();
          // create instance for properties to access producer configs

    	
    }
    
    public void sendMsg(Map<String, String> map) {

        for(Map.Entry<String, String> m: map.entrySet()){

            producer.send(new ProducerRecord<String, String>(this.topicName, m.getKey(),m.getValue()));

        }						

       
    }
    
    public void setMsg(IOFSwitch sw, OFMessage msg, FloodlightContext cntx){ 	    
    	
    	Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        HashMap<String, String> map = new HashMap<String, String>();
    
        if (eth.getEtherType() == EthType.IPv4) {
    	    	IPv4 ipv4 = (IPv4) eth.getPayload();
    	    	IPv4Address dstIp = ipv4.getDestinationAddress();
    	
    	    	sw.getId().toString();
    	    	sw.getPorts();
    	
    	    	String producerStr = 
    			"{"+ "\"srcIpv4\":" + "\""+ ipv4.getSourceAddress()+"\"" +","			    	 				    	 
    	    	+"\"dstIpv4\":"+"\"" +ipv4.getDestinationAddress() +"\""+","
    					+ "\"srcMac\":" +"\""+ eth.getSourceMACAddress().toString() + "\""+","
    	    	+ "\"dstMac\":" +"\"" +eth.getDestinationMACAddress() + "\"" 
    					+ "}";
    	
    	    	map.put( String.valueOf(System.currentTimeMillis()),  producerStr);	
    	    	sendMsg(map); 	    
    	    	
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
		

        System.setProperty("java.security.auth.login.config", "./conf/kafka_client_jaas.conf");

        props.put("bootstrap.servers", "10.3.218.150:9092");

        props.put("acks", "all");

        props.put("retries", 0);

        props.put("batch.size", 16384);

        props.put("linger.ms", 1);

        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");


        producer = new KafkaProducer<String, String>(props);
 	
        
				
	
		
	}
    

	    
    

}
