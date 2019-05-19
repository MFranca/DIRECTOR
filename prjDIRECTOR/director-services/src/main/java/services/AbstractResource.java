package services;

import java.time.LocalDate;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mysqlDB.AbstractEntity;
import services.resources.CFService;
import utils.LogUtils;

public abstract class AbstractResource implements CFService {
	public static final String METHOD_START = ">>> STARTING MICROSERVICE triggered by: ";
	public static final String METHOD_END = ">>> ENDING MICROSERVICE at: ";
	public static final String METHOD_SUCCESS = "{\"success\":true}";
	//public static final String SERVICE_INSTANCE_PREFIX = "drctr_";
	
	//private String token = "";

	@Override
	public boolean isAlive(String source) {
		// make sure the service is alive...
		return true;
	}
	
	protected void start(String method, String source) {		
		LogUtils.logInformation(method, METHOD_START + source);
		AbstractEntity.setupEntityManager();
	}
	
	protected void end(String method) {
		AbstractEntity.dispose();
		LogUtils.logInformation(method, METHOD_END + LocalDate.now().toString());
	}
	
	protected JsonObject error(Exception ex, String resourceName, String method) {
		String stackTraceMethodName = "n/a";
		String stackTraceClassName = "n/a";
		String stackTraceFileName = "n/a";
		String stackTraceLineNumber = "n/a";
		
		//SRVE0315E: Ocorreu uma exceção: java.lang.Throwable: java.lang.NullPointerException: Value in JsonObjects name/value pair cannot be null
		String reason = ex.getMessage() == null ? "unknown" : ex.getMessage();
		
		if (ex.getStackTrace().length > 0) {
			stackTraceMethodName = ex.getStackTrace()[0].getMethodName();
			stackTraceClassName = ex.getStackTrace()[0].getClassName();
			stackTraceFileName = ex.getStackTrace()[0].getFileName();
			stackTraceLineNumber = Integer.toString(ex.getStackTrace()[0].getLineNumber());			
		}
		
		JsonObjectBuilder stackTrace = Json.createObjectBuilder()
				.add("stackTraceMethodName", stackTraceMethodName)
				.add("stackTraceClassName", stackTraceClassName)
				.add("stackTraceFileName", stackTraceFileName)
				.add("stackTraceLineNumber", stackTraceLineNumber);
		
		JsonObject response = 
				Json.createObjectBuilder()
						.add("success", false)
						.add(resourceName, Json.createObjectBuilder()
						.add("exception", Json.createObjectBuilder()
								.add("method", method)							
								.add("description", reason)
						)
						.add("stackTrace", stackTrace)
				)
				.build();
		
		LogUtils.logError(method, reason);
		return response;
	}	
}