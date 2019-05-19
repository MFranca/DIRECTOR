package cloudFoundry;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

//import org.cloudfoundry.reactor.DefaultConnectionContext;
//import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.json.JSONObject;

import file.PropertyFile;
import utils.LogUtils;
import utils.RestUtils;

public class CFPlatform {

	//private static final String BLUEMIX_API_HOST_ENDPOINT = "https://api.ng.bluemix.net";
	
	//https://developer.ibm.com/answers/questions/384049/can-i-login-in-bluemix-v2-using-rest-api-and-api-k/
	//private static final String BLUEMIX_USER = "apikey";
	//private static final String BLUEMIX_PASSWORD = "";
	
	//private final static String CF_GET_INFO = "/v2/info";
	//private static final String GET_SPACES = "/v2/spaces";
	
	//https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#oauth2-token-endpoint-post-oauth-token
	//private static final String GET_TOKEN = "/oauth/token";
	//private static final String CLIENT_ID = "cf";
	//private static final String CLIENT_SECRET = "";

	private String cfUsername; // CLIENT_ID
	private String cfPassword; // CLIENT_SECRET
	private String cfEndpoint;
	
	private String cfAuthorizationEndpoint;// = "";
	private String cfToken = "";
	
	PropertyFile configuration = new PropertyFile();	

	// ------------------------------- Constructors ------------------------------------	
	public CFPlatform() {
		/*this.cfEndpoint= CFPlatform.BLUEMIX_API_HOST_ENDPOINT; // default
		this.cfUsername = CFPlatform.BLUEMIX_USER; // default
		this.cfPassword = CFPlatform.BLUEMIX_PASSWORD; // default*/
	}	

	public CFPlatform(String token) {
		//this.cfEndpoint= CFPlatform.BLUEMIX_API_HOST_ENDPOINT; // default
		this.cfToken = token;		
	}
	
	public CFPlatform(String username, String password) {
		//this.cfEndpoint= CFPlatform.BLUEMIX_API_HOST_ENDPOINT; // default
		this.cfUsername = username;
		this.cfPassword = password;
	}
	
	public CFPlatform(String username, String password, String endpoint) {
		this.cfEndpoint= endpoint;
		this.cfUsername = username;
		this.cfPassword = password;
	}
	
	public CFPlatform(String username, String password, String endpoint, String authorizationEndpoint) {
		this.cfEndpoint= endpoint;
		this.cfAuthorizationEndpoint = authorizationEndpoint;
		this.cfUsername = username;
		this.cfPassword = password;
	}
	
	// ------------------------------- INFO Operation ------------------------------------	
	public String getInformation() {		
		String methodName = "getInformation";
		String response;
		String resource = configuration.getValue("info");
		
		try {
			LogUtils.logInformation(methodName, "Starting...");
			LogUtils.logInformation(methodName, "Calling: " + this.cfEndpoint + resource);
			response = RestUtils.callRest(this.cfEndpoint, resource);
			
			// transform/navigate the JSON for the Authorization endpoint.
			JSONObject json = new JSONObject(response);
			LogUtils.logInformation(methodName, "Response: " + json.toString());
			
			this.cfAuthorizationEndpoint = json.getString("authorization_endpoint");			
			
		} catch (Exception ex) {
			LogUtils.logWarning(methodName, ex.getMessage());
			return "[" + methodName + "] Error: " + ex.getMessage();
			
		} finally {
			LogUtils.logInformation(methodName, "Ending...");
		}
				
		return response;
	}

	public String getCfAuthorizationEndpoint() {
		return cfAuthorizationEndpoint;
	}

	public void setCfAuthorizationEndpoint(String cfAuthorizationEndpoint) {
		this.cfAuthorizationEndpoint = cfAuthorizationEndpoint;
	}	

	// ------------------------------- TOKEN Operation ------------------------------------
	public String getCfToken() {
		String methodName = "getCfToken";
		//LogUtils.logInformation(methodName, "Starting...");
		
		try {
			if (this.cfToken.equals("")) {
				// let us get a new token...
				String response = this.login();
				JSONObject json = new JSONObject(response);				
								
				this.cfToken = json.getString("token_type") + " " + json.getString("access_token");
				LogUtils.logInformation(methodName, "A new CF Token was generated.");				
			}	
						
		} catch (Exception ex){			
			LogUtils.logWarning(methodName, ex.getMessage());			
			
		} finally {
			//this.cfToken = token;	
			//LogUtils.logInformation(methodName, "Ending...");
		}		
		
		return this.cfToken;		
	}
	
	// returns the token for REST access
	private String login() {
		String methodName = "login";
		String response;
		String resource;
		
		try {
			LogUtils.logInformation(methodName, "Starting...");
			resource = configuration.getValue("token");
			
			//LogUtils.logInformation(methodName, "Using Authorization Endpoint: " + this.cfAuthorizationEndpoint);
			//if (this.cfAuthorizationEndpoint.equals(""))
			//	this.getInformation(); // we need this to setup the UAA endpoint
			
			// *** new approach UAA - User Account and Authentication ***
			//http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/MultivaluedHashMap.html
			//Header
			MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>(); //MetadataMap				
			headers.putSingle("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");//headers.putSingle("Content-Type", "application/json;charset=UTF-8");
			headers.putSingle("Accept", "application/json;charset=UTF-8");
			headers.putSingle("Authorization", "Basic Y2Y6");			
			
			//Body
			Form body = new Form();
			body.param("grant_type", "password");
			body.param("username", this.cfUsername);
			body.param("password", this.cfPassword);
			body.param("response_type", "token");
						 
			LogUtils.logInformation(methodName, "Calling: " + this.cfAuthorizationEndpoint + resource);
			//LogUtils.logInformation(methodName, "username: " + this.cfUsername + " - password: " + this.cfPassword);
			response =  RestUtils.callRest(
					this.cfAuthorizationEndpoint, 
					resource,
					this.cfUsername, 
					this.cfPassword, 
					headers, 
					body);
			LogUtils.logInformation(methodName, "Response: " + response);

			return response;
			
		} catch (Exception ex) { 
			// [ADVERTÊNCIA] [login] Erro: HTTP 403 Forbidden
			// [main] WARN  db - [login] Erro: HTTP 500 Internal Server Error
			LogUtils.logWarning(methodName, "Erro: " + ex.getMessage());
			throw ex;
			
		} finally {
			LogUtils.logInformation(methodName, "Ending...");
		}
	}
}
