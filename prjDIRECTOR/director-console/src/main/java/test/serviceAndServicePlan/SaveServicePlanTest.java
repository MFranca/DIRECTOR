package test.serviceAndServicePlan;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cloudFoundry.CFPlatform;
import cloudFoundry.CFService;
import cloudant.dao.ServicePlanNoSqlDao;
import cloudant.model.ServicePlanDocument;
import mysqlDB.AbstractEntity;
import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import test.AbstractTest;
import utils.LogUtils;

public class SaveServicePlanTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();

			//String methodName = "setMetadata";
			//String response = METHOD_SUCCESS;			
			String source = "Test";
			//JsonObject jsonResponse;
			
			Platform platformDao = new Platform();			
			CFPlatform daoCFPlatform; // in order to get a valid access token...
			String token; // for getting the service commercial plans of the PaaS...
			CFService daoCFService; // public String getPlansAsJson(String guid) {
			
			//start(methodName, source);
			AbstractEntity.setupEntityManager();	
			
			// Get all available platforms
	        List<Platform> platforms = platformDao.findActive();	        
			
	        if (platforms != null) {
	            for (Platform p : platforms) {
	            	// For each platform, retrieve all active services on this PaaS...
	            	System.out.println("Platform: " + p.getDbName());
	            	List<Service> services = p.getServices();   
	            	daoCFPlatform = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint(), p.getInformation().getAuthorizationEndpoint());
	            	token = daoCFPlatform.getCfToken();

	            	if (services != null) {
	    	            for (Service s : services) {
	    	            	// For each active service, retrieve all service plans...
	    	            	System.out.println("Service: " + s.getName());
	    	            	if (s.isActive()) {	    	            		
	    	            		daoCFService = new CFService(p.getEndpoint(), token);

	    						String json = daoCFService.getPlansFirstJson(s.getGuid());	    						
	    						saveServicePlans(p, json, source); //ServicePlanDocument
	    						
	    						while (!daoCFService.getNextPlansUrl().equals("")) { // while we have another page...
	    							json = daoCFService.getPlansNextJson();
	    							saveServicePlans(p, json, source);
	    						}
	    					}
	    	            }	    	            	
	    	        }
	            }	            	
	        }	        
			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {
			end();
		}
	}
	
	private static void saveServicePlans(Platform p, String jsonText, String source) {
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

		System.out.println("Saving this service's plans...");
		
		String methodName = "saveServicePlans";
		JSONArray servicePlansJson;
		JSONObject json; 
		
		// transform/navigate the JSON.
		json = new JSONObject(jsonText);
		
		// ---------------------- Service Plans --------------------------------------
		if (json.has("resources") && !json.isNull("resources"))
			servicePlansJson = (JSONArray) json.get("resources");
		else {
			LogUtils.logInformation(methodName, "No service plans at " + p.getName() + " uSaaS were found at this time.");
			return;
		}

		for (int i = 0; i < servicePlansJson.length(); i++) { // this "page"
			// we need to store docs (services) in the NoSql (CouchDB/Cloudant) related to this platform.						
			JSONObject jsonServicePlan = servicePlansJson.getJSONObject(i);
			JSONObject jsonServicePlanMetadata = jsonServicePlan.getJSONObject("metadata");
			JSONObject jsonServicePlanEntity = jsonServicePlan.getJSONObject("entity");

			ServicePlanDocument aServicePlan = new ServicePlanDocument();
			aServicePlan.setSource(source);

			// *** Metadata ***
			aServicePlan.setGuid(jsonServicePlanMetadata.getString("guid"));
			aServicePlan.setUrl(jsonServicePlanMetadata.getString("url"));
			aServicePlan.setCreatedAt(jsonServicePlanMetadata.getString("created_at"));
			if (jsonServicePlanMetadata.has("updated_at") && !jsonServicePlanMetadata.isNull("updated_at"))
				aServicePlan.setUpdatedAt(jsonServicePlanMetadata.getString("updated_at"));

			// *** Entity ***
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
			ServicePlanNoSqlDao noSqlDao = new ServicePlanNoSqlDao(p.getDbName());
			String id = noSqlDao.save(aServicePlan);
			System.out.println("A service plan was saved with document ID:" + id);			
		}
	}
}