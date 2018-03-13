package net.floodlightcontroller.orchestrator;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetFlags;

import java.util.Map;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.List;
import java.util.LinkedList;



public class SystemResource {
	

    static Map<String, Long> rxCurrentMap = new HashMap<String, Long>();
    static Map<String, List<Long>> rxChangeMap = new HashMap<String, List<Long>>();
    static Map<String, Long> txCurrentMap = new HashMap<String, Long>();
    static Map<String, List<Long>> txChangeMap = new HashMap<String, List<Long>>();
    private static Sigar sigar;
    
    private static Map<String, Long> pageFoults;
    
    SystemResource(Sigar s){
   	 String javaLibPath="/home/yc/floodlight/lib";
        System.setProperty("java.library.path", javaLibPath);
   	 sigar = s;
   	
   }
    
   
   

    public static void main(String[] args) throws SigarException, InterruptedException {
    	 
          
          
    	 new SystemResource(new Sigar());
    	// SystemResource.startCpuUsage();
    	 
    	  while(true) {
	            
	            System.out.println("-------------Network usage----------------------");

	            
	            //network start
	            Long[] m = getMetric();
	            long totalrx = m[0];
	            long totaltx = m[1];
	            System.out.print("totalrx(download): ");
	            System.out.println("\t" + Sigar.formatSize(totalrx));
	            System.out.print("totaltx(upload): ");
	            System.out.println("\t" + Sigar.formatSize(totaltx));
	            //network end
	            
	            System.out.println("---------------Cpu usage--------------------");

	            //cpu start
	            SystemResource.startCpuUsage();
	            String pid = ""+sigar.getPid();
	            System.out.println("getMetric(pid)="+getMetric(pid));
	            
	            /*
	            for(Double d:getCpuMetric()){
	                System.out.println("fetch all elements in loop \t"+d);
	            }*/

	            //cpu end
	            
	            System.out.println("---------------Mem--------------------");

	            //mem start
	            Map<String, String> map = SystemResource.getMemMetric("" + sigar.getPid());

	            System.out.println("Resident: \t"+ Sigar.formatSize(Long.valueOf(map.get("Resident"))));
	            System.out.println("PageFaults: \t" + map.get("PageFaults"));
	            System.out.println("PageFaultsTotal:\t" + map.get("PageFaultsTotal"));
	            System.out.println("Size:\t" + Sigar.formatSize(Long.valueOf(map.get("Size"))));

	            Map<String, String> map2 = getMemMetric();
	            for (Entry<String, String> e : map2.entrySet()) {
	                String s;

	                try {
	                    s = Sigar.formatSize(Long.valueOf(e.getValue()));
	                } catch (NumberFormatException ex) {
	                    s = ((int) (double) Double.valueOf(e.getValue())) + "%";
	                }

	                System.out.print(e.getKey() + ": " + s + "; ");
	            }
	            //mem end
	            
	            System.out.println("---------process------------");

	            //process start
	            
	            System.out.println(SystemResource.getProcessMetric(getPidString()));

	            //process end

	            Thread.sleep(1000);
	        }
    	 //SystemResource.startNetworkUsage();
    	 
    	 //SystemResource.getCpuUsage();
    	 //SystemResource.getMemUsage();
    	// SystemResource.getNetworkUsage();
    	 //SystemResource.getProcessUsage();

    	 
    	
    }
    
    
 
  
	
	
	public static void getCpuUsage() throws InterruptedException, SigarException{
		
		startCpuUsage();
		
	}
	
	public static void getMemUsage() {
		
	}
	
	public static void getNetworkUsage() throws SigarException, InterruptedException {
		startNetworkUsage();
		
	}
	
	public static void getProcessUsage() {
		
		
	}
	
	/**
	 * networking 
	 * 
	 * */
	
	 public static void startNetworkUsage() throws SigarException, InterruptedException {
	        while (true) {

	            Thread.sleep(1000);
	        }

	    }

	    public static Long[] getMetric() throws SigarException {
	        for (String ni : sigar.getNetInterfaceList()) {
	            // System.out.println(ni);
	            NetInterfaceStat netStat = sigar.getNetInterfaceStat(ni);
	            NetInterfaceConfig ifConfig = sigar.getNetInterfaceConfig(ni);
	            String hwaddr = null;
	            if (!NetFlags.NULL_HWADDR.equals(ifConfig.getHwaddr())) {
	                hwaddr = ifConfig.getHwaddr();
	            }
	            if (hwaddr != null) {
	                long rxCurrenttmp = netStat.getRxBytes();
	                saveChange(rxCurrentMap, rxChangeMap, hwaddr, rxCurrenttmp, ni);
	                long txCurrenttmp = netStat.getTxBytes();
	                saveChange(txCurrentMap, txChangeMap, hwaddr, txCurrenttmp, ni);
	            }
	        }
	        long totalrxDown = getMetricData(rxChangeMap);
	        long totaltxUp = getMetricData(txChangeMap);
	        for (List<Long> l : rxChangeMap.values())
	            l.clear();
	        for (List<Long> l : txChangeMap.values())
	            l.clear();
	        return new Long[] { totalrxDown, totaltxUp };
	    }

