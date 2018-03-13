package net.floodlightcontroller.orchestrator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
//import com.google.gson.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;



import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

//import javax.ws.rs.Path;

public class Parser extends ServerResource{
	
	public static void main(String[] args) {
		 ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		 
		 Gson gson = new GsonBuilder().setPrettyPrinting().create();
		 com.google.gson.JsonParser jp = new com.google.gson.JsonParser();
		 
		 
		 
		 
		 
		 //Read all topics and deny all
		 //KafkaAcl kAclDeviceInfo = new KafkaAcl("DeviceInfo");	   	       		 
		 //kAclDeviceInfo.blockAllUser();  
		 
		 
	        		
		 
	        try {
	        	
	        	Policy[] appPolicy = mapper.readValue(new File("/home/yc/floodlight/conf/policy.yaml"), Policy[].class);
	        	MarathonRule mRule = new MarathonRule();//.toString()
	        	//mRule.id = "myApp6";

	        	for(Policy p : appPolicy) {
	        		//p.appname;
	        		System.out.println(ReflectionToStringBuilder.toString(p,ToStringStyle.MULTI_LINE_STYLE));
	        		System.out.println(" ");
	        		mRule.id = p.appname;
	        		mRule.cpus =  Float.valueOf(p.cpus);
	        		mRule.mem = Integer.valueOf(p.memory.replace("MB", "").replaceAll(" ", ""));
	        		mRule.disk = Integer.valueOf(p.storage.replace("MB", "").replaceAll(" ", ""));
	        		
	        		for(String s : p.readableServices) {
	        			
	        			KafkaAcl kAcl = new KafkaAcl(s);	   	       		 
	        			kAcl.allowUserRead(p.appname);  	        		
	   	        		System.out.println( kAcl.acl);
	        			
	        		}     		
	        		
	        		for(String s : p.writableServices) {
	        			KafkaAcl kAcl = new KafkaAcl(s);	   	       		 
	        			kAcl.allowUserWrite(p.appname);    	        		
	   	        		System.out.println(kAcl.acl);
	        			
	        		}
	        		
	        		
	        		
	        		
	         	
	        	}
	        	
        		System.out.println(" ");

	        	
	        	
        		//System.out.println( mRule);

        		JsonElement je = jp.parse(mRule.toString());       		 
        		String prettyJsonString = gson.toJson(je);
       		System.out.println( prettyJsonString);

	        	

	        	//Policy policy = mapper.readValue(appPolicy, Policy.class);
	        	
	            //System.out.println(policy.appname);
	            //policy.readableServices;

	        	
	            //System.out.println(ReflectionToStringBuilder.toString(appPolicy,ToStringStyle.MULTI_LINE_STYLE));
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		
	}
	
	@Get("json")
    public Map<String, Object> retrieve() {
        HashMap<String, Object> model = new HashMap<String, Object>();
        Runtime runtime = Runtime.getRuntime();
        model.put("policy", new Long(runtime.totalMemory()));
        model.put("engine", new Long(runtime.freeMemory()));
        
        return model;
    }
	
	@Get("json?loggedIn")
    public Map<String, Object> handleRequest() {
        HashMap<String, Object> model = new HashMap<String, Object>();
        Runtime runtime = Runtime.getRuntime();
        model.put("policygfgdgdfgdfg2", new Long(runtime.totalMemory()));
        model.put("engine2", new Long(runtime.freeMemory()));
        
        return model;
    }
	
	@Get("yaml")
	public String parseYaml(String yamlPolicy) throws IOException, JsonParseException {
		
		YAMLFactory factory = new YAMLFactory();
		JsonParser parser = factory.createJsonParser(yamlPolicy); // don't be fooled by method name...
		while (parser.nextToken() != null) {
		  // do something!
			
		}
		
		return null;
	}


}
