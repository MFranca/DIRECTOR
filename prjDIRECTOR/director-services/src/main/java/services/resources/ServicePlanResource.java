package services.resources;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONObject;

import cloudFoundry.CFPlatform;
import cloudFoundry.CFService;
import cloudant.dao.ServicePlanNoSqlDao;
import cloudant.model.ServicePlanDocument;
import mysqlDB.model.Platform;
import mysqlDB.model.PlatformInformation;
import mysqlDB.model.Service;
import services.AbstractResource;
import utils.LogUtils;

@Path("/servicePlan")
public class ServicePlanResource extends AbstractResource {
	// **********************
	// *** SAVE OPERATION ***
	// **********************
	// Endpoint: http://localhost:9080/director-services/api/servicePlan/save?source=postman
	// Endpoint: https://director-services.mybluemix.net/api/servicePlan/save?source=postmanOnBluemix
	// Description: Retrieves the service plans' metadata information from CF PaaS and save it into the NoSql database...
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
		
		Platform platformDao = new Platform();
		CFPlatform daoCFPlatform; // in order to get a valid access token...
		String token; // for getting the service commercial plans of the PaaS...
		CFService daoCFService; // public String getPlansAsJson(String guid) {
		
		try {
			start(methodName, source);
			
			// Get all active platforms
	        List<Platform> platforms = platformDao.findActive();	
			
	        if (platforms != null) {
	            for (Platform p : platforms) {	            	
	            	// we need a token for quering service's commercial plans...
	            	daoCFPlatform = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint(), p.getInformation().getAuthorizationEndpoint());
	            	token = daoCFPlatform.getCfToken();
	            	if (token.equals("")) // HTTP 401 Unauthorized
	            		continue;
	            		            	
	            	ServicePlanNoSqlDao noSqlDao = new ServicePlanNoSqlDao(p.getDbName());
	            	List<Service> services = p.findActiveServices();
	            	LogUtils.logInformation(methodName, "For PaaS "+ p.getDbName() +", let us save the commercial plans from all " + services.size() + " active services...");
	            	
	            	if (services != null) {
	    	            for (Service s : services) {
	    	            	try {
	    	            		// For each active service, retrieve all service plans from CF...	    	            	
	    	            		daoCFService = new CFService(p.getEndpoint(), token);

	    						String json = daoCFService.getPlansFirstJson(s.getGuid());	    						
	    						saveServicePlans(p, s, json, source, noSqlDao); //ServicePlanDocument
	    						
	    						while (!daoCFService.getNextPlansUrl().equals("")) { // while we have another page...
	    							json = daoCFService.getPlansNextJson();
	    							saveServicePlans(p, s, json, source, noSqlDao);
	    						}	
	    						
	    	            	} catch (Exception ex) {
	    	            		//LogUtils.logTrace("Let us try the next microservice...");
	    	            		continue;
	    	            		
	    	            	} /*finally {
	    	            			
	    	            	} */    					
	    	            }	    	            
	    	        }
	            	
	            	// let us close the connection (previously inside saveServicePlans)
	            	noSqlDao.closeConnection();
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
	// Endpoint: 
	// Description:
	@Override
	@GET
	@Produces("application/json")
	@Path("/update")
	public String synchronize (
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
			
			// Get all available platforms
	        List<Platform> platforms = platformDao.findActive();	
			
	        if (platforms != null) {
	            for (Platform p : platforms) {
	            	//LogUtils.logTrace("***********************************");
	            	//LogUtils.logTrace(">>> Platform: " + p.getName());
	            	
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
	                	isInformationOutOfDate = info.isServicePlanInformationOutOfDate();
	                	                
	                if (isInformationOutOfDate) {
	                	try {
	                		LogUtils.logInformation(methodName, "It is time to update the commercial plans information...");
	                		updateServicePlanInformation(p);
	                	} catch (Exception ex) {
	                		LogUtils.logError(methodName, ex.getMessage());
	                		continue; // let us try with the next platform...
	                	}
	                		
						LogUtils.logDebug(methodName, "SERVICE PLAN information was updated...");
		                
	                } else
	                	LogUtils.logDebug(methodName, "SERVICE PLAN information was up to date...");	      
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
	// *** INFO OPERATION ***
	// **********************
	// Endpoint: 
	// Description:
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
		JsonObject jsonPlatform, jsonPlatformServices;
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
					// Preparing the response (Info)...								
					jsonPlatformServices =
							Json.createObjectBuilder() // implicit root
								.add("authorizationEndpoint", p.getInformation().getAuthorizationEndpoint())
								.add("updatedAt", p.getInformation().getSyncDate().toLocaleString())								
		                  	.build();
					
					// Preparing the response (Platform)...								
					jsonPlatform =
							Json.createObjectBuilder() // implicit root
								.add("id", p.getId())
								.add("status", p.getStatus())
								.add("name", p.getName())
								.add("description", p.getDescription())
								.add("endpoint", p.getEndpoint())
								.add("additionalInformation", jsonPlatformServices)
		                  	.build();		
					
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
	private void updateServicePlanInformation (Platform p) throws Exception {   
		//String methodName = "updateServicePlanInformation";
     	//LogUtils.logTrace("On platofrm " + p.getDbName() + "...");
		
        // ------------------------------------------------------------
        // Let us update each service in the relational database...
		// regarding the following fields: qt_service_plans, st_has_free_plan, st_has_public_plan        
        ServicePlanNoSqlDao dao = new ServicePlanNoSqlDao(p.getDbName());
        
        // Let us first check if there is information available to sync...
     	if (dao.getServicePlansQuantityOnDate(ServicePlanNoSqlDao
     			.getTimestampForYesterday()) == 0) // it runs before the save operation (Step #6) of the day...
     		throw new Exception ("No Service Plan information was available (yesterday) for synchronizing!");

     	List<Service> platformActiveServices = p.findActiveServices();
     	//LogUtils.logTrace("Sync the plans from each of the " + platformActiveServices.size() + " active services...");
     	
     	for (Service s : platformActiveServices) { // only the active services, since it runs after the Step #4 (service/update)
        	List<ServicePlanDocument> servicePlans = dao.getServicePlansOnDateForService(s.getGuid(), ServicePlanNoSqlDao
        			.getTimestampForYesterday());  // it runs before the save operation (Step #6) of the day...
        	//LogUtils.logTrace("Retrieved " + servicePlans.size() + " service plans for service " + s.getName() + " (" + s.getId() + " / " + s.getGuid() + "), yesterday, on " + s.getPlatform().getName() + " (" + s.getPlatform().getId() + ").");
        	
        	// update active service.
        	s.setServicePlansQuantity(new Long(servicePlans.size()));
        	
        	boolean foundFree = false;
        	boolean foundPublic = false;
        	for (ServicePlanDocument aServicePlan : servicePlans) {
        		if (aServicePlan.isFree())
        			foundFree = true;
        		
        		if (aServicePlan.isPublicPlan())
        			foundPublic = true;
        		
        		if (foundFree && foundPublic)
        			break;
        	}	
        	
        	s.setFree(foundFree);
        	s.setPublic(foundPublic);
        	
        	//LogUtils.logTrace("Updating a service in the RDMS...");
			//s.save();
        	s.update();
        }        
        
		// update and persist info for today.        
        //LogUtils.logTrace("Update and persist info for today...");        
        PlatformInformation info = new PlatformInformation().findById(p.getInformation().getId()); // some JPA context issue
        info.setServicePlansSyncDate(new Date()); // today
        info.update();        
	}
	
	private void saveServicePlans(Platform p, Service s, String jsonText, String source, ServicePlanNoSqlDao noSqlDao) throws Exception { // InterruptedException
		/*
		{
	    "total_results": 3,
	    "total_pages": 1,
	    "prev_url": null,
	    "next_url": null,
	    "resources": [
	        {
	            "metadata": {
	                "guid": "e72c6030-cfe3-4477-9fb1-ca2b0408cbcb",
	                "url": "/v2/service_plans/e72c6030-cfe3-4477-9fb1-ca2b0408cbcb",
	                "created_at": "2016-09-08T12:55:17Z",
	                "updated_at": "2018-07-09T14:58:43Z"
	            },
	            "entity": {
	                "name": "Lite",
	                "free": true,
	                "description": "The Lite plan provides access to the full functionality of Cloudant for development and evaluation. The plan has a set amount of provisioned throughput capacity as shown and includes a max of 1GB of encrypted data storage.",
	                "service_guid": "14c83ad2-6fd4-439a-8c3a-d1a20f8a2381",
	                "extra": "{\"description\":\"The Lite plan provides access to the full functionality of Cloudant for development and evaluation. The plan has a set amount of provisioned throughput capacity as shown and includes a max of 1GB of encrypted data storage.\",\"plan\":\"lite\",\"lite\":true,\"bullets\":[\"1 GB of data storage\",\"Provisioned throughput capacity fixed at:\",\" 20 Lookups per second\",\" 10 Writes per second\",\" 5 Queries per second\"],\"displayName\":\"Lite\",\"testCheckInterval\":\"10\",\"singleInstanceScope\":\"org\"}",
	                "unique_id": "cloudant-lite",
	                "public": true,
	                "bindable": true,
	                "active": true,
	                "service_url": "/v2/services/14c83ad2-6fd4-439a-8c3a-d1a20f8a2381",
	                "service_instances_url": "/v2/service_plans/e72c6030-cfe3-4477-9fb1-ca2b0408cbcb/service_instances",
	                "schemas": {
	                    "service_instance": {
	                        "create": {
	                            "parameters": {}
	                        },
	                        "update": {
	                            "parameters": {}
	                        }
	                    },
	                    "service_binding": {
	                        "create": {
	                            "parameters": {}
	                        }
	                    }
	                }
	            }
	        },
		*/

		String methodName = "saveServicePlans";
		
		try {			
			JSONArray servicePlansJson;
			JSONObject json; 
			
			// transform/navigate the JSON.
			json = new JSONObject(jsonText);
			
			// ---------------------- Service Plans --------------------------------------
			if (json.has("resources") && !json.isNull("resources"))
				servicePlansJson = (JSONArray) json.get("resources");
			else {
				LogUtils.logWarning(methodName, "No service plans at " + p.getName() + " PaaS were found at this time for " + s.getName()  + " (" + s.getId() + ") uSaaS.");				
				throw new Exception("No service plans for this microservice available at this time.");
			}

			for (int i = 0; i < servicePlansJson.length(); i++) { // this "page"
				// we need to store docs (services) in the NoSql (CouchDB/Cloudant) related to this platform.						
				JSONObject jsonServicePlan = servicePlansJson.getJSONObject(i);
				JSONObject jsonServicePlanMetadata = jsonServicePlan.getJSONObject("metadata");
				JSONObject jsonServicePlanEntity = jsonServicePlan.getJSONObject("entity");

				ServicePlanDocument aServicePlan = new ServicePlanDocument();
				aServicePlan.setSource(source);

				// *** Metadata ***
				//LogUtils.logTrace("Saving metadata...");
				aServicePlan.setGuid(jsonServicePlanMetadata.getString("guid"));
				aServicePlan.setUrl(jsonServicePlanMetadata.getString("url"));
				aServicePlan.setCreatedAt(jsonServicePlanMetadata.getString("created_at"));
				if (jsonServicePlanMetadata.has("updated_at") && !jsonServicePlanMetadata.isNull("updated_at"))
					aServicePlan.setUpdatedAt(jsonServicePlanMetadata.getString("updated_at"));

				// *** Entity ***
				//LogUtils.logTrace("Saving entity...");
				if (jsonServicePlanEntity.has("name") && !jsonServicePlanEntity.isNull("name"))
					aServicePlan.setName(jsonServicePlanEntity.getString("name"));
				if (jsonServicePlanEntity.has("free") && !jsonServicePlanEntity.isNull("free"))
					aServicePlan.setFree(jsonServicePlanEntity.getBoolean("free"));			
				if (jsonServicePlanEntity.has("description") && !jsonServicePlanEntity.isNull("description"))
					aServicePlan.setDescription(jsonServicePlanEntity.getString("description"));	
				if (jsonServicePlanEntity.has("service_guid") && !jsonServicePlanEntity.isNull("service_guid"))
					aServicePlan.setServiceGuid(jsonServicePlanEntity.getString("service_guid"));
				if (jsonServicePlanEntity.has("extra") && !jsonServicePlanEntity.isNull("extra"))
					aServicePlan.setExtra(jsonServicePlanEntity.getString("extra"));			
				if (jsonServicePlanEntity.has("public") && !jsonServicePlanEntity.isNull("public"))
					aServicePlan.setPublicPlan(jsonServicePlanEntity.getBoolean("public"));
				if (jsonServicePlanEntity.has("bindable") && !jsonServicePlanEntity.isNull("bindable"))
					aServicePlan.setBindable(jsonServicePlanEntity.getBoolean("bindable"));
				if (jsonServicePlanEntity.has("active") && !jsonServicePlanEntity.isNull("active"))
					aServicePlan.setActive(jsonServicePlanEntity.getBoolean("active"));
				// * Mioto *
				if (jsonServicePlanEntity.has("service_instances_url") && !jsonServicePlanEntity.isNull("service_instances_url"))
					aServicePlan.setServiceInstancesUrl(jsonServicePlanEntity.getString("service_instances_url"));
							
				// Now it is time to persist this in the NoSql
				//ServicePlanNoSqlDao noSqlDao = new ServicePlanNoSqlDao(p.getDbName());
				String id = noSqlDao.saveAndKeepConnectionOpen(aServicePlan);
				
				// Lite plan capacity => 10 Writes per second
				// Status: 429 (Too Many Requests) {\"error\":\"too_many_requests\",\"reason\":\"You`ve exceeded your rate limit allowance. Please try again later.\"}			
				try {
					if (i < (servicePlansJson.length() - 1)) // if we will need to write another one...
						TimeUnit.MILLISECONDS.sleep(80); // 100 (1 second = 1000 milliseconds / 10 writes = 100, considering 20ms for a writing, just need to wait 80ms)
					
				} catch (InterruptedException ex) {
					/* If an InterruptedException is thrown it means that something wants to interrupt (usually terminate) that Thread. 
					 * This is triggered by a call to the threads interrupt() method. 
					 * The wait method detects that and throws an InterruptedException so the catch code can handle the request for termination immediately 
					 * and does not have to wait till the specified time is up.
					 * */
					throw ex;
				} 
				
				//LogUtils.logInformation(methodName, "A service plan was saved with document ID:" + id);			
			}
			
		} catch (Exception ex) {
			LogUtils.logError(methodName, ex.getMessage());
			// let us try to save the next...
			
		} finally {
			//noSqlDao.closeConnection(); the caller does it!	
		}		
	}
}