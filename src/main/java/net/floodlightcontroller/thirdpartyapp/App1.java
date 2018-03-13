package net.floodlightcontroller.thirdpartyapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;
import org.projectfloodlight.openflow.types.VlanVid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.orchestrator.BasicConsumer;
import net.floodlightcontroller.orchestrator.BasicProducer;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.staticentry.StaticEntryPusher;


public class App1 implements IOFMessageListener, IFloodlightModule{
	
	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	
	ThirdAppProducer packetInProducer = new ThirdAppProducer("user1","DeviceInfo");
    //PacketInProducer packetInProducer;//  = new PacketInProducer();

	
	public class PacketInProducer extends Thread{
		
		IOFSwitch sw; 
		OFMessage msg; 
		FloodlightContext cntx;
		
		Properties props = new Properties();
		Producer<String, String> producer;
		ArrayList<String> topics;
		String userName;
		String topicName;
		    
		
		public PacketInProducer(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) 
		{
			this.sw = sw;
			this.msg = msg;
			this.cntx = cntx;	
			this.topicName ="DeviceInfo";
			
		}
	
		public void run() {
			
			init();
					    	
			sendMsg();
		}
		
		private void sendMsg() {
	    	
	    	
	    	Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
	    	
	    	
	    			    	
	    	HashMap<String, String> map = new HashMap<String, String>();
	    	
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
			    	 
			    	 for(Map.Entry<String, String> m: map.entrySet())
			    	 {

			             producer.send(new ProducerRecord<String, String>(this.topicName, m.getKey(),m.getValue()));

			         }	
			    	 
			    	 
	    	 
	  
			    	 
			
			
		}
		
