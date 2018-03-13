package net.floodlightcontroller.orchestrator;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.core.web.ControllerVersionResource;
import net.floodlightcontroller.restserver.RestletRoutable;

public class OrchestratorWebRoutable implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {		  
		
		Router router = new Router(context); 
		     
	   router.attach("/parser/json", Parser.class);

	   router.attach("/cpudata/json", CpuData.class);
	   router.attach("/ramdata/json", RamData.class);
	   router.attach("/nicdata/json", NetworkResource.class);
	   router.attach("/processdata/json", ProcessData.class);
	   router.attach("/version/json", ControllerVersionResource.class);
	   router.attach("/system/resource/json", SystemResourceUsage.class);
	   router.attach("/networkapplication/resource/json", ApplicationUsage.class);
	 
	   return router;
	}

	@Override
	public String basePath() {
		return "/wm/orchestrator";
		
	}

}
