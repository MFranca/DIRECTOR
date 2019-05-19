package stackExchange;

import java.util.Date;

import file.PropertyFile;
import utils.DateTimeUtils;
import utils.LogUtils;
import utils.RestUtils;

public class SEQuestion {

	//private static final String HEADER_AUTHORIZATION_KEY = "Bearer ";
	//http://stackapps.com/apps/oauth/register
	//https://stackapps.com/apps/oauth
	//https://api.stackexchange.com/docs/authentication
	
	public static final String APPLICATION_KEY = "((";
	//Key: ((
	//Pass this as key when making requests against the Stack Exchange API to receive a higher request quota.
	
	private PropertyFile configuration;	 
	private String endpoint;

	// ------------------------------- Constructors ------------------------------------	
	public SEQuestion() {		
		this.configuration = new PropertyFile(PropertyFile.STACK_EXCHANGE_PROPERTY_FILE);
		this.endpoint = this.configuration.getValue("endpoint");		
	}	
	
	// ------------------------------- INFO Operation ------------------------------------	
	public String getStatistics() {		
		String methodName = "getInformation";
		String response = "";		
		String resource = configuration.getValue("info");		
		
		try {
			LogUtils.logInformation(methodName, "Starting...");
			String request = this.endpoint + resource + "&key=" + APPLICATION_KEY;
			
			//LogUtils.logInformation(methodName, "Calling: " + request);
			response = RestUtils.callRestGet(request);			
			//LogUtils.logTrace(response);
			
			//JSONObject json = new JSONObject(response);
			//LogUtils.logInformation(methodName, "Response: " + json.toString());
			
		} catch (Exception ex) {
			LogUtils.logError(methodName, ex.getMessage());
			return "[" + methodName + "] Error: " + ex.getMessage();
			
		} finally {
			LogUtils.logInformation(methodName, "Ending...");
		}
				
		return response;
	}
	
	// ------------------------------- SEARCH Operation ------------------------------------
	
	public String list (Date startDate, String titleSearch) {		
		String methodName = "list";
		String response = "";		
		String resource = configuration.getValue("search");
		
		try {
			//LogUtils.logInformation(methodName, "Starting...");
			//http://api.stackexchange.com/2.2/search?site=stackoverflow&tagged=cloudfoundry;ibm-bluemix
			//&fromdate=1483228800&order=desc&sort=activity&intitle=cloudant			
			String fromDate = DateTimeUtils.toUnixEpochTime(startDate);
			//LogUtils.logTrace("EpochTime: " + fromDate);
			String request = this.endpoint + resource
				+ "&fromdate=" + fromDate 
				+ "&intitle=" + titleSearch.trim()
				+ "&key=" + APPLICATION_KEY;
			
			//LogUtils.logInformation(methodName, "Calling: " + request);
			response = RestUtils.callRestGet(request);			
			//LogUtils.logTrace(response);
			
			//JSONObject json = new JSONObject(response);
			//LogUtils.logInformation(methodName, "Response: " + json.toString());
			
		} catch (Exception ex) {
			LogUtils.logError(methodName, ex.getMessage());
			return "[" + methodName + "] Error: " + ex.getMessage();
			
		} finally {
			//LogUtils.logInformation(methodName, "Ending...");
		}
				
		return response;
	}
	
	// ------------------------------- TAG Operation ------------------------------------

	
	
	// ------------------------------- Private Methods ------------------------------------
	
}