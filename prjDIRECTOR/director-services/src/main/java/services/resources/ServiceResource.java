package services.resources;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cloudFoundry.CFService;
import cloudant.NoSqlDao;
import cloudant.dao.ServiceNoSqlDao;
import cloudant.dao.SummaryNoSqlDao;
import cloudant.model.ServiceDocument;
import cloudant.model.SummaryDocument;
import mysqlDB.model.Platform;
import mysqlDB.model.PlatformInformation;
import mysqlDB.model.Service;
import mysqlDB.model.ServiceTag;
import services.AbstractResource;
import utils.LogUtils;

@Path("/service")
public class ServiceResource extends AbstractResource {
	// **********************
	// *** SAVE OPERATION ***
	// **********************
	// Endpoint: http://localhost:9080/director-services/api/service/save?source=postman
	// Endpoint: https://director-services.mybluemix.net/api/service/save?source=tws
	// Description: Retrieves the service's metadata information from CF PaaS and save it into the NoSql database...
	//				We do this daily, that is why we do not need to check whether isInformationOutOfDate.
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
		JsonObject jsonErrorResponse;
		
		Platform platformDao = new Platform();
		CFService daoCFService;
		
		try {
			start(methodName, source);				
			
			// Get all available and active platforms...
	        List<Platform> platforms = platformDao.findActive();	
			
	        if (platforms != null) {
	            for (Platform p : platforms) {
	            	// For each platform, retrieve all the services for this PaaS...
	            	LogUtils.logInformation(methodName, "Getting services from platform " + p.getName());
	            	daoCFService = new CFService(p.getEndpoint());

					String json = daoCFService.getFirstJson();					
					saveServicesSummary(p, json, source);
					
					ServiceNoSqlDao noSqlDao = new ServiceNoSqlDao(p.getDbName()); // performance
					saveServices(p, json, source, noSqlDao);

					while (!daoCFService.getNextUrl().equals("")) { // while we have another page...
						json = daoCFService.getNextJson();
						saveServices(p, json, source, noSqlDao);
					}
					
					noSqlDao.closeConnection();
	            }
	        }
	        
		} catch (Exception ex) {			
			jsonErrorResponse = error(ex, this.getClass().getName(), methodName);
			response = jsonErrorResponse.toString();
			//LogUtils.logError(methodName, ex.getMessage());
			
		} finally {
			end(methodName);
		}

