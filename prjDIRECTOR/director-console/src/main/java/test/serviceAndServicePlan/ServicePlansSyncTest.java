package test.serviceAndServicePlan;

import services.resources.ServiceResource;
import test.AbstractTest;

public class ServicePlansSyncTest extends AbstractTest  {

	public static void main(String[] args) {
		try {
			start();
			
			ServiceResource resource = new ServiceResource();
			resource.synchronize("consoleTest");
			
			//ServicePlanResource resource = new ServicePlanResource();
			//resource.synchronize("consoleTest");
			
			/*
			Platform p = new Platform();
			//p = p.findById(21L); // Pivotal
			p = p.findById(11L); // Bluemix
			System.out.println("Plataforma: " + p.getName());
			
			ServicePlanNoSqlDao dao = new ServicePlanNoSqlDao(p.getDbName());
			//List<ServicePlanDocument> servicePlans = dao.getServicePlansOnDateForService("9dd2e109-7daa-4300-86f0-13df2075b11f"); // it might run before the save operation of the day...
        	//System.out.println("Quantidade de Planos RETORNADOS: " + servicePlans.size());
						
	        for (Service s : p.getServices()) {
	        	if (!s.isActive())
	        		continue;

	        	// update active service.
	        	System.out.println("Service GUID: " + s.getGuid());
	        	System.out.println("Quantidade de planos: " + dao.getServicePlansQuantity(s.getGuid()));
	        	
	        	List<ServicePlanDocument> servicePlans = dao.getServicePlansOnDateForService(s.getGuid()); // it might run before the save operation of the day...
	        	System.out.println("Quantidade de Planos RETORNADOS: " + servicePlans.size());	        	
	        	
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
	        	
	        	System.out.println("Encontrou um plano gratis? " + foundFree);
	        	System.out.println("Encontrou um plano publico? " + foundPublic);
	        	
	        	LogUtils.logTrace("Updating a service in the RDMS...");
				//s.save();
	        }
	        */     
			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {
			end();
		}	

	}

}
