package net.floodlightcontroller.orchestrator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class SystemResourceUsage extends ServerResource {
	
	Sigar sigar= new  Sigar();
	
	  
    
		@Get("json?memory")
	    public Map<String, String> handleMemoryRequest() throws SigarException {   	 
	    	
			Map<String, String> model = new HashMap<String, String>();			
		
	    	new SystemResource(sigar);
	    	
			
				model = SystemResource.getMemMetric("" + sigar.getPid());
				
				
			    Map<String, String> map2 = SystemResource.getMemMetric();
		         for (Entry<String, String> e : map2.entrySet()) {
		             String s;

		             try {
		                 s = Sigar.formatSize(Long.valueOf(e.getValue()));
		             } catch (NumberFormatException ex) {
		                 s = ((int) (double) Double.valueOf(e.getValue())) + "%";
		             }

		            // System.out.print(e.getKey() + ": " + s + "; ");
		             
		             model.put(e.getKey(), s);
		            
		         }
				
				
				
		
			
	        //System.out.println("Resident: \t"+ Sigar.formatSize(Long.valueOf(map.get("Resident"))));
	        // System.out.println("PageFaults: \t" + map.get("PageFaults"));
	        // System.out.println("PageFaultsTotal:\t" + map.get("PageFaultsTotal"));
	        // System.out.println("Size:\t" + Sigar.formatSize(Long.valueOf(map.get("Size"))));     
	          
	        return model;
	    }
		
		@Get("json?cpu")
	    public Map<? extends String, ? extends String> handleCpuRequest() throws SigarException, InterruptedException { 
			
	    	new SystemResource(sigar);

			SystemResource.startCpuUsage();
	        String pid = ""+sigar.getPid();
	        
			
			Map<? extends String, ? extends String> model = new HashMap();
			model.putAll(SystemResource.getCpuMetric());   
	         
	          
	        return model;
	    }
		
		@Get("json?network")
	    public Map<String, String> handleNetworkRequest() throws SigarException, InterruptedException { 	    	
			
			new SystemResource(sigar);
			
			Map<String, String> model = new HashMap<String, String>();

	    	
	    	Long[] m = SystemResource.getMetric();
	        long totalrx = m[0];
	        long totaltx = m[1];
	        
	        model.put("download",  Sigar.formatSize(totalrx));
	        model.put("upload",   Sigar.formatSize(totaltx));	         
	          
	        return model;
	    }
		
		@Get("json?process")
	    public Map<String, String> handleProcessRequest() throws SigarException, InterruptedException { 	    	
			
			new SystemResource(sigar);
			
			//Map<String, String> model = new HashMap<String, String>();
			
			//SystemResource.getProcessMetric(SystemResource.getPidString());
			//System.out.println(SystemResource.getProcessMetric(SystemResource.getPidString()));

            /*
            String processString = SystemResource.getProcessMetric(SystemResource.getPidString()).replace("=", "\":");
            processString.replace(", ", "\"");

            System.out.println(processString);*/


	        //model.put("pid",  SystemResource.getPidString());
	          
	        return SystemResource.getProcessMetric(SystemResource.getPidString());// model;
	    }
		
		
		@Get("json") 
		public Map<String, String> handleRequest() throws SigarException, InterruptedException { 	    	
			
			new SystemResource(sigar);
			
			Map<String, String> model = new HashMap<String, String>();
			
			model.putAll(handleNetworkRequest());
			model.putAll(handleMemoryRequest());
			model.putAll(handleProcessRequest());
			model.putAll(handleCpuRequest());



			/*
			SystemResource.startCpuUsage();
			
	        String pid = ""+sigar.getPid();
	        

			
	         
	         
	     	model.put("totalmemory",  Sigar.formatSize(Long.valueOf(  SystemResource.getMemMetric().get("Total"))));
	     	model.put("usedmemory",  Sigar.formatSize(Long.valueOf(SystemResource.getMemMetric().get("Used"))));
	     	
	     	model.put("usedmemorypinercent", ((int) (double) Double.valueOf(SystemResource.getMemMetric().get("UsedPercent"))) + "%");
	     	
	    	Long[] m = SystemResource.getMetric();
	        long totalrx = m[0];
	        long totaltx = m[1];
	        
	        model.put("download",  Sigar.formatSize(totalrx));
	        model.put("upload",   Sigar.formatSize(totaltx));	 			
	     
			
			model.put("systemcpu", SystemResource.getCpuMetric().get("system").toString());
			model.put("idlecpu", SystemResource.getCpuMetric().get("idle").toString());
			model.put("user", SystemResource.getCpuMetric().get("user").toString());

	        model.put("pid",  SystemResource.getPidString());
	        */
	          
	        return model;
	    }
		
		

}
