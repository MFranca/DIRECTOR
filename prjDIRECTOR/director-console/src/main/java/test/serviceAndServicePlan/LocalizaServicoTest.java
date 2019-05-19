package test.serviceAndServicePlan;

import cloudant.NoSqlDao;
import mysqlDB.model.Service;
import test.AbstractTest;

public class LocalizaServicoTest extends AbstractTest {

	public static void main(String[] args) {
		
		try {
			Long id = 11L;
			String label = "elephantsql";
			
			start();

			// Verificar se existe no banco relacional...
			Service service = Service.findFirstByPlatformAndLabel(id, label);
			
			if (service == null)
				System.out.println("Não encontrou no banco relacional...");
			else
				System.out.println("Encontrou no banco relacional...");
			
			String data="2018-01-31T21:50:45Z";
			System.out.println( "Data: " + NoSqlDao.getDate(data));
			
			System.out.println("Finalizou com sucesso.");
			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			
			end();	
		}
		
	}

}
