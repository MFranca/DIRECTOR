package test.pivotal;

import java.util.List;

import cloudFoundry.CFPlatform;
import cloudFoundry.CFService;
import cloudant.dao.ServicePlanNoSqlDao;
import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import test.AbstractTest;

public class GetPivotalTokenTest extends AbstractTest {
	public static void main(String[] args) {
		start();

		try {
			Platform p = new Platform().findById(21L);
			
			CFPlatform daoCFPlatform = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint(), p.getInformation().getAuthorizationEndpoint());
        	String token = daoCFPlatform.getCfToken();
        	System.out.println("Token: " + token);
			
        	List<Service> services = p.getServices();
        	ServicePlanNoSqlDao noSqlDao = new ServicePlanNoSqlDao(p.getDbName());        	
        	if (services != null) {
	            for (Service s : services) {
	            	if (!s.isActive())
	            		continue;
	            	
	            	// For each active service, retrieve all service plans...	    	            	
	            	CFService daoCFService = new CFService(p.getEndpoint(), token);

					String json = daoCFService.getPlansFirstJson(s.getGuid());	    						
					System.out.println("JSON: " + json.toString());
	            }	    	            	
	        }
        	
		
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
	
		} finally {
			end();
		}

	}
}
