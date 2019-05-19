package services.resources;

import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import cloudFoundry.CFPlatform;
import cloudant.dao.PlatformNoSqlDao;
import cloudant.dao.ServiceNoSqlDao;
import cloudant.dao.SummaryNoSqlDao;
import cloudant.model.PlatformDocument;
import mysqlDB.AbstractEntity;
import mysqlDB.model.Platform;
import mysqlDB.model.PlatformInformation;
import services.AbstractResource;
import utils.LogUtils;

@Path("/platform")
public class PlatformResource extends AbstractResource {
	// **********************
	// *** SAVE OPERATION ***
	// **********************
	// Endpoint: http://localhost:9080/director-services/api/platform/save?source=postman
	// Endpoint: https://director-services.mybluemix.net/api/platform/save?source=tws
	// Description: Retrieves the platform's metadata information from CF PaaS and save it into the NoSql database...
	@Override
	@GET
	@Produces("application/json")
	@Path("/save")
	public String setMetadata(
			@DefaultValue("unknown") 
			@QueryParam("source") 
			String source) {
		
		String methodName = "setMetadata";
		String response = METHOD_SUCCESS;				
		JsonObject jsonResponse;
		JSONObject jsonData;
		Platform platformDao = new Platform();
				
		try {
			start(methodName, source);
						
			// Get all available active platforms.
	        List<Platform> platforms = platformDao.findActive();	
			
	        if (platforms != null) {
	            for (Platform p : platforms) {
	            	CFPlatform cf = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint());
                                        
                    // ------------------------------------------------------------
                    // Saving in the NoSql database...
                    PlatformDocument aPlatform;
                    PlatformNoSqlDao nosqlDao = new PlatformNoSqlDao(p.getDbName());
                                      
                    // transform/navigate the JSON.
                	jsonData = new JSONObject(cf.getInformation());
                	aPlatform = new PlatformDocument(jsonData);
                	
                	aPlatform.setEndpoint(p.getEndpoint());
                	aPlatform.setSource(source);
						
					String id = nosqlDao.save(aPlatform);	                    
					//LogUtils.logTrace("Info saved with document ID:" + id);
	            }
	        }
	        
		} catch (Exception ex) {
			jsonResponse = error(ex, this.getClass().getName(), methodName);
			response = jsonResponse.toString();
			
		} finally {
			end(methodName);
		}

