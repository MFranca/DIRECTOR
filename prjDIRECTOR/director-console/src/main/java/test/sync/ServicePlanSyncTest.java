package test.sync;

import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import services.resources.ServicePlanResource;
import test.AbstractTest;

public class ServicePlanSyncTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();

			/*
			Platform p = new Platform().findById(21);
			System.out.println("Plataforma: " + p.getDbName() + " - " + p.getName());
			for (Service s : p.findActiveServices())
				System.out.println("Service: " + s.getName() + " (" + s.getId() + ") on " + s.getPlatform().getName());
			*/
			
			// --------------------------------------------------
			ServicePlanResource resource = new ServicePlanResource();
			// --------------------------------------------------
			
			resource.synchronize("consoleTest");

		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();

		} finally {
			end();
		}

	}

}
