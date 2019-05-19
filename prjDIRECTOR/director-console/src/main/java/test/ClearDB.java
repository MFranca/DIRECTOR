package test;

import java.util.List;

import mysqlDB.model.Platform;

public class ClearDB {

	public static void main(String[] args) {
		Platform.setupEntityManager();
		Platform platformDao = new Platform();

		System.out.println("Connection test: " + "/director-dao/src/main/resources/META-INF/persistence.xml");

		// Get all available platforms
		List<Platform> platforms = platformDao.findAll();

		if (platforms != null)
			for (Platform p : platforms)
				System.out.println("[Plat]: " + p.toString());

		Platform.dispose();
	}

}
