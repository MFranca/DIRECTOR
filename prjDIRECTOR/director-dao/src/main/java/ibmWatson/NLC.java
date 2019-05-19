package ibmWatson;

import file.PropertyFile;
import utils.LogUtils;
import utils.RestUtils;

public class NLC {

	public static final String CREDENTIALS_USERNAME = "";
	public static final String CREDENTIALS_PASSWORD = "";

	private PropertyFile configuration;
	private String endpoint;

	// ------------------------------- Constructors ------------------------------------
	public NLC() {
		this.configuration = new PropertyFile(PropertyFile.IBM_WATSON_PROPERTY_FILE);
		this.endpoint = this.configuration.getValue("endpoint");
	}

	public String getClassifiers() {
		String methodName = "getClassifiers";
		String response = "";		
		String resource = configuration.getValue("classifiers");		
		
		try {
			LogUtils.logInformation(methodName, "Starting...");
			String request = this.endpoint + resource;
			
			//LogUtils.logTrace("Calling: " + request);
			response = RestUtils.callRestGetBasicAuth(request, CREDENTIALS_USERNAME, CREDENTIALS_PASSWORD);			
			//LogUtils.logTrace(response);
			
		} catch (Exception ex) {
			LogUtils.logError(methodName, ex.getMessage());
			return "[" + methodName + "] Error: " + ex.getMessage();
			
		} finally {
			LogUtils.logInformation(methodName, "Ending...");
		}
				
		return response;
	}
	
	public String getClassifierDetails(String classifierId) {
		String methodName = "getClassifierDetails";
		String response = "";		
		String resource = configuration.getValue("classifier");		
		
		try {
			LogUtils.logInformation(methodName, "Starting...");
			
			String request = this.endpoint + resource.replaceAll("\\Q{id}\\E", classifierId); // https://www.baeldung.com/java-regexp-escape-char
			
			//LogUtils.logTrace("Calling: " + request);
			response = RestUtils.callRestGetBasicAuth(request, CREDENTIALS_USERNAME, CREDENTIALS_PASSWORD);			
			//LogUtils.logTrace(response);
			
		} catch (Exception ex) {
			LogUtils.logError(methodName, ex.getMessage());
			return "[" + methodName + "] Error: " + ex.getMessage();
			
		} finally {
			LogUtils.logInformation(methodName, "Ending...");
		}
				
		return response;
	}
	
	public String classify(String classifierId, String text) {
		String methodName = "classify";
		String response = "";		
		String resource = configuration.getValue("classify");		
		
		try {
			LogUtils.logInformation(methodName, "Starting...");
			
			String request = this.endpoint + resource.replaceAll("\\Q{id}\\E", classifierId) + text;
						
			//LogUtils.logTrace("Calling: " + request);
			response = RestUtils.callRestGetBasicAuth(request, CREDENTIALS_USERNAME, CREDENTIALS_PASSWORD);			
			//LogUtils.logTrace(response);
			
		} catch (Exception ex) {
			LogUtils.logError(methodName, ex.getMessage());
			return "[" + methodName + "] Error: " + ex.getMessage();
			
		} finally {
			LogUtils.logInformation(methodName, "Ending...");
		}
				
		return response;
	}
}