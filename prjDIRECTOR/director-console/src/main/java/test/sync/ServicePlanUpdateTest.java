package test.sync;

import services.resources.ServiceResource;
import test.AbstractTest;

public class ServicePlanUpdateTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();

			// --------------------------------------------------
			ServiceResource resource = new ServiceResource();
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
