package test.couchDb;

import java.util.List;

import cloudant.dao.PlatformNoSqlDao;
import cloudant.dao.SummaryNoSqlDao;
import mysqlDB.AbstractEntity;
import mysqlDB.model.Platform;
import test.AbstractTest;

public class PlatformCountTest extends AbstractTest {
	
	public static void main (String[] args) {
		
		start();

		try {		
			AbstractEntity.setupEntityManager(); // /director-dao/src/main/resources/META-INF/persistence.xml			
			
			List<Platform> platforms = new Platform().findAll();
			
			for (Platform p: platforms) {				
				//PlatformNoSqlDao noSqlDao = new PlatformNoSqlDao(p.getDbName());
				SummaryNoSqlDao noSqlDao = new SummaryNoSqlDao(p.getDbName());
				long count = noSqlDao.getWorkingDays();						
				
				System.out.println("Platform " + p.getName() + " - " + "dias " + count);				
			}			
					
		
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
	
		} finally {
			end();
		}
			
	}	
	
}