package net.floodlightcontroller.orchestrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceUrl {
	
	private static String hostIp = "http://localhost:8082";
	private static String baseUrl=null;
	private static String url=null;
	private static String serviceUrl=null;
	
	
	public static String getServiceUrl(String serviceName) {
		
		switch (serviceName) {
		case ServiceNames.READ_SWITCH_ALL:
			baseUrl = "/wm/core";
			url = "/controller/switches/json";			
			serviceUrl = new StringBuilder().append(hostIp).append(baseUrl).append(url).toString();
		    break;
		    
		case ServiceNames.SERVICE1_1:
			baseUrl = "/wm/core";
			url = "/controller/switches/json";			
			serviceUrl = new StringBuilder().append(hostIp).append(baseUrl).append(url).toString();
		    break;
		    
		case ServiceNames.SERVICE2_1:
			baseUrl = "/wm/core";
			url = "/controller/switches/json";			
			serviceUrl = new StringBuilder().append(hostIp).append(baseUrl).append(url).toString();
		    break;
		    
		case ServiceNames.READ_DEVICE_ALL:
			baseUrl = "/wm/device";
			url = "/all/json";			
			serviceUrl = new StringBuilder().append(hostIp).append(baseUrl).append(url).toString();
		    break;
		    
		case ServiceNames.READ_STATIC_ENTRY_ALL:
			baseUrl = "/wm/staticflowpusher";
			url = "/list/all/json";			
			serviceUrl = new StringBuilder().append(hostIp).append(baseUrl).append(url).toString();
		    break;
		    
		    
		case ServiceNames.WRITE_STATIC_ENTRY:
			baseUrl = "/wm/staticflowpusher";
			url = "/json";			
			serviceUrl = new StringBuilder().append(hostIp).append(baseUrl).append(url).toString();
		    break;
	
		default:
			
			break;
		
		
		}
		
		return serviceUrl;
			
			
		
	}
	
	public static Map getServiceData(String serviceName) throws JsonProcessingException {
		
		//String url = getServiceUrl(serviceName);
		String serviceSwitchApiUrl = ServiceUrl.getServiceUrl(serviceName);
		Long currentMillis=0L;// =  System.currentTimeMillis();
		Map data = createFIFOMap(100);
		 
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
    			currentMillis = System.currentTimeMillis();
    			//System.out.println(currentMillis);

    			//System.out.println(output);
    			data.put(currentMillis.toString(),output);
        		 
        		   		
        		//myProducer.sendMsg(serviceName, dataKafka);
        		//dataKafka.clear();
    			
    		}

    		conn.disconnect();

    	  } catch (MalformedURLException e) {

    		e.printStackTrace();

    	  } catch (IOException e) {

    		e.printStackTrace();

    	  }
        
      
		// String jsonInString = new ObjectMapper().writeValueAsString(data);
		
		return data;
		
	}
	

	public static <K, V> Map<K, V> createFIFOMap(final int maxEntries) {
    	
        return new LinkedHashMap<K, V>(maxEntries*10/7, 0.7f, true) {
            
        	@Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        	
        };
    }
	
	
	private ServiceUrl() {}

}
