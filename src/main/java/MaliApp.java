import java.util.HashMap;
import java.util.UUID;

public class MaliApp {
	
	public static void main(String[] args) {
		
		 HashMap<String, String> map1 = new HashMap<String, String> ();
	

			String str="";
			System.out.println("malicious network app is runnning to generate infinit data...");
			while(true) {
				
			
				try {
					map1.put(UUID.randomUUID().toString(), "a");
					
					
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println("map.size()="+map1.size());
					
					


				}
		
	}
	}
}
