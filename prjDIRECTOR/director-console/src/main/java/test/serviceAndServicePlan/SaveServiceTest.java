package test.serviceAndServicePlan;

import services.AbstractResource;
import services.resources.ServiceResource;
import test.AbstractTest;

public class SaveServiceTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();

			AbstractResource resource = new ServiceResource();
			resource.setMetadata("consoleTest");
					
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {
			end();
		}

	}

}