		return response;
	}
	
	// **********************
	// *** SYNC OPERATION ***
	// **********************
	// Endpoint: http://localhost:9080/director-services/api/platform/update?source=postman
	// Description: Read from relational database, then checks if we need to update (authorization endpoint) from CF and, then, update the data in relational database.
	@Override
	@GET
	@Produces("application/json")
	@Path("/update")
	public String synchronize(
			@DefaultValue("unknown") 
			@QueryParam("source") 
			String source) {
		
		String methodName = "synchronize";
		String response = METHOD_SUCCESS;
				
		JsonObject jsonResponse;
		
		Platform platformDao = new Platform();
		boolean isInformationOutOfDate;		
		
		try {
			start(methodName, source);
			//AbstractEntity.setupEntityManager();	
			
			// Get all active platforms
	        List<Platform> platforms = platformDao.findActive();	
			
	        if (platforms != null) {
	            for (Platform p : platforms) {
	            	isInformationOutOfDate = false;
	            	PlatformInformation info = p.getInformation();
	            	
	            	// For each platform, check if there is information is up to date (today)	            		                	                
	                if (info == null) {
	                	LogUtils.logTrace("This platform does not have an information record associated...");
	                	isInformationOutOfDate = true;
	                	info = new PlatformInformation();
	                	info.setPlatform(p);
	                	info.save();
	                	
	                } else              	
	                	isInformationOutOfDate = info.isPlatformInformationOutOfDate();
	                	                
	                if (isInformationOutOfDate) {
	                	LogUtils.logInformation(methodName, "It is time to update this PaaS information...");
	                	String information = updatePlatformInformation(p, info);	                	
	                	
	                	// ------------------------------------------------------------
	                    // Saving it, also, in the NoSql database... (why?)
	                    /*
	                	PlatformDocument document;
	                    PlatformNoSqlDao nosqlDao = new PlatformNoSqlDao(p.getDbName());
	                    nosqlDao.setDbName(p.getDbName());
	                   
	                    // transform/navigate the JSON.
	                	JSONObject jsonData = new JSONObject(information);
	                	document = new PlatformDocument(jsonData);
	            			
	            		String id = nosqlDao.save(document);
						LogUtils.logTrace("Info saved with document ID:" + id);
						*/
		                
	                } else
	                	LogUtils.logTrace("PLATFORM information was up to date...");	                
	            }
	        }

	        //throw new Exception ("AWKRST012E An error occurred submitting the RESTFul Web Services job. The error message is \"Error 500: java.lang.NullPointerException: Value in JsonObjects name/value pair cannot be null\"");
	        //throw new Exception ();
	        
		} catch (Exception ex) {		
			jsonResponse = error(ex, this.getClass().getName(), methodName);
			response = jsonResponse.toString();	
						
		} finally {
			end(methodName);
		}

		return response;
	}
	
	// **********************
	// *** INFO OPERATION ***
	// **********************
	// Endpoint: http://localhost:9080/director-services/api/platform/info?source=postman
	// Endpoint: https://director-services.mybluemix.net/api/platform/info?source=postman
	// Description: Reading from relational database.
	@Override
	@GET
	@Produces("application/json")
	@Path("/info")
	public String getInformation(
			@DefaultValue("unknown") 
			@QueryParam("source") 
			String source) {
		
		String methodName = "getInformation";
		String response;
		
		JsonArrayBuilder jsonPlatforms;
		JsonObject jsonPlatform;
		JsonObject jsonResponse;
		
		try {
			start(methodName, source);
				
			//AbstractEntity.setupEntityManager();
			Platform platformDao = new Platform();					
			
			// Get all available platforms
			List<Platform> platforms = platformDao.findAll();

			if (platforms != null) {				
				jsonPlatforms = Json.createArrayBuilder();
				
				for (Platform p : platforms) {		
					LogUtils.logInformation(methodName, "Getting information from " + p.getName());
					jsonPlatform = prepareInfoResponse(p);
					jsonPlatforms.add(jsonPlatform);
				}
				
				jsonResponse = Json.createObjectBuilder()
						  .add("platforms", jsonPlatforms.build()) // array of objects
						  .build(); 	
				
				response = jsonResponse.toString();	
				
			} else
				response = "{ }";				

		} catch (Exception ex) {
			jsonResponse = error(ex, this.getClass().getName(), methodName);
			response = jsonResponse.toString();

		} finally {
			//Platform.dispose(); //  User 'ba873a1a706df5' has exceeded the 'max_user_connections' resource (current value: 5)
			end(methodName);
		}

		return response;
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	// Private methods...
	// ----------------------------------------------------------------------------------------------------

	private String updatePlatformInformation (Platform p, PlatformInformation info) {
		String methodName = "updatePlatformInformation";
		
		SummaryNoSqlDao summaryDao = new SummaryNoSqlDao(p.getDbName());
		long daysCount = summaryDao.getWorkingDays();
		
		ServiceNoSqlDao serviceDao = new ServiceNoSqlDao(p.getDbName());
		long servicesCount = serviceDao.getServicesQuantity();
		
		CFPlatform cf = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint());
        String response = cf.getInformation();	
        	                
        // update and persist info for today.
        info.setAuthorizationEndpoint(cf.getCfAuthorizationEndpoint());
        info.setSyncDate(new Date()); // today
        info.setDocument(StringUtils.left(response, 255));
        
        info.setRunningDays(daysCount);
        info.setServicesQuantity(servicesCount);
        
        info.setPlatform(p); // could this be the first time?!
        	                                                             
        //info.save();
        info.update();
        LogUtils.logInformation(this, methodName, "PLATFORM information was updated in the RELATIONAL database...");
        
        return response;
	}
	
	private JsonObject prepareInfoResponse(Platform p) {
		JsonObject jsonPlatform, jsonPlatformInformation;
		
		String platformUpdatedAt = p.getInformation().getSyncDate() == null ? "n/a" : p.getInformation().getSyncDate().toLocaleString();
		String servicesUpdatedAt = p.getInformation().getServicesSyncDate() == null ? "n/a" : p.getInformation().getServicesSyncDate().toLocaleString();
		String servicePlansUpdatedAt = p.getInformation().getServicePlansSyncDate() == null ? "n/a" : p.getInformation().getServicePlansSyncDate().toLocaleString();
		
		// Preparing the response (Info)...								
		jsonPlatformInformation =
				Json.createObjectBuilder() // implicit root
					.add("authorizationEndpoint", p.getInformation().getAuthorizationEndpoint())
										
					.add("platformUpdatedAt (20 days)", platformUpdatedAt)					
					.add("servicesUpdatedAt (10 days)", servicesUpdatedAt)
					.add("servicePlansUpdatedAt (5 days)", servicePlansUpdatedAt)
					
					.add("qtdUSaaS", p.getInformation().getServicesQuantity())
					.add("qtdDaysMonitored", p.getInformation().getRunningDays())
              	.build();
		
		// Preparing the response (Platform)...								
		jsonPlatform =
				Json.createObjectBuilder() // implicit root
					.add("id", p.getId())
					.add("status", p.getStatus())
					.add("name", p.getName())
					.add("description", p.getDescription())
					.add("endpoint", p.getEndpoint())
					.add("additionalInformation", jsonPlatformInformation)
              	.build();
		
		return jsonPlatform;
	}
}