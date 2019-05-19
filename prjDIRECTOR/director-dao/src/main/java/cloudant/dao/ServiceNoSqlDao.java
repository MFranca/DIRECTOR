package cloudant.dao;

import java.util.List;

import cloudant.NoSqlDao;
import cloudant.model.ServiceDocument;

public class ServiceNoSqlDao extends NoSqlDao {
	public static final String ACTIVE_SERVICES_ON_DATE_VIEW = "service/service-active-on-date";
	public static final String SERVICES_QUANTITY_VIEW = "service/service-quantity-count";
	/*function (doc) {
		  if (doc.doctype == "SERVICE" && doc.source == "workloadScheduler" && doc.active === true)
		    emit(doc.timestamp, doc);
		}*/
	
	public static final String SERVICES_ACTIVE_DAYS_COUNT_VIEW = "service/service-active-days-count";
	/*
	function (doc) {
	  if (doc.doctype == "SERVICE" && doc.source == "workloadScheduler" && doc.active === true)
	    emit(doc.guid, 1);
	}
	*/
		
	// Constructor
	public ServiceNoSqlDao(String dbName) {
		super(dbName);
	}
	
	public List<ServiceDocument> getActiveServiceOnDate(String timestamp) {			
		List<ServiceDocument> services =  
				this.getDbClient().view(ACTIVE_SERVICES_ON_DATE_VIEW)	//ibmWorkloadScheduler		 	
				.includeDocs(true)	
				.key(timestamp)	// it will be applied to what was defined (in the view) as the first EMIT parameter (key).					
				.query(ServiceDocument.class);
		
		this.closeDbClient();
		return services;
	} 
	
	public long getServicesQuantity() {
		String key = getTimestampForToday(); // it runs after service/save (step 2)...
		return this.getCountFromView(SERVICES_QUANTITY_VIEW, key);
	}
	
	public long getServicesActiveDays(String serviceGuid) {
		String key = serviceGuid;
		return this.getCountFromView(SERVICES_ACTIVE_DAYS_COUNT_VIEW, key);
	} 
}
