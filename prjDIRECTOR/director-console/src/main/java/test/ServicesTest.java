package test;

import java.text.SimpleDateFormat;
import java.util.List;

import mysqlDB.AbstractEntity;
import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import utils.LogUtils;

public class ServicesTest {

	public static void main(String[] args) {
		AbstractEntity.setupEntityManager();
		Platform dao = new Platform();
		
		// Get all available platforms
        List<Platform> platforms = dao.findActive();
                
        if (platforms != null) {
            for (Platform p : platforms) {            	
            	LogUtils.logTrace("----------------------------------------------------");
            	LogUtils.logTrace("[Plat] Id: " + p.getId());
            	LogUtils.logTrace("[Plat] Name: " + p.getName());
            	LogUtils.logTrace("[Plat] Endpoint: " + p.getEndpoint());
                
                // Information            	
            	LogUtils.logTrace("[Info] Id: " + p.getInformation().getId());
            	LogUtils.logTrace("[Info] Date of the information: " + new SimpleDateFormat("dd/MM/yyyy").format(p.getInformation().getSyncDate()));
            	LogUtils.logTrace("[Info] Authorization Endpoint: " + p.getInformation().getAuthorizationEndpoint());
            	
            	// Services
            	for (Service s: p.getServices()) {
            		LogUtils.logTrace("[Service] Id: " + s.getId());
                	LogUtils.logTrace("[Service] Date of the information: " + new SimpleDateFormat("dd/MM/yyyy").format(s.getSyncAt()));
                	LogUtils.logTrace("[Service] Label: " + s.getName());
                	LogUtils.logTrace("[Service] Description: " + s.getDescription());
            	}
            }
        }
                
        AbstractEntity.dispose(); 
	}

}
