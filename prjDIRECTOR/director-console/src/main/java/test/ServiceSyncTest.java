package test;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cloudFoundry.CFService;
import cloudant.dao.ServiceNoSqlDao;
import cloudant.model.ServiceDocument;
import cloudant.model.SummaryDocument;
import mysqlDB.AbstractEntity;
import mysqlDB.model.Platform;
import utils.LogUtils;

public class ServiceSyncTest {

	public static void main(String[] args) {
		AbstractEntity.setupEntityManager();
		Platform dao = new Platform();
		
		// Get all available platforms
        List<Platform> platforms = dao.findActive();
                
        if (platforms != null) {
            for (Platform p : platforms) {
            	LogUtils.logTrace("Synchying platform " + p.getEndpoint());
            	updatePlatform(p);
            }
        }
                
        AbstractEntity.dispose(); 
	}

	private static void updatePlatform(Platform p) {
		LogUtils.logTrace("Updating a platform...");
		String methodName = "updatePlatform";
		
		// With pagination.
		int page;
		boolean hasAnotherPage;		
		String json; // from PaaS metadata

		try {
			//LogUtils.logInformation(methodName, "Starting...");
			//LogUtils.logInformation(methodName, "execution triggered by: manual testing");

			CFService dao = new CFService(p.getEndpoint());
									
			page=0;						
			do {				
				page++;
				LogUtils.logTrace("Page: " + page);
				
				if (page==1)
					json = dao.getFirstJson();
				else
					json = dao.getNextJson();
				
				LogUtils.logTrace("Will persist...");
				persist(json, p.getDbName());
				
				listServices(json);
								
				hasAnotherPage = ! dao.getNextUrl().isEmpty();
			} while (hasAnotherPage);
			
			LogUtils.logInformation(methodName, "A total of " + page + " pages (service information) were saved!");
			//LogUtils.logInformation(methodName, "Ending...");

		} catch (Exception ex) {
			LogUtils.logWarning(methodName, ex.getMessage());			
		}
	}
		
	private static void persist(String json, String dbName) {				
		SummaryDocument summary;
		ServiceNoSqlDao dao = new ServiceNoSqlDao(dbName);
		
		//LogUtils.doLogInformation("persistServiceMetaData", "Starting...");
			
		// transform/navigate the JSON.
		JSONObject data = new JSONObject(json);

		// ----------------------- Snapshot ---------------------------
		/*
		 * "total_results": 148, "total_pages": 3, "prev_url": null, "next_url":
		 * "/v2/services?order-direction=asc&page=2&results-per-page=50",
		 */	
		
		String nextUrl="";
		if (data.has("next_url") && !data.isNull("next_url")) // pagination
			nextUrl = data.getString("next_url");		
			
		if (data.has("prev_url") && data.isNull("prev_url")) {
			// First page, let us save the summary
			LogUtils.logTrace("First page, let us save the summary");
			summary = new SummaryDocument();
			
			summary.setSource("manualTest");
			summary.setTotalResults(data.getInt("total_results"));
			summary.setTotalPages(data.getInt("total_pages"));
								
			summary.setNextUrl(nextUrl);
					
			dao.save(summary);
			//LogUtils.doLogInformation("persistServiceMetaData", "Service Summary saved.");
			
		}	else
			LogUtils.logTrace("Ignoring...");
	}
	
	private static void listServices(String json) {		
		ServiceDocument aService;
						
		LogUtils.logTrace("persistServiceMetaData");
		
		// transform/navigate the JSON.
		JSONObject data = new JSONObject(json);
		
		
		// ---------------------- Services --------------------------------------
		JSONArray dataArray = (JSONArray) data.get("resources");

		for (int i = 0; i < dataArray.length(); i++) { // this page
			JSONObject jsonService = dataArray.getJSONObject(i);
			JSONObject jsonServiceMetadata = jsonService.getJSONObject("metadata");
			JSONObject jsonServiceEntity = jsonService.getJSONObject("entity");
			
			aService = new ServiceDocument();
			aService.setSource("manualTesting");
			
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
			// Tags -------------------------------------------------------
			/* "tags": [
	                    "mysql",
	                    "relational",
	                    "data_management",
	                    "ibm_experimental"
	                ],*/		
			if (jsonServiceEntity.has("tags") && !jsonServiceEntity.isNull("tags")) {
				JSONArray tagsArray = (JSONArray) jsonServiceEntity.get("tags");
				String tagsAsString = tagsArray.toString().replaceAll("[\\[\\]\"]", "");// [<specific characters>]				
				String[] tagsStringArray = tagsAsString.split(",");					        
				aService.setTags(tagsStringArray);
			}
			if (jsonServiceEntity.has("requires") && !jsonServiceEntity.isNull("requires")) {
				JSONArray requiresArray = (JSONArray) jsonServiceEntity.get("requires");
				String requiresAsString = requiresArray.toString().replaceAll("[\\[\\]\"]", "");// [<specific characters>]				
				String[] requiresStringArray = requiresAsString.split(",");					        
				aService.setRequires(requiresStringArray);
			}
			if (jsonServiceEntity.has("documentation_url") && !jsonServiceEntity.isNull("documentation_url"))
				aService.setExtra(jsonServiceEntity.getString("documentation_url"));
			
			if (jsonServiceEntity.has("plan_updateable") && !jsonServiceEntity.isNull("plan_updateable"))
				aService.setPlanUpdateable(jsonServiceEntity.getBoolean("plan_updateable"));
			if (jsonServiceEntity.has("service_plans_url") && !jsonServiceEntity.isNull("service_plans_url"))
				aService.setServicePlansUrl(jsonServiceEntity.getString("service_plans_url"));
			
			//aService.setFullEntity(jsonServiceEntity.toString());	
			
			LogUtils.logTrace("A service was found: " + aService.getLabel() + " - " + aService.getDescription());			
			
		}
		
	}
	
}
