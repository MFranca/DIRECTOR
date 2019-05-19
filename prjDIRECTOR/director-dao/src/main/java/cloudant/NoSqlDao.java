package cloudant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.enterprise.concurrent.AbortedException;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Document;
import org.lightcouch.NoDocumentException;
import org.lightcouch.Page;
import org.lightcouch.Response;

import com.google.gson.JsonObject;

import cloudant.model.NoSqlAbstractEntity;
import utils.LogUtils;

/*
Ref.: http://www.lightcouch.org/lightcouch-guide.html#docs-api-find
An object can be: 
1. Plain Java Object.
2. java.util.Map. 
3. com.google.gson.JsonObject.
*/

public abstract class NoSqlDao {
	public static final String PROPERTY_FILE = "couchdb.properties";
	protected CouchDbClient dbClient;
	private String dbPropertyFile;
	private String dbName;
		
	/*
	public NoSqlDao() {		
		this.dbPropertyFile = "couchdb.properties";	
	}
	*/
	
	public NoSqlDao(String dbName) {
		this.setDbName(dbName);		
		//example: bluemix-couchdb.properties
	}
	
	/*
	@Override
	public void finalize () {
		this.closeDbClient();
	}
	*/

	// *** Protected Methods ***
	// /////////////////////////
	protected CouchDbClient getDbClient() {
		//http://www.lightcouch.org/lightcouch-guide.html#configure-and-use
		if (this.dbClient == null)
			this.dbClient = new CouchDbClient(this.dbPropertyFile);
		
		return dbClient;
	} 
	
	protected void closeDbClient() {		
		this.dbClient.shutdown();		
		this.dbClient = null;
	}
	
	protected long getCountFromView(String viewName) {	
		String methodName = "getCountFromView";
		long count = 0;
			
		try {
			count = this.getDbClient().view(viewName).queryForLong();
			
		} catch (NoDocumentException ex) {
			count = 0;
		
		} catch (Exception ex) {
			LogUtils.logWarning(methodName, ex.getMessage());
			
		} finally {
			this.closeDbClient();	
		}
			
		return count;
	}	
	
	protected long getCountFromView(String viewName, String key) {
		String methodName = "getCountFromView";
		long count = 0;
			
		try {
			count = this.getDbClient().view(viewName).key(key).queryForLong();			
			
		} catch (NoDocumentException ex) {
			count = 0;
			
		} catch (Exception ex) {
			LogUtils.logWarning(methodName, ex.getMessage());
			
		} finally {
			this.closeDbClient();	
		}
			
		return count;
	}	
	
	protected long getCountFromView(String viewName, String key1, String key2) {	
		String methodName = "getCountFromView";
		long count = 0;
			
		try {
			//count = this.getDbClient().view(viewName).keys(keys).group(true).groupLevel(2).queryForLong();
			count = this.getDbClient().view(viewName).key(key1, key2).queryForLong();
			//LogUtils.logTrace("Fez o count...");
			
		} catch (NoDocumentException ex) {
			//LogUtils.logTrace("Não achou documentos...");
			count = 0;
			
		} catch (Exception ex) {
			LogUtils.logWarning(methodName, ex.getMessage());
			
		} finally {
			this.closeDbClient();	
		}
			
		return count;
	}
	
	// *** Public Methods ***
	// //////////////////////
	public String save(NoSqlAbstractEntity vo) {
		Response response = this.getDbClient().save(vo);
		this.closeDbClient();
		
		return response.getId();
	}
	
	public String saveAndKeepConnectionOpen(NoSqlAbstractEntity vo) {
		Response response = this.getDbClient().save(vo);
		//this.closeDbClient();		
		return response.getId();
	}
	
	public void closeConnection() {
		this.closeDbClient();
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
		this.dbPropertyFile = dbName + "-" + PROPERTY_FILE;
	}
	
	public static String getTimestamp(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(date); 
	}
	
	public static String getTimestampForToday() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(date); 
	}
	
	public static String getTimestampForYesterday() {
		final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    		
		Date date = cal.getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(date); 
	}
	
	public static Date getDate(String timestamp) throws ParseException {		
		//"2018-01-31T21:50:45Z"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		return sdf.parse(timestamp);
	}	
		
	public static List<JsonObject> getAllJsonDocumentsFromView(String dbName, String viewName) {
		String dbPropertyFile = dbName + "-" + PROPERTY_FILE;		
		@SuppressWarnings("resource")
		CouchDbClient dbClient = new CouchDbClient(dbPropertyFile);
				
		List<JsonObject> documents =  
				dbClient.view(viewName)			 	
				.includeDocs(true)	
				.query(JsonObject.class);
			
		dbClient.shutdown();		
		dbClient = null;
		
		return documents;
	}
	
	public static List<Document> getAllDocumentsFromView(String dbName, String viewName) {
		String dbPropertyFile = dbName + "-" + PROPERTY_FILE;		
		@SuppressWarnings("resource")
		CouchDbClient dbClient = new CouchDbClient(dbPropertyFile);
				
		List<Document> documents =  
				dbClient.view(viewName)		 	
				.includeDocs(true)	
				.query(Document.class);
			
		dbClient.shutdown();		
		dbClient = null;
		
		return documents;
	}	
	
	public static Page<Document> getDocumentPageFromView(String dbName, String viewName, String pageNumber) {
		String dbPropertyFile = dbName + "-" + PROPERTY_FILE;		
		@SuppressWarnings("resource")
		CouchDbClient dbClient = new CouchDbClient(dbPropertyFile);
		int resultsPerPage = 50;
				
		Page<Document> pageOfDocuments =  
				dbClient.view(viewName)		 	
				.reduce(false)
				.includeDocs(true)	
				.queryPage(resultsPerPage, pageNumber, Document.class); // null param gets the first page
			
		dbClient.shutdown();		
		dbClient = null;
		
		return pageOfDocuments;
	}
	
	public static String saveJsonDocument(String dbName, JsonObject document) {
		String dbPropertyFile = dbName + "-" + PROPERTY_FILE;		
		@SuppressWarnings("resource")
		CouchDbClient dbClient = new CouchDbClient(dbPropertyFile);
				
		Response result = dbClient.save(document);
		
		dbClient.shutdown();		
		dbClient = null;
		
		return result.getId();
	}	
}
