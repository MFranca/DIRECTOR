package test;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cloudFoundry.CFPlatform;
import mysqlDB.model.Platform;
import mysqlDB.model.PlatformInformation;

public class ConnectionsManagerTest {

	public static void main(String[] args) {
		Platform dao = new Platform();		
				
		// Print all the Students
        List<Platform> platforms = dao.findAll();
        String response;
        
        if (platforms != null) {
            for (Platform p : platforms) {
                System.out.println(p);
                System.out.println("Endpoint: " + p.getEndpoint());
                
                
                
                CFPlatform cf = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint());
                response = cf.getInformation();
                System.out.println("\n\nInfo: " + response + "\n\n");
                
                // TODO: verificar se já existe um registro antes (hoje, para essa plataforma)
                // persist infor for today.
                PlatformInformation info = new PlatformInformation();
                info.setAuthorizationEndpoint(cf.getCfAuthorizationEndpoint());
                info.setSyncDate(new Date()); // today
                info.setDocument(StringUtils.left(response, 255));
                info.setPlatform(p);
                
                System.out.println("Salvando o info no banco de dados...");
                info.save();                
            }
        }
                
        dao.dispose(); //  User 'ba873a1a706df5' has exceeded the 'max_user_connections' resource (current value: 5)
        
        System.out.println("*** Ending ***");
	}
}