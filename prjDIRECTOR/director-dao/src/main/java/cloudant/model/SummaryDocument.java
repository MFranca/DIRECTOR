package cloudant.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SummaryDocument extends NoSqlAbstractEntity {
	private static final long serialVersionUID = 1L;
	private static final String DOCUMENT_TYPE = "SERVICE_SUMMARY";
	
	/*
	"total_results": 148,
	"total_pages": 3,
	"prev_url": null,
	"next_url": "/v2/services?order-direction=asc&page=2&results-per-page=50",
	*/
	private long total_results;
	private long total_pages;
	private String prev_url;
	private String next_url;
	
	// ----- Default Constructor -----
	public SummaryDocument () {
		super(); // just for legibility
		
		this.setDoctype(SummaryDocument.DOCUMENT_TYPE);
		this.setDocversion(SummaryDocument.serialVersionUID);		
	}
		
	// ----- Constructor With a JSON -----
	public SummaryDocument(JSONObject jsonData) throws IllegalArgumentException, IllegalAccessException, JSONException {
		this();
		this.populateAttributes(this, jsonData);
	}	
	
	public long getTotalResults() {
		return total_results;
	}
	public void setTotalResults(long totalResults) {
		this.total_results = totalResults;
	}
	public long getTotalPages() {
		return total_pages;
	}
	public void setTotalPages(long totalPages) {
		this.total_pages = totalPages;
	}
	public String getPreviousUrl() {
		return prev_url;
	}
	public void setPreviousUrl(String previousUrl) {
		this.prev_url = previousUrl;
	}
	public String getNextUrl() {
		return next_url;
	}
	public void setNextUrl(String nextUrl) {
		this.next_url = nextUrl;
	}
		
}
