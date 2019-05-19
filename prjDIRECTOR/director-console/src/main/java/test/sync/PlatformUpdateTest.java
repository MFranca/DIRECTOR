package test.sync;

import services.resources.PlatformResource;
import test.AbstractTest;

public class PlatformUpdateTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();

			// --------------------------------------------------
			PlatformResource resource = new PlatformResource();
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
