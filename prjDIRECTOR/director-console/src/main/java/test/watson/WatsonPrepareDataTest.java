package test.watson;

import test.AbstractTest;
import watson.WatsonNLC;

public class WatsonPrepareDataTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();
			
			WatsonNLC watson = new WatsonNLC();
			watson.prepareAndSaveServicesData();			

		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();

		} finally {
			end();
		}
	}
}