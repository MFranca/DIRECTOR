package test.sync;

import services.resources.ServicePlanResource;
import test.AbstractTest;

public class ServiceUpdateTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();

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
