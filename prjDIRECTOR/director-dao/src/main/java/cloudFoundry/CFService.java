package cloudFoundry;

import org.json.JSONObject;

import file.PropertyFile;
import utils.LogUtils;
import utils.RestUtils;

public class CFService {	
	private String cfEndpoint;
	private String nextUrl;
	private String nextPlansUrl;
	private PropertyFile configuration = new PropertyFile();
	
	private String token;
	
	// Constructors
	public CFService(String endpoint) {
		this.cfEndpoint= endpoint;		
	}

	public CFService(String endpoint, String token) {
		this.cfEndpoint= endpoint;		
		this.token = token;
	}	
	
	public String getCfEndpoint() {
		return cfEndpoint;
	}

	public void setCfEndpoint(String cfEndpoint) {
		this.cfEndpoint = cfEndpoint;
	}
	
	public String getNextUrl() {
		return nextUrl;
	}
	
	public String getNextPlansUrl() {
		return nextPlansUrl;
	}
	
	// ------------------------------- SERVICE Operation ------------------------------------	
	public String getFirstJson() {
		String methodName="getFirstJson";
		String response;
		String resource = configuration.getValue("services");
		this.nextUrl = "";
		
		try {
			LogUtils.logInformation(methodName, "Starting...");
			LogUtils.logInformation(methodName, "Calling: " + this.cfEndpoint + resource);
			
			response = RestUtils.callRest(this.cfEndpoint, resource);
			
			// transform/navigate the JSON for the Authorization endpoint.
			JSONObject json = new JSONObject(response);
			
			// pagination
			if (json.has("next_url") && !json.isNull("next_url")) // pagination
				this.nextUrl = json.getString("next_url");
			
			response = json.toString();			
			
		} catch (Exception ex) {
			LogUtils.logWarning(methodName, ex.getMessage());
			response = "{ }";//"[" + methodName + "] Error: " + ex.getMessage();
		}
				
		return response;
	}

	public String getNextJson() {
		String methodName = "getNextJson";
		String response;
		String resource = this.nextUrl;
		
		try {
			if (resource.equals(""))
				return "{ }";
			
			LogUtils.logInformation(methodName, "Starting...");
			LogUtils.logInformation(methodName, "Calling: " + this.cfEndpoint + resource);
			
			response = RestUtils.callRest(this.cfEndpoint, resource);
			
			// transform/navigate the JSON for the NEXT URL.
			JSONObject json = new JSONObject(response);			
			
			// pagination
			if (json.has("next_url") && !json.isNull("next_url")) // pagination
				this.nextUrl = json.getString("next_url");
			else
				this.nextUrl = "";
			
			response = json.toString();			
			
		} catch (Exception ex) {
			LogUtils.logWarning(methodName, ex.getMessage());
			response = "{ }"; //"[" + methodName + "] Error: " + ex.getMessage();
		}
				
		return response;
	}
	
	// https://apidocs.cloudfoundry.org/263/services/list_all_service_plans_for_the_service.html
	// http://api.ng.bluemix.net/v2/services/9f6d8c63-4b5e-4197-aa3f-d2d7fafc8ffb/service_plans
	// http://api.ng.bluemix.net/v2/services/14c83ad2-6fd4-439a-8c3a-d1a20f8a2381/service_plans
	public String getPlansFirstJson(String guid) {
		String methodName = "getPlansFirstJson";
		String response;
		String resource = configuration.getValue("servicePlans");	
		
		try {
			this.nextPlansUrl = "";
			//LogUtils.logInformation(methodName, "Starting...");
			
			// check whether a valid token was provided in the constructor
			if (this.token == null || this.token.trim().equals(""))
				throw new Exception("Access token must be provided for querying commercial plans...");
			
			//LogUtils.logInformation(methodName, "Calling: " + this.cfEndpoint + resource.replace(":guid", guid));// + ", with token: " + this.token);
			response = RestUtils.callRestGet(this.cfEndpoint, resource.replace(":guid", guid), this.token);
			//LogUtils.logTrace("Response: " + response);
			
			// transform/navigate the JSON for the NEXT URL.
			JSONObject json = new JSONObject(response);
						
			// pagination
			if (json.has("next_url") && !json.isNull("next_url")) // pagination
				this.nextPlansUrl = json.getString("next_url");
						
			response = json.toString();			
			
		} catch (Exception ex) {			
			response = "{ }"; 
			LogUtils.logWarning(methodName, ex.getMessage()); //WARN  db - [getPlansFirstJson] HTTP 403 Forbidden
		}
				
		return response;		
	}
	
	public String getPlansNextJson() {
		String methodName = "getPlansNextJson";
		String response;
		String resource = this.nextPlansUrl;
		
		try {
			//LogUtils.logInformation(methodName, "Starting...");
			
			if (resource.equals(""))				
				return "{ }";		
			
			//LogUtils.logInformation(methodName, "Calling: " + this.cfEndpoint + resource);
			response = RestUtils.callRestGet(this.cfEndpoint, resource, this.token);
			
			// transform/navigate the JSON for the NEXT URL.
			JSONObject json = new JSONObject(response);
						
			// pagination
			if (json.has("next_url") && !json.isNull("next_url")) // pagination
				this.nextPlansUrl = json.getString("next_url");
						
			response = json.toString();			
			
		} catch (Exception ex) {
			this.nextPlansUrl = "";
			response = "{ }"; 
			LogUtils.logWarning(methodName, ex.getMessage());
		}
				
		return response;		
	}
}