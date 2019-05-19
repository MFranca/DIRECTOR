package test.platformInfo;

import java.util.List;

import cloudFoundry.CFPlatform;
import mysqlDB.AbstractEntity;
import mysqlDB.model.Platform;
import test.AbstractTest;

public class GetAccessTokenTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();

			Platform platformDao = new Platform();
			CFPlatform daoCFPlatform; // in order to get a valid access token...
			String token; // for getting the service commercial plans of the PaaS...

			// start(methodName, source);
			AbstractEntity.setupEntityManager();

			// Get all available platforms
			List<Platform> platforms = platformDao.findActive();

			if (platforms != null) {
				for (Platform p : platforms) {
					// For each platform, retrieve all active services on this PaaS...
					System.out.println("Platform: " + p.getDbName());
					
					daoCFPlatform = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint(),
							p.getInformation().getAuthorizationEndpoint());
					token = daoCFPlatform.getCfToken();

					System.out.println("We got this token: " + token);
				}
			}

		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();

		} finally {
			end();
		}
	}
}
