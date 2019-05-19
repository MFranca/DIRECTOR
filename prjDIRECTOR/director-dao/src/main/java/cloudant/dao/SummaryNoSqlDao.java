package cloudant.dao;

import org.lightcouch.NoDocumentException;

import cloudant.NoSqlDao;

public class SummaryNoSqlDao extends NoSqlDao {
	//if (doc.doctype == "SERVICE_SUMMARY" && doc.source == "workloadScheduler")
	public static final String WORKING_DAYS_VIEW = "serviceSummary/running-days-count";
	
	// Constructor
	public SummaryNoSqlDao(String dbName) {
		super(dbName);
	}
	
	// Do we need something specific here?	
	public long getWorkingDays() {
		/*
		int count = 0;
		
		try {
			count = this.getDbClient().view(WORKING_DAYS_VIEW).queryForInt();			
		} catch (NoDocumentException ex) {
			count = 0;
		} finally {
			this.closeDbClient();	
		}
		
		return count;
		*/
		
		return this.getCountFromView(WORKING_DAYS_VIEW) + 1; // it might run before the save operation of the day...
	} 
}