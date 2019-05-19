package test.watson;

import test.AbstractTest;
import watson.WatsonNLC;

public class WatsonCreateAndTrainClassifierTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();
			
			String fileName = "service_data_20181021.csv";
			WatsonNLC watson = new WatsonNLC();
			
			watson.createAndTrain(fileName);

		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();

		} finally {
			end();
		}
	}
}