package test.serviceAndServicePlan;

import services.resources.ServiceResource;
import test.AbstractTest;

public class CallingUpdateServiceTest extends AbstractTest {

	public static void main(String[] args) {
		
		try {
			start();

			/*
			// update and persist info for today.
	        LogUtils.logTrace("Update and persist info for today...");        
	        PlatformInformation info = new PlatformInformation();
	        info = info.findById(2); // 12, 22, 32
	        LogUtils.logTrace(info.getId() + " - " + info.getServicesSyncDate().toGMTString());
	        
	        info.setServicesSyncDate(new Date()); // today
	        info.update();
	        LogUtils.logTrace(info.getId() + " - " + info.getServicesSyncDate().toGMTString());
	        //info.setPlatform(p); // could this be the first time?!
	        //p.save();
	         * 
	         */
	        			
			// --------------------------------------------------
			ServiceResource restService = new ServiceResource();
			restService.synchronize("consoleTest");
			// --------------------------------------------------
			
			// testar update
			//Service dao = new Service();
			//dao.deactivateServices();			
			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {			
			end();	
		}
	}
}
