package net.floodlightcontroller.orchestrator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;

public class Orchestrator implements IFloodlightModule, IOFMessageListener{
	
	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	protected IRestApiService restApi;
	protected FloodlightModuleContext fmlContext;
	private String sigarLibPath;
	
	public static void main(String[] args) throws JsonProcessingException, InterruptedException {
		
		  ArrayList<String> consumer1Topics = new ArrayList<String>();
		  ArrayList<String> consumer2Topics = new ArrayList<String>();

		  consumer1Topics.add(ServiceNames.READ_DEVICE_ALL);
	      consumer2Topics.add(ServiceNames.READ_STATIC_ENTRY_ALL);


	       BasicConsumer myConsumer1 = new BasicConsumer("app1",ServiceNames.READ_DEVICE_ALL);
	       BasicConsumer myConsumer2 = new BasicConsumer("app1",ServiceNames.READ_STATIC_ENTRY_ALL);
	      
	       
	       Thread thr1 = new Thread(myConsumer1);
	       Thread thr2 = new Thread(myConsumer2);
	       thr1.start();
	       thr2.start();
	       
	       myConsumer1.receiveMsg();
	       myConsumer2.receiveMsg();


	        //BasicProducer myProducer1= new BasicProducer("user1");
	        String jsonInString;

	        Map map = new HashMap();
	        int i=0;
	        while(true){

	        

	            //map.put(myProducer1.getUserName()+"-"+String.valueOf(i),String.valueOf(i));	            
	            
	           TopoViewer.publishSwitchConnection();// topoviewer = new TopoViewer();//.publishSwitchConnection();
	            
	            
	          EntryManager.publishFlowEntryList();// entryManager = new EntryManager();
	            //myConsumer.keepDataEntity(entity);

	            myConsumer1.printMsg();
	            myConsumer2.printMsg();
	            
	            	/*System.out.println(i) ;
	           
 jsonInString = new ObjectMapper().writeValueAsString("");

	            System.out.println(jsonInString);


	            i++;
	            */
	            


	            Thread.sleep(1000);

	        }
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
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
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		return null;
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
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		  Collection<Class<? extends IFloodlightService>> l =
			        new ArrayList<Class<? extends IFloodlightService>>();
			    l.add(IFloodlightProviderService.class);
				l.add(IRestApiService.class);

			    return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(Orchestrator.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		this.fmlContext = context;
		Map<String, String> configOptions = context.getConfigParams(this);
		
		this.sigarLibPath =  configOptions.get("sigarLibPath");
	    System.setProperty("java.library.path", this.sigarLibPath);
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		restApi.addRestletRoutable(new OrchestratorWebRoutable());

		
	}
	

}
