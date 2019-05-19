package cloudant.dao;

import cloudant.NoSqlDao;

public class PlatformNoSqlDao extends NoSqlDao {
	
	public static final String PLATFORM_COUNT_ALL_VIEW = "platform/all-count";
	/*
	function (doc) {
	  if (doc.doctype == "PLATFORM" && doc.source == "workloadScheduler")
	    emit(null, 1);
	}
	*/
	
	// Constructor
	public PlatformNoSqlDao(String dbName) {
		super(dbName);
	}
	
	
	/*public String getSummaries() {
		String result;
				
		List<ServiceSummaryVO> summary = 
			this.getDbClient().view("serviceSummary/summary-view")			 	
				.includeDocs(true)
				.query(ServiceSummaryVO.class);		
			
		result =  "Number of days being monitored (summary documents) ==> " + summary.size() + System.getProperty("line.separator"); 			
		result += "======================================================" + System.getProperty("line.separator");
			
		for (ServiceSummaryVO s: summary) 
			result += "[" + s.getTotalResults() + " available services - on " + s.getTimestamp() + "]" + System.getProperty("line.separator");
			
		return result;
	}*/
	
	
	//---------------------------------------------------------------------
	// *** SAVE TO NOSQL DB ***
	
/*	public String save(NoSqlVO vo) {
		Response response = this.getDbClient().save(vo);
		this.closeDbClient();
		
		return response.getId();
	}
	
	public String saveSummary (ServiceSummaryVO vo) { // return the id
		Response response = this.getDbClient().save(vo);
		this.closeDbClient();
		
		return response.getId();		 
	}
	
	public String save (ServiceVO vo) {
		Response response = this.getDbClient().save(vo);
		this.closeDbClient();
		
		return response.getId();
	}
	
	public String savePlan (ServicePlanVO vo) {
		Response response = this.getDbClient().save(vo);
		this.closeDbClient();
		
		return response.getId();
	}*/
	
	//---------------------------------------------------------------------
		
	
	/*public String getSpace() {
		String methodName = "getSpace";
		LogUtils.doLogInformation(methodName, "Starting...");			
		
		String jsonResponse = RestUtils.callRestGet(this.endpoint, ServiceDao.GET_SPACES, this.token);		
		
		LogUtils.doLogInformation(methodName, "Ending...");
		return jsonResponse;
	}	*/

	public long getCount() {
		return this.getCountFromView(PLATFORM_COUNT_ALL_VIEW);
	}
}
