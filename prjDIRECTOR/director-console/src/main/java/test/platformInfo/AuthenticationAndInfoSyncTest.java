package test.platformInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;

import cloudFoundry.CFPlatform;
import mysqlDB.AbstractEntity;
import mysqlDB.model.Platform;
import mysqlDB.model.PlatformInformation;

public class AuthenticationAndInfoSyncTest {

	public static void main(String[] args) {
		Platform platformDao = new Platform();
		boolean isInformationOutOfDate;
		String response;
		PlatformInformation info;
		
		EntityManagerFactory emf = AbstractEntity.getEntityManagerFactory();
		AbstractEntity.setEntityManager(emf.createEntityManager());
		//Platform.setupEntityManager();
		
		// Get all available platforms
        List<Platform> platforms = platformDao.findAll();
                
        if (platforms != null) {
            for (Platform p : platforms) {
            	isInformationOutOfDate = false;
            	// For each platform, check if there is information is up to date (today)
            	
                //System.out.println(p);
            	System.out.println("\n----------------------------------------------------");
                System.out.println("[Plat] Id: " + p.getId());
                System.out.println("[Plat] Name: " + p.getName());
                System.out.println("[Plat] Endpoint: " + p.getEndpoint());
                
                if (p.getInformation() == null) {
                	isInformationOutOfDate = true;
                	info = new PlatformInformation();
                	
                } else {
                	info = p.getInformation();
                	
                	System.out.println("[Info] Id: " + info.getId());
                	System.out.println("[Info] Date of the information: " + new SimpleDateFormat("dd/MM/yyyy").format(info.getSyncDate()));
                	System.out.println("[Info] Authorization Endpoint: " + info.getAuthorizationEndpoint());
                	
                	/*// check if the date is today                	
                	Date today = null;
                	Date informationDate = p.getInformation().getDate();
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						today = sdf.parse(sdf.format(new Date()));
					} catch (ParseException e) {
						e.printStackTrace();
					}                	
                	                	
                	long elapsedDays =TimeUnit.DAYS.convert(today.getTime() - informationDate.getTime(), TimeUnit.MILLISECONDS);                	
                    System.out.println ("[Info] Elapsed days: " + elapsedDays);
                    
                    if(elapsedDays>7)
                    	isInformationOutOfDate = true;	*/
                	isInformationOutOfDate = info.isPlatformInformationOutOfDate();
                }
                
                if (isInformationOutOfDate) {
                	CFPlatform cf = new CFPlatform(p.getUsername(), p.getPassword(), p.getEndpoint());
                    response = cf.getInformation();	
                    System.out.println("\n\nInfo: " + response + "\n\n");	
                
                    // update and persist info for today.
                    info.setAuthorizationEndpoint(cf.getCfAuthorizationEndpoint());
                    info.setSyncDate(new Date()); // today
                    info.setDocument(StringUtils.left(response, 255));
                    info.setPlatform(p);
                    
                    System.out.println("Salvando o info no banco de dados...");                                         
                    info.save();
                }               
            }
        }
                
        Platform.dispose(); //  User 'ba873a1a706df5' has exceeded the 'max_user_connections' resource (current value: 5)
        
        System.out.println("*** Ending ***");

	}

}
