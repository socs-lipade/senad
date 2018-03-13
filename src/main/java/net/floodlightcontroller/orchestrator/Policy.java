package net.floodlightcontroller.orchestrator;

import java.util.Map;
import java.util.List;

public class Policy {
	
	public String appname;
	public String cpus;
	public String memory;
	public String storage;
	//public String ip;
	public boolean networking = false;
	//public String approle;
	public List<String> readableServices;
	public List<String> writableServices;
	/*
	public String getAppname() {
        return appname;
    }
    public void setAppname(String appname) {
        this.appname = appname;
    }
    
	public float getCpus() {
        return cpus;
    }
    public void setCpus(float cpus) {
        this.cpus = cpus;
    }
	
	
	
class AppRole {
		
		public int priority=0;		
		public String roleName;
		
		public AppRole(String roleName) {
			
			this.roleName = roleName;
			
			if(roleName.toUpperCase().equals("ADMIN")) {
				this.priority = 100;
								
			}else if(roleName.toUpperCase().equals("SECURITY")) {
				this.priority = 99;

		
				
			}else {
				
				this.priority = 80;
				
			}
		}
		
	}
	*/
	

}