		private void init() {

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
	


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return   App1.class.getSimpleName();

	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		  floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		    macAddresses = new ConcurrentSkipListSet<Long>();
		    logger = LoggerFactory.getLogger(App1.class);
		    
			
			 
	
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
	    floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);     		
		
	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	    Collection<Class<? extends IFloodlightService>> l =
	        new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IFloodlightProviderService.class);
	    return l;
	}
	

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx)  {
						
		 switch (msg.getType()) {
		
		 
		    case PACKET_IN:
		    	
		    			    	
		    	Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		    	
		    	
		    			    	
		    	HashMap<String, String> map = new HashMap<String, String>();
		    	//HashMap<String, String> idData = new HashMap<String, String>(); //mac -> macAddr; sw & port -> sw + port
		    	
		    	 if (eth.getEtherType() == EthType.IPv4) {
			            IPv4 ipv4 = (IPv4) eth.getPayload();
			            
			            IPv4Address dstIp = ipv4.getDestinationAddress();
			           	            				    	 
			        
			            //sw.getId().toString();
			            //sw.getPorts();

			            
			            System.out.println( " sw id = " + sw.getId().toString() +" /sw.getPorts() = " +sw.getPorts() + "/sw.getPort(3) = " + sw.getPort("3") );
			            		          
			           			            
			            			            
			            
			            /*
				    	 String producerStrTest = 
				    			 "{"+ "\"srcIpv4\":" + "\""+ ipv4.getSourceAddress()+"\"" +","			    	 				    	 
				    	 +"\"dstIpv4\":"+"\"" +ipv4.getDestinationAddress() +"\""+","
				    					 + "\"srcMac\":" +"\""+ eth.getSourceMACAddress().toString() + "\""+","
				    					 + "\"dstMac\":" +"\"" +eth.getDestinationMACAddress() + "\"" 
				    					 + "}";
				    	 
			            
			            System.out.println(producerStrTest);
			            */
			            
			            
			             String producerStr =  
			            		 "{" +"\"mac\":" + "\""+ eth.getSourceMACAddress().toString() + "\""   
			             +"," +"\"switch\":" + "\"" + sw.getId().toString() +"\""
			            + "}";
			            			            
				    	 map.put(ipv4.getSourceAddress().toString(),producerStr);
				    	 
				    	 packetInProducer.sendMsg(map);
				    	 
				    	 producerStr =  "{"+ "\"mac\":" + "\""+ eth.getDestinationMACAddress().toString() + "\"" 
					             +"," +"\"switch\":" + "\"" + sw.getId().toString() +"\""
				    			 + "}";
				    	 
				    	 map.put(ipv4.getDestinationAddress().toString(), producerStr);
				    	
				    	 packetInProducer.sendMsg(map);
				    	 
				    	 //map.put( String.valueOf(System.currentTimeMillis()),  producerStr);
				    	 
				    	 //long time1 = System.currentTimeMillis();
				    	 
				    	 //long time2 = System.currentTimeMillis();
				    	 
				    	// System.out.println("latency for publishing = " + (time2-time1) );
				    	 
				 
			             
			            if (ipv4.getProtocol() == IpProtocol.TCP) {
			                // We got a TCP packet; get the payload from IPv4 
			                TCP tcp = (TCP) ipv4.getPayload();
			  
			                // Various getters and setters are exposed in TCP
			                TransportPort srcPort = tcp.getSourcePort();
			                TransportPort dstPort = tcp.getDestinationPort();
			                short flags = tcp.getFlags();
			                 
			                //more logic
			            
			            } else if (ipv4.getProtocol() == IpProtocol.UDP) {
			                // We got a UDP packet; get the payload from IPv4 
			                UDP udp = (UDP) ipv4.getPayload();
			  
			              // Various getters and setters are exposed in UDP 
			                TransportPort srcPort = udp.getSourcePort();
			                TransportPort dstPort = udp.getDestinationPort();
			                 
							// more logics		            
			            }
			            
		    	 } else if (eth.getEtherType() == EthType.ARP) {
			            
		    		 // got an ARP packet; get the payload from Ethernet
			            ARP arp = (ARP) eth.getPayload();
			  
			         // Various getters and setters are exposed in ARP 
			           boolean gratuitous = arp.isGratuitous();
			  
			        } else {
			            // Unhandled ethertype 
			        
			        }
		    	
		    	 
	

		    	 
		
		    	
		    	
		        return null;//Command.CONTINUE;
		        
            case PACKET_OUT:
            	System.out.println("packet out msg");
            	
		        return null;

            case FLOW_MOD:
            	
            	System.out.println("flow mod  msg");

		        return null;

 
		    default:
		    	
		        break;
		    }
		 
		 
		    return Command.CONTINUE;
	}
	
	private HashMap<String,String> getSwitchPortbyMac(IOFSwitch sw, String macAddr)
	{
		sw.getPort("3");		
		return null;
		
	}
	
	public void sendPacketOut(String macAddress, IOFSwitch sw ) {
		
		Ethernet l2 = new Ethernet();
		l2.setSourceMACAddress(MacAddress.of(macAddress));
		l2.setDestinationMACAddress(MacAddress.BROADCAST);
		l2.setEtherType(EthType.IPv4);
		
		
		IPv4 l3 = new IPv4();
		//l3.setSourceAddress("192.168.1.1");
		//l3.getSourceAddress();
		
		//l3.setSourceAddress(IPv4.of("192.168.1.1"));
		//l3.setDestinationAddress(IPv4.of("192.168.1.255"));
		l3.setTtl((byte) 64);
		l3.setProtocol(IpProtocol.UDP);
		
		UDP l4 = new UDP();
		l4.setSourcePort(TransportPort.of(65003));
		l4.setDestinationPort(TransportPort.of(67));
		
		Data l7 = new Data();
		l7.setData(new byte[1000]);
		
		l2.setPayload(l3);
		l3.setPayload(l4);
		l4.setPayload(l7);
		
		
		
		byte[] serializedData = l2.serialize();
		
		
		OFPacketOut po = sw.getOFFactory().buildPacketOut() /* mySwitch is some IOFSwitch object */
			    .setData(serializedData)
			    .setActions(Collections.singletonList((OFAction) sw.getOFFactory().actions().output(OFPort.FLOOD, 0xffFFffFF)))
			    .setInPort(OFPort.CONTROLLER)
			    .build();
			  
			sw.write(po);
			
	    	System.out.println("PO msg go ...");
		
		
	}
	


}