		return response;
	}	
	
	// **********************
	// *** SYNC OPERATION ***
	// **********************
	// Endpoint: http://localhost:9080/director-services/api/service/update?source=postman
	// Description: Merges the information from CF into relational database...
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
	                	isInformationOutOfDate = info.isServiceInformationOutOfDate();	                
	                
	                if (isInformationOutOfDate) {
	                	LogUtils.logInformation(methodName, "It is time to update this uSaaS information...");
	                	updateServiceInformation(p);		                    
						LogUtils.logDebug(methodName, "SERVICE information was updated...");
		                
	                } else
	                	LogUtils.logDebug(methodName, "SERVICE information was up to date...");	                
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
	// Endpoint: http://localhost:9080/director-services/api/service/info?source=postman
	// Description: Reading available/listed services from relational database...
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
		
		JsonArrayBuilder jsonPlatformsBuilder, jsonServicesBuilder;		
		JsonObject jsonPlatform, jsonService;
		//List<JsonObject> jsonPlatformServices;
		JsonObject jsonResponse;
		
		try {
			start(methodName, source);
				
			// Get all available platforms
			Platform platformDao = new Platform();
			List<Platform> platforms = platformDao.findActive();

			if (platforms != null) {				
				jsonPlatformsBuilder = Json.createArrayBuilder();
				
				for (Platform p : platforms) {
					// Preparing the response (Services)...
					//jsonPlatformServices = new ArrayList<>();
					jsonServicesBuilder = Json.createArrayBuilder();
					
					for (Service s : p.getServices()) {
						jsonService =
								Json.createObjectBuilder() // implicit root
									.add("serviceId", s.getId())									
									.add("serviceGuid", s.getGuid())
									.add("serviceURL", s.getUrl())
									.add("serviceName", s.getName())
									.add("serviceDescription", s.getDescription())
									.add("servicePlansQuantity", s.getServicePlansQuantity())
									.add("serviceStatus", (s.isActive()? "active": "inactive"))
									.add("serviceCreatedAt", s.getCreated().toLocaleString())
									.add("serviceUpdatedAt", s.getUpdated().toLocaleString())
									.add("serviceSynchronizedAt", s.getSyncAt().toLocaleString())
			                  	.build();
						
						jsonServicesBuilder.add(jsonService);
					}
					
					// Preparing the response (Platforms)...								
					jsonPlatform =
							Json.createObjectBuilder() // implicit root
								.add("id", p.getId())								
								.add("name", p.getName())	
								.add("platformsSyncDate", p.getInformation().getSyncDate().toLocaleString())
								.add("servicesSyncDate", p.getInformation().getServicesSyncDate().toLocaleString())
								.add("servicesQuantity", p.getInformation().getServicesQuantity())
								.add("services", jsonServicesBuilder.build()) // array of Services
		                  	.build();		
					
					jsonPlatformsBuilder.add(jsonPlatform);
				}
				
				jsonResponse = Json.createObjectBuilder()
						  .add("platforms", jsonPlatformsBuilder.build()) // array of Platforms
						  .build(); 	
				
				response = jsonResponse.toString();	
				
			} else
				response = "{ }";				

		} catch (Exception ex) {
			jsonResponse = error(ex, this.getClass().getName(), methodName);
			response = jsonResponse.toString();

		} finally {
			end(methodName);
		}

		return response;
	}
	
	// **********************************************************************************
	// *** OTHER OPERATIONS ***
	// **********************************************************************************
	
	// Endpoint: http://localhost:9080/director-services/api/service/tags?source=postman
	// Description: Reading available/listed service tags (features) from relational database...	
	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/json")
	@Path("/tags")
	public String getTagInformation(
			@DefaultValue("unknown") 
			@QueryParam("source") 
			String source) {
		
		String methodName = "getTagInformation";
		String response;
		
		JsonArrayBuilder jsonTagsBuilder;		
		JsonObject jsonTag;
		JsonObject jsonResponse;
		
		Map<String, Long> tags;		
		
		try {
			start(methodName, source);
				
			// Get all available tags
			tags = ServiceTag.getAllTagsWithCount();

			if (tags.isEmpty()) 
				response = "{ }";
			
			else {
				// Preparing the response (Tags)...					
				jsonTagsBuilder = Json.createArrayBuilder();
				
				//https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap				
				Iterator it = tags.entrySet().iterator();
			    while (it.hasNext()) {			        
					Map.Entry<String, Long> pair = (Map.Entry<String, Long>) it.next();
			        
			        jsonTag =
							Json.createObjectBuilder() // implicit root
								.add("tag", pair.getKey())									
								.add("hits", pair.getValue())					
		                  	.build();
			        
			        //System.out.println(pair.getKey() + " = " + pair.getValue());
			        //it.remove(); // avoids a ConcurrentModificationException
			        jsonTagsBuilder.add(jsonTag);
			    }
			    			    
			    // Preparing the response (Platforms)...								
				jsonResponse = Json.createObjectBuilder()
						.add("featuresQuantity", tags.size()) 
						.add("tags", jsonTagsBuilder.build()) // array of Tags
						.build();	
					
				response = jsonResponse.toString();	
			}				

		} catch (Exception ex) {
			jsonResponse = error(ex, this.getClass().getName(), methodName);
			response = jsonResponse.toString();

		} finally {
			end(methodName);
		}

		return response;
	}
	
	// Endpoint: http://localhost:9080/director-services/api/service/candidates?source=postman
	// Description: Positive + Negative filter for service candidates...	
	@GET
	@Produces("application/json")
	@Path("/candidates")
	public String getServiceCandidates(
			@DefaultValue("unknown") 
			@QueryParam("source") 
			String source, 
			
			@FormParam("txtPositiveFilter") // @HeaderParam => Postman Test  
			String positiveFilter, 
			@FormParam("txtNegativeFilter") // @HeaderParam => Postman Test
			String negativeFilter) {
		
		String methodName = "getServiceCandidates";
		String response;
		
		JsonArrayBuilder jsonCandidatesBuilder;		
		JsonObject jsonCandidate;
		JsonObject jsonResponse;
		
		try {
			start(methodName, source);
			
			List<String> featuresToInclude = Arrays.asList(positiveFilter.split(","));
			List<String> featuresToExclude = Arrays.asList(negativeFilter.split(","));
			
			Map<String, String> candidates;
				
			// Get all selected candidates
			candidates = Service.findCandidates(featuresToInclude, featuresToExclude);

			if (candidates.isEmpty()) 
				response = "{ }";
			
			else {
				// Preparing the response (Tags)...					
				jsonCandidatesBuilder = Json.createArrayBuilder();
				
				//https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap				
				Iterator it = candidates.entrySet().iterator();
			    while (it.hasNext()) {			        
					Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
					Long id = Long.parseLong(pair.getKey());
					Service s = new Service().findById(id);
			        
			        jsonCandidate =
							Json.createObjectBuilder() // implicit root
								.add("serviceId", pair.getKey())									
								.add("serviceName", pair.getValue())
								.add("serviceDescription", s.getDescription())
								.add("servicePlatform", s.getPlatform().getName())
		                  	.build();
			        
			        //System.out.println(pair.getKey() + " = " + pair.getValue());
			        //it.remove(); // avoids a ConcurrentModificationException
			        jsonCandidatesBuilder.add(jsonCandidate);
			    }
			    			    
			    // Preparing the response (Platforms)...								
				jsonResponse = Json.createObjectBuilder()
						.add("candidatesFound", candidates.size()) 
						.add("candidates", jsonCandidatesBuilder.build()) // array of Tags
						.build();	
					
				response = jsonResponse.toString();	
			}				

		} catch (Exception ex) {
			jsonResponse = error(ex, this.getClass().getName(), methodName);
			response = jsonResponse.toString();

		} finally {
			end(methodName);
		}

		return response;
	}
	
	// Endpoint: http://localhost:9080/director-services/api/service/ranked?source=postman
	// Description: Receive a list of service candidates and, from a technical perspective, rank them...	
	@GET
	@Produces("application/json")
	@Path("/ranked")
	public String getRankedServices(
			@DefaultValue("unknown") 
			@QueryParam("source") 
			String source, 
			
			@FormParam("txtCandidates") // @HeaderParam => Postman Test  
			String serviceCandidates) {
		
		String methodName = "getServiceCandidates";
		String response = "";
		
		JsonArrayBuilder jsonCandidatesBuilder;		
		JsonObject jsonCandidate;
		JsonObject jsonResponse;
		
		try {
			start(methodName, source);
			
			List<String> candidates = Arrays.asList(serviceCandidates.split(","));			
			
			//TODO: Criar classe para avaliação objetiva/técnica.
			
			Map<String, String> rankedCandidates;
				
			// Get all selected candidates
			//rankedCandidates = Service.findCandidates(featuresToInclude, featuresToExclude);

			/*
			if (rankedCandidates.isEmpty()) 
				response = "{ }";
			
			else {
				// Preparing the response (Tags)...					
				jsonCandidatesBuilder = Json.createArrayBuilder();
				
				//https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap				
				Iterator it = rankedCandidates.entrySet().iterator();
			    while (it.hasNext()) {			        
					Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
					Long id = Long.parseLong(pair.getKey());
					Service s = new Service().findById(id);
			        
			        jsonCandidate =
							Json.createObjectBuilder() // implicit root
								.add("serviceId", pair.getKey())									
								.add("serviceName", pair.getValue())
								.add("serviceDescription", s.getDescription())
								.add("servicePlatform", s.getPlatform().getName())
		                  	.build();
			        
			        //System.out.println(pair.getKey() + " = " + pair.getValue());
			        //it.remove(); // avoids a ConcurrentModificationException
			        jsonCandidatesBuilder.add(jsonCandidate);
			    }
			    			    
			    // Preparing the response (Platforms)...								
				jsonResponse = Json.createObjectBuilder()
						.add("candidatesFound", candidates.size()) 
						.add("candidates", jsonCandidatesBuilder.build()) // array of Tags
						.build();	
					
				response = jsonResponse.toString();	
			}
			*/				

		} catch (Exception ex) {
			jsonResponse = error(ex, this.getClass().getName(), methodName);
			response = jsonResponse.toString();

		} finally {
			end(methodName);
		}

		return response;
	}
	
	// ----------------------------------------------------------------------------------------------------
	// Private methods...
	// ----------------------------------------------------------------------------------------------------
	
	private void updateServiceInformation (Platform p) throws Exception {   
		String methodName = "updateServiceInformation";

		// Get all services from NoSql database (source information).
        ServiceNoSqlDao serviceNoSqlDao = new ServiceNoSqlDao(p.getDbName());
        List<ServiceDocument> services = serviceNoSqlDao.getActiveServiceOnDate(ServiceNoSqlDao
        		.getTimestampForToday()); // it runs after the save operation of the day (step #2)...
        
        if (services.size() == 0) // something wrong
        	return;
        
        for (ServiceDocument aService : services) {
            // ------------------------------------------------------------
            // Let us update each service in the relational database...

			// Check the existence in the relational database...
			//Service service = Service.findFirstByPlatformAndLabel(p.getId(), aService.getLabel());
        	Service service = Service.findFirstByPlatformAndGuid(p.getId(), aService.getGuid());

			if (service == null) {
				// *** Insert ***
				//LogUtils.logTrace("Inserting a service in the RDMS...");
				service = new Service();
				service.setPlatform(p);
				service.setName(aService.getLabel());
				
				service.setGuid(aService.getGuid());
				service.setUrl(aService.getUrl());
				
				try {
					service.setCreated(NoSqlDao.getDate(aService.getCreatedAt()));
					service.setUpdated(NoSqlDao.getDate(aService.getUpdatedAt()));
					
				} catch (ParseException e) {
					LogUtils.logWarning(methodName, e.getMessage());	  
				}
												
				service.setDescription(aService.getDescription());
				service.setLongDescription(aService.getLongDescription());
				service.setVersion(aService.getVersion());
				service.setInformationUrl(aService.getInfoUrl());
				service.setDocumentationUrl(aService.getDocumentationUrl());
				service.setActive(aService.isActive());
				service.setBindable(aService.isBindable());
				service.setPlanUpdateable(aService.isPlanUpdateable());
				service.setAdditionalInformation(aService.getExtra()); // 255
				service.setServicePlansUrl(aService.getServicePlansUrl());
				
				//service.setServicePlansQuantity(servicePlanDao.getServicePlansQuantity(aService.getGuid())); --> later
				service.setServicePlansQuantity(0L);
				service.setRequires(Arrays.toString(aService.getRequires()));				
				
				service.setSyncAt(new Date()); // dt_sync
				service.save();
				
				// Service's tags
				for (String aTag: aService.getTags()) {
					ServiceTag tag = new ServiceTag();
					
					tag.setService(service);
					tag.setName(aTag);					
					
					tag.save(); // in the RDMS
				}

			} else {
				// *** Update ***
				//LogUtils.logTrace("Updating a service in the RDMS...");
				//service.setPlatform(p);
				//service.setName(aService.getLabel());				
				//service.setGuid(aService.getGuid());
				service.setUrl(aService.getUrl());
				
				try {
					service.setCreated(NoSqlDao.getDate(aService.getCreatedAt()));
					service.setUpdated(NoSqlDao.getDate(aService.getUpdatedAt()));
					
				} catch (ParseException e) {
					LogUtils.logWarning(methodName, e.getMessage());	  
				}
				
				service.setDescription(aService.getDescription());
				service.setLongDescription(aService.getLongDescription());
				service.setVersion(aService.getVersion());
				service.setInformationUrl(aService.getInfoUrl());
				service.setDocumentationUrl(aService.getDocumentationUrl());
				service.setActive(aService.isActive());
				service.setBindable(aService.isBindable());
				service.setPlanUpdateable(aService.isPlanUpdateable());
				service.setAdditionalInformation(aService.getExtra()); // 255
				
				service.setServicePlansUrl(aService.getServicePlansUrl());
				
				//service.setServicePlansQuantity(servicePlanDao.getServicePlansQuantity(aService.getGuid())); --> later
				service.setRequires(Arrays.toString(aService.getRequires()));
				
				service.setSyncAt(new Date()); // dt_sync
				service.update();
				
				// Assumption: only if the number of tags has changed...
				if (service.getTags().size() != aService.getTags().length) {
					LogUtils.logTrace("Updating a service's TAGs in the RDMS...");
					// first we remove them...
					service.clearTags();
					
					// Service's tags
					for (String aTag: aService.getTags()) {
						ServiceTag tag = new ServiceTag();
						
						tag.setService(service);
						tag.setName(aTag);
						
						tag.save(); // in the RDMS
					}
				}							
			}
		}
        
        // Let us deactivate the services that we weren't able to find this time...
        LogUtils.logTrace("Let us inactivate the services that we weren't able to find this time...");
        Service.deactivateInexistentServices();
        
        // update and persist info for today.
        LogUtils.logTrace("Update and persist sync info for today...");        
        PlatformInformation info = new PlatformInformation();
        info = info.findById(p.getInformation().getId());
        info.setServicesSyncDate(new Date()); // today
        info.update();
	}
	
	private void saveServicesSummary(Platform p, String jsonText, String source) throws IllegalArgumentException, IllegalAccessException, JSONException {
		/*
		 * { "total_results": 180, "total_pages": 4, "prev_url": null,
		 * "next_url":
		 * "/v2/services?order-direction=asc&page=2&results-per-page=50",
		 * "resources": [ {
		 */
		String methodName = "saveServicesSummary";
		
		// transform/navigate the JSON.
		JSONObject json = new JSONObject(jsonText);

		// ---------------------- Services Summary --------------------------------------
		if (json.has("total_results") && !json.isNull("total_results")) {
			/*
			 * SummaryDocument aSummary = new SummaryDocument();
			 * aSummary.setTotalResults(json.getInt("total_results"));
			 * aSummary.setTotalPages(json.getInt("total_pages"));
			 */			
			// To store the summary document into the NoSql (CouchDB/Cloudant) - in this platform database.				
			SummaryDocument aSummary = new SummaryDocument(json);
			aSummary.setSource(source);
			
			// Save it into NoSql ----------------------------------------
			SummaryNoSqlDao noSqlDao = new SummaryNoSqlDao(p.getDbName());

			String id = noSqlDao.save(aSummary);
			//LogUtils.logInformation(methodName, "A summary document saved with ID:" + id);
			
		} else {
			LogUtils.logInformation(methodName, "No services summary at " + p.getName() + " were found.");
			return;
		}
	}
	
	private void saveServices(Platform p, String jsonText, String source, ServiceNoSqlDao noSqlDao) throws Exception { // InterruptedException
		/*
		 * { "total_results": 180, "total_pages": 4, "prev_url": null,
		 * "next_url":
		 * "/v2/services?order-direction=asc&page=2&results-per-page=50",
		 * "resources": [ { "metadata": { "guid":
		 * "bed0b74d-6d3e-47b4-ade5-b1407a5b1795", "url":
		 * "/v2/services/bed0b74d-6d3e-47b4-ade5-b1407a5b1795", "created_at":
		 * "2014-04-15T20:58:58Z", "updated_at": "2018-02-13T18:52:34Z" },
		 * "entity": { "label": "blazemeter", "provider": null, "url": null,
		 */

		String methodName = "saveServices";
		JSONArray servicesJson;
		JSONObject json; 
		
		// transform/navigate the JSON.
		json = new JSONObject(jsonText);
		
		// ---------------------- Services --------------------------------------
		if (json.has("resources") && !json.isNull("resources"))
			servicesJson = (JSONArray) json.get("resources");
		else {
			LogUtils.logWarning(methodName, "No services at " + p.getName() + " PaaS were found this time.");
			return;
		}

		for (int i = 0; i < servicesJson.length(); i++) { // this "page"
			// we need to store docs (services) in the NoSql (CouchDB/Cloudant) related to this platform.						
			JSONObject jsonService = servicesJson.getJSONObject(i);
			JSONObject jsonServiceMetadata = jsonService.getJSONObject("metadata");
			JSONObject jsonServiceEntity = jsonService.getJSONObject("entity");

			ServiceDocument aService = new ServiceDocument();
			aService.setSource(source);

			// *** Metadata ***
			aService.setGuid(jsonServiceMetadata.getString("guid"));
			aService.setUrl(jsonServiceMetadata.getString("url"));
			aService.setCreatedAt(jsonServiceMetadata.getString("created_at"));
			if (jsonServiceMetadata.has("updated_at") && !jsonServiceMetadata.isNull("updated_at"))
				aService.setUpdatedAt(jsonServiceMetadata.getString("updated_at"));

			// *** Entity ***
			if (jsonServiceEntity.has("label") && !jsonServiceEntity.isNull("label"))
				aService.setLabel(jsonServiceEntity.getString("label"));

			if (jsonServiceEntity.has("url") && !jsonServiceEntity.isNull("url"))
				aService.setEntityUrl(jsonServiceEntity.getString("url"));
			if (jsonServiceEntity.has("description") && !jsonServiceEntity.isNull("description"))
				aService.setDescription(jsonServiceEntity.getString("description"));
			if (jsonServiceEntity.has("long_description") && !jsonServiceEntity.isNull("long_description"))
				aService.setLongDescription(jsonServiceEntity.getString("long_description"));
			if (jsonServiceEntity.has("version") && !jsonServiceEntity.isNull("version"))
				aService.setVersion(jsonServiceEntity.getString("version"));
			if (jsonServiceEntity.has("info_url") && !jsonServiceEntity.isNull("info_url"))
				aService.setInfoUrl(jsonServiceEntity.getString("info_url"));
			if (jsonServiceEntity.has("active") && !jsonServiceEntity.isNull("active"))
				aService.setActive(jsonServiceEntity.getBoolean("active"));
			if (jsonServiceEntity.has("bindable") && !jsonServiceEntity.isNull("bindable"))
				aService.setBindable(jsonServiceEntity.getBoolean("bindable"));

			if (jsonServiceEntity.has("extra") && !jsonServiceEntity.isNull("extra"))
				aService.setExtra(jsonServiceEntity.getString("extra"));
			
			// *** Tags ***
			/*
			 * "tags": [ "mysql", "relational", "data_management",
			 * "ibm_experimental" ],
			 */
			if (jsonServiceEntity.has("tags") && !jsonServiceEntity.isNull("tags")) {
				JSONArray tagsArray = (JSONArray) jsonServiceEntity.get("tags");
				String tagsAsString = tagsArray.toString().replaceAll("[\\[\\]\"]", "");// [<specific
																						// characters>]
				String[] tagsStringArray = tagsAsString.split(",");
				aService.setTags(tagsStringArray);
			}
			/*		        
			 "requires": [
                    "route_forwarding"
                ],
            */
			if (jsonServiceEntity.has("requires") && !jsonServiceEntity.isNull("requires")) {
				JSONArray requiresArray = (JSONArray) jsonServiceEntity.get("requires");
				String requiresAsString = requiresArray.toString().replaceAll("[\\[\\]\"]", "");// [<specific
																								// characters>]
				String[] requiresStringArray = requiresAsString.split(",");
				aService.setRequires(requiresStringArray);
			}
			if (jsonServiceEntity.has("documentation_url") && !jsonServiceEntity.isNull("documentation_url"))
				aService.setExtra(jsonServiceEntity.getString("documentation_url"));

			if (jsonServiceEntity.has("plan_updateable") && !jsonServiceEntity.isNull("plan_updateable"))
				aService.setPlanUpdateable(jsonServiceEntity.getBoolean("plan_updateable"));
			if (jsonServiceEntity.has("service_plans_url") && !jsonServiceEntity.isNull("service_plans_url"))
				aService.setServicePlansUrl(jsonServiceEntity.getString("service_plans_url"));

			// we don't need this level of redundancy for now...
			// aService.setFullEntity(jsonServiceEntity.toString());
			
			// Now it is time to persist this in the NoSql
			
			// Error 429 - "Too Many Requests "
			// Lite plan capacity: 10 Writes per second.
			try {
				if (i < (servicesJson.length() - 1)) // if we will need to write another one...
					TimeUnit.MILLISECONDS.sleep(80); // 100 (1 second = 1000 milliseconds / 10 writes = 100ms per writing, considering 20ms for a writing, just need to wait 80ms)
				
			} catch (InterruptedException ex) {
				/* If an InterruptedException is thrown it means that something wants to interrupt (usually terminate) that Thread. 
				 * This is triggered by a call to the threads interrupt() method. 
				 * The wait method detects that and throws an InterruptedException so the catch code can handle the request for termination immediately 
				 * and does not have to wait till the specified time is up.
				 * */
				throw ex;
			}
			
			//ServiceNoSqlDao noSqlDao = new ServiceNoSqlDao(p.getDbName());
			//String id = noSqlDao.save(aService);			
			String id = noSqlDao.saveAndKeepConnectionOpen(aService);
			//LogUtils.logInformation(methodName, "A service was saved with document ID:" + id);			
		}
	}
}