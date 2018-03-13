package net.floodlightcontroller.orchestrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApplicationUsage extends ServerResource {
	
	public static void main(String[] args) throws JsonProcessingException {
		
		 //getServiceData();
		ApplicationUsage appResource =  new ApplicationUsage();
		System.out.println(appResource.processServiceDataStringtoMap().get("t1").cpus);
		//HashMap<String, AppResourceEntity> map =  appResource.processServiceDataStringtoMap();
			
	}
	
	@Get("json")
    public ArrayList<AppResourceEntity> handleRequest() {
		 
		ArrayList<AppResourceEntity> model = new ArrayList<AppResourceEntity> ();
        ApplicationUsage appResource =  new ApplicationUsage();

		
		for ( Map.Entry<String, AppResourceEntity> entry : appResource.processServiceDataStringtoMap().entrySet()) {
		 model.add(entry.getValue());
		}
		
	
		
		
		/*
		HashMap<String, Object> model = new HashMap<String, Object>();
        Runtime runtime = Runtime.getRuntime();
        model.put("policygfgdgdfgdfg2", new Long(runtime.totalMemory()));
        model.put("engine2", new Long(runtime.freeMemory()));
        */
        
        
		//System.out.println(appResource.processServiceDataStringtoMap().get("t1").cpus);
        
        return model;
    } 
	
	
	public  HashMap<String, AppResourceEntity> processServiceDataStringtoMap() {
		
		HashMap<String, AppResourceEntity> data = new HashMap<String, AppResourceEntity>();

		 
		 ObjectMapper mapper = new ObjectMapper();

			try {

				
				String jsonInString =  getServiceData();
				
				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject) parser.parse(jsonInString);
				
			
				//System.out.println(json.getAsJsonArray("tasks"));
				
			for(JsonElement obj : json.getAsJsonArray("tasks")) {
				/// System.out.println(obj.getAsJsonObject());
				 JsonObject subObj = obj.getAsJsonObject();
				 //JsonObject appName = subObj.getAsJsonObject("name");
				 if(subObj.get("state").toString().replace("\"", "").equals("TASK_RUNNING")) {
					 
					
					
					JsonObject  resourceObj = subObj.getAsJsonObject("resources");

					AppResourceEntity resource = new AppResourceEntity();
					resource.cpus = Float.valueOf( resourceObj.get("cpus").toString());
					
					resource.mem = Float.valueOf( resourceObj.get("mem").toString());

					resource.gpus = Float.valueOf( resourceObj.get("gpus").toString());
					resource.disk = Float.valueOf( resourceObj.get("disk").toString());
					resource.ports = resourceObj.get("ports").toString();
					
					
					resource.name = subObj.get("name").toString().replace("\"", "");



					data.put(subObj.get("name").toString().replace("\"", ""), resource);
					
					//System.out.println(subObj.get("name"));
					//System.out.println(subObj.get("state"));
					//System.out.println(subObj.get("resources"));
					
					//System.out.println(data.get("t1").cpus);
				 }
					
				
			}
				
			
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		
		
		
		return data;
		
	}
	

	
	public static String getServiceData() throws JsonProcessingException {
		
		String serviceSwitchApiUrl = "http://localhost:5050/tasks.json";
		//Long currentMillis=0L;// =  System.currentTimeMillis();
		String jsonStringData=""; // createFIFOMap(100);
		 
        try {
        	

    		URL url = new URL(serviceSwitchApiUrl);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setRequestMethod("GET");
    		conn.setRequestProperty("Accept", "application/json");
    		
    		
    		if (conn.getResponseCode() != 200) {
    			throw new RuntimeException("Failed : HTTP error code : "
    					+ conn.getResponseCode());
    		}
    		

    		BufferedReader br = new BufferedReader(new InputStreamReader(
    			(conn.getInputStream())));

    		String output=null;
    		//System.out.println("Output from Server .... \n");
    		
    		while ((output = br.readLine()) != null) {
    			
    		
    			jsonStringData= output;
    			
    			
    		}

    		conn.disconnect();

    	  } catch (MalformedURLException e) {

    		e.printStackTrace();

    	  } catch (IOException e) {

    		e.printStackTrace();

    	  }
        		
		return jsonStringData;//data;
		
	}
	
	class AppResourceEntity{
		
		public String name;
		public float disk;
		public float mem;
		public float gpus;
		public float cpus;
		public String ports;
		
	}
	
	
	

}