	    private static long getMetricData(Map<String, List<Long>> rxChangeMap) {
	        long total = 0;
	        for (Entry<String, List<Long>> entry : rxChangeMap.entrySet()) {
	            int average = 0;
	            for (Long l : entry.getValue()) {
	                average += l;
	            }
	            total += average / entry.getValue().size();
	        }
	        return total;
	    }

	    private static void saveChange(Map<String, Long> currentMap,
	                                   Map<String, List<Long>> changeMap, String hwaddr, long current,
	                                   String ni) {
	        Long oldCurrent = currentMap.get(ni);
	        if (oldCurrent != null) {
	            List<Long> list = changeMap.get(hwaddr);
	            if (list == null) {
	                list = new LinkedList<Long>();
	                changeMap.put(hwaddr, list);
	            }
	            list.add((current - oldCurrent));
	        }
	        currentMap.put(ni, current);
	    }
	    
	    public static String networkInfo() throws SigarException {
	        String info = sigar.getNetInfo().toString();
	        info += "\n"+ sigar.getNetInterfaceConfig().toString();
	        return info;
	    }

	    public static String getDefaultGateway() throws SigarException {
	        return sigar.getNetInfo().getDefaultGateway();
	    }
	    
	    /*
	     * cpu
	     * */
	    
	    public static void startCpuUsage() throws InterruptedException, SigarException {
	    	
	        new Thread() {
	            public void run() {
	                while(true)
	                   BigInteger.probablePrime(MAX_PRIORITY, new Random());

	            };
	        }.start();

	      
	    }

	    public String cpuInfo() throws SigarException {
	        CpuInfo[] infos = sigar.getCpuInfoList();
	        CpuInfo info = infos[0];

	        String infoString = info.toString();
	        if ((info.getTotalCores() != info.getTotalSockets())
	                || (info.getCoresPerSocket() > info.getTotalCores())) {
	            infoString+=" Physical CPUs: " + info.getTotalSockets();
	            infoString+=" Cores per CPU: " + info.getCoresPerSocket();
	        }

	        long cacheSize = info.getCacheSize();
	        if (cacheSize != Sigar.FIELD_NOTIMPL) {
	            infoString+="Cache size...." + cacheSize;
	        }
	        return infoString;
	    }

	    public static Map getCpuMetric() throws SigarException {
	        
	    	CpuPerc cpu = sigar.getCpuPerc();
	        
	        Map model = new HashMap<String, Double>();
	        
	        double system = cpu.getSys();
	        double user = cpu.getUser();
	        double idle = cpu.getIdle();
	        
	        model.put("cpusystem", CpuPerc.format(system));
	        model.put("cpuidle", CpuPerc.format(idle));
	        model.put("cpuuser",CpuPerc.format(user) );
	        return model;
	        
	        //System.out.println("system: "+CpuPerc.format(system)+ ", idle: " +CpuPerc.format(idle) +", user: "+CpuPerc.format(user));
	        //return new Double[] {system, user, idle};
	    }

	    public static double getMetric(String pid) throws SigarException {
	        ProcCpu cpu = sigar.getProcCpu(pid);
	        //System.out.println(sigar.getProcFd(pid));
	        //System.err.println(cpu.toString());
	        return cpu.getPercent();
	    }
	    
	    //mem start
	    
	    public static Map<String, String> getMemMetric() throws SigarException {
	    	
	    	Mem mem = sigar.getMem();
	    	
	    	return (Map<String, String>) mem.toMap();
	    	
	    }
	    
	    public static Map<String, String> getMemMetric(String pid) throws SigarException {
	    	
	    	if (pageFoults == null)
	    		
	    		pageFoults = new HashMap<String, Long>();
	    	
	    	ProcMem state = sigar.getProcMem(pid); 
	    	Map<String, String> map = new TreeMap<String, String>(state.toMap());
	    	
	    	if (!pageFoults.containsKey(pid))
	    		pageFoults.put(pid, state.getPageFaults());
	    	map.put("PageFaults", "" + (state.getPageFaults() - pageFoults.get(pid)));    
	    	map.put("PageFaultsTotal", ""+state.getPageFaults());    
	    	return map;
	    	
	    }
	    //mem end
	    
	    //process start
	    public static Map<String, String> getProcessMetric() throws SigarException {
	        ProcStat state = sigar.getProcStat();
	        return (Map<String, String>) state.toMap();
	    }

	    public static Map<String, String> getProcessMetric(String pid) throws SigarException {
	        ProcState state = sigar.getProcState(pid);
	        return (Map<String, String>) state.toMap();
	    }
	    
	    public static String getPidString() {
	        return ""+sigar.getPid();
	    }

	    
	    //process end
	
	

}
