package test;

import java.util.List;

import mysqlDB.model.Platform;

public class DisplayingAllPlatformsTest {

	public static void main(String[] args) {		
		Platform platformDao = new Platform();
		System.out.println("Instanciou...");

		Platform.setupEntityManager();
		System.out.println("Configurou entity manager...");

		// Get all available platforms
		List<Platform> platforms = platformDao.findAll();
		System.out.println("Fez o findAll...");

		if (platforms != null) {
			for (Platform p : platforms) {
				// System.out.println(p);
				System.out.println("\n----------------------------------------------------");
				System.out.println("[Plat] Id: " + p.getId());
				System.out.println("[Plat] Name: " + p.getName());
				System.out.println("[Plat] Endpoint: " + p.getEndpoint());
			}
		}
				
		Platform.dispose(); // User 'ba873a1a706df5' has exceeded the
							// 'max_user_connections' resource (current value:
							// 5)
		System.out.println("Fez o dispose...");

		System.out.println("*** Ending ***");

	}

}
