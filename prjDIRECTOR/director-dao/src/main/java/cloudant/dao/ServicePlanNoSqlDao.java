package cloudant.dao;

import java.util.ArrayList;
import java.util.List;

import cloudant.NoSqlDao;
import cloudant.model.ServicePlanDocument;

public class ServicePlanNoSqlDao extends NoSqlDao {
	public static final String SERVICE_PLANS_QUANTITY_VIEW = "servicePlan/commercial-plan-quantity-count";
	/*
	function (doc) {
	  if (doc.doctype == "SERVICE_PLAN" &&  doc.source == "workloadScheduler")
	    emit([doc.timestamp, doc.serviceGuid], 1);
	}
	*/
	public static final String SERVICE_PLANS_ON_DATE_VIEW = "servicePlan/commercial-plans-on-date-for-service";
	/*
 	function (doc) {
	  if (doc.doctype == "SERVICE_PLAN" &&  doc.source == "workloadScheduler")
	    emit([doc.timestamp, doc.serviceGuid], doc);
	}
	*/
	public static final String SERVICE_PLANS_COUNT_ON_DATE_VIEW = "servicePlan/commercial-plan-day-count";
	/*
	function (doc) {
  		if (doc.doctype == "SERVICE_PLAN" &&  doc.source == "workloadScheduler")
    		emit(doc.timestamp, 1);
	}
	*/
		
	// Constructor
	public ServicePlanNoSqlDao(String dbName) {
		super(dbName);
	}
	
	public List<ServicePlanDocument> getServicePlansOnDateForService(String serviceGuid, String timeStamp) {
		String[] keys = new String[2]; 
		keys[0] = timeStamp;
		keys[1] = serviceGuid;
		
		List<ServicePlanDocument> servicePlans =  
				this.getDbClient().view(SERVICE_PLANS_ON_DATE_VIEW)
				.key(keys[0], keys[1])
				.includeDocs(true)
				.query(ServicePlanDocument.class);
		
		this.closeDbClient();
		return servicePlans;
	} 
	
	public long getServicePlansQuantity(String serviceGuid) {
		return this.getCountFromView(SERVICE_PLANS_QUANTITY_VIEW, getTimestampForYesterday(), serviceGuid);
	}
	
	public long getServicePlansQuantityOnDate(String timeStamp) {
		return this.getCountFromView(SERVICE_PLANS_COUNT_ON_DATE_VIEW, timeStamp);
	} 
}
