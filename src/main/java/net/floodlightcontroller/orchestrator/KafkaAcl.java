package net.floodlightcontroller.orchestrator;

public class KafkaAcl {
	/*
	 * ./bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:*  --topic read-device-all
	 * */
	
	//block all user
	//add permission one by one
	
	public String topic ="--topic";
	public String topicName = "";
	
	public String acl="";
	public String pathKafkaAcl = "./bin/kafka-acls.sh";
	public String authProps = "--authorizer-properties";
	public String zookeeper ="zookeeper.connect=localhost:2181";
	public String aclAddOperation = "--add"; //--remove
	public String aclRemoveOperation = "--remove";
	public String allowPermission ="--allow-principal";
	public String denyPermission ="--deny-principal";
	public String allUser = "User:*";
	
	public KafkaAcl(String topicName) {
		this.topicName = topicName;
		this.acl = new StringBuilder(this.acl)
				.append(this.pathKafkaAcl)
				.append(" ")
				.append(this.authProps)
				.append(" ")
				.append(this.zookeeper)
				
				
				.toString();
		
	}
	
	public String allowUserRead(String user) {
		this.acl = new StringBuilder(this.acl)
.append(" ")
				
				.append(this.aclAddOperation)
				.append(" ")
				.append(this.allowPermission)
				
				.append("User:")
				.append(user)
				.append(" ")

				.append("--consumer")
				
				.append(" ")
				.append(this.topic)
				
				.append(" ")
				.append(this.topicName)
				.append(" ")			
			
				.append(" --group *")
				.append(" ")
				
				.toString();
		/*
				.append(" ")
				.append("User:")
				.append(user)
				.append(" --consumer")
				
				.append(" ")
				.append(this.topic)
				
				.append(" ")
				.append(this.topicName)
				.append(" ")
				.append(this.aclAddOperation)
				.append(" ")
				.append(this.allowPermission)
			
				.append(" --group *")
				.append(" ")
				
				.toString();
				*/
	
		return this.acl;	
	}
	
	
	public String allowUserWrite(String user) {
		this.acl = new StringBuilder(this.acl)
				.append(" ")
				
				.append(this.aclAddOperation)
				.append(" ")
				.append(this.allowPermission)
				
				.append("User:")
				.append(user)
				.append(" ")

				.append("--producer")
				
				.append(" ")
				.append(this.topic)
				
				.append(" ")
				.append(this.topicName)
				.append(" ")			
			
				.append(" --group *")
				.append(" ")
				
				.toString();
		
		return this.acl;	
	}
	
	public String blockAllUser() {
		this.acl = new StringBuilder(this.acl)
				.append(this.aclAddOperation)
				.append(" ")
				.append(this.denyPermission)
				.append(" ")
				.append(this.allUser)
				.toString();
		
		
		return this.acl;		
	}
	
	

}
