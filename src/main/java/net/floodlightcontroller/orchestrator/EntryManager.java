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

public class EntryManager  {
	
	static String serviceName = ServiceNames.READ_STATIC_ENTRY_ALL;
	static String serviceEntryListApiUrl = ServiceUrl.getServiceUrl(serviceName);
	

		
	public static void publishFlowEntryList() throws JsonProcessingException{
		

		//BasicProducer myProducer= new BasicProducer("user1",serviceName);
		 
		BasicProducer myProducer= new BasicProducer("app1",serviceName);
		myProducer.sendMsg( ServiceUrl.getServiceData(serviceName));
		/*
		
		Long currentMillis=0L;// =  System.currentTimeMillis();
		Map dataKafka = createFIFOMap(100);
        
        try {

    		URL url = new URL(serviceEntryListApiUrl);
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
        		dataKafka.put(currentMillis.toString(),output);
        		
        		
        		
        		myProducer.sendMsg( dataKafka);
        		//dataKafka.clear();
    			
    		}

    		conn.disconnect();

    	  } catch (MalformedURLException e) {

    		e.printStackTrace();

    	  } catch (IOException e) {

    		e.printStackTrace();

    	  }
    	  */

		
	}
	
public static <K, V> Map<K, V> createFIFOMap(final int maxEntries) {
    	
        return new LinkedHashMap<K, V>(maxEntries*10/7, 0.7f, true) {
            
        	@Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        	
        };
    }
	

}
