package net.floodlightcontroller.orchestrator;

import java.util.UUID;


public class MarathonRule {
	
	public String id = UUID.randomUUID().toString();;
	public String cmd =null;
	public float cpus=1f;
	public int mem=512;
	public int disk=0;
	public int instances = 1;
	public Container container = new Container();
	
	
	
	class Container{
		public Docker docker = new Docker();
		
		public String type = "DOCKER";
		class Docker{
			public String image = "sdn/netapp:v2";
			public String network = "BRIDGE";
		}
	}
	
	@Override
    public String toString() {
        return "{\"id\":" + "\""+ this.id +"\""+ ","+
        		 "\"cmd\":" + this.cmd + ","+
        		"\"cpus\":" + this.cpus + ","+
        		"\"mem\":" + this.mem + ","+
        		"\"disk\":" + this.disk + ","+
        		"\"instances\":" + this.instances + ","+
        		"\"container\": {" + 
        		"\"docker\": {" +  
        		"\"image\":"+  "\"" + this.container.docker.image + "\""+ "," + 
        		"\"network\":" +"\"" + this.container.docker.network +"\""+ 
        		"}," + 
        		"\"type\":"+"\""+ this.container.type + "\""+
        		"}" + 
        		"}";
    }

}
