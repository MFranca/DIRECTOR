package test.token;

import java.util.List;

import cloudFoundry.CFPlatform;
import cloudFoundry.CFService;
import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import test.AbstractTest;

public class GetTokenTest extends AbstractTest {

	public static void main(String[] args) {
		start();

		try {
			Platform platformDao = new Platform();			
			CFPlatform plataforma;
			String token;

			// para cada uma das plataformas ativas...
			List<Platform> platforms = platformDao.findActive();
			for (Platform p : platforms) {
				System.out.println("\n***\n[Plat]: " + p.getName() + " [Plat] Id: " + p.getId());
								
				plataforma = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint(), p.getInformation().getAuthorizationEndpoint());
				System.out.println("Obtendo token de acesso a partir de: " + plataforma.getCfAuthorizationEndpoint());
				
				token = plataforma.getCfToken(); //WARN  db - [login] Erro: HTTP 401 Unauthorized
				//System.out.println("[Token]: " + token);
				
				//token = plataforma.getCfToken(); //WARN  db - [login] Erro: HTTP 401 Unauthorized
				//System.out.println("[Token]: " + token);
				
				Service service = p.getServices().get(0);
				
				CFService cfService = new CFService(p.getEndpoint(), token);
				System.out.println("Planos: " + cfService.getPlansFirstJson(service.getGuid()));
				
				break;
			}
		
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
	
		} finally {
			end();
		}

	}

}
