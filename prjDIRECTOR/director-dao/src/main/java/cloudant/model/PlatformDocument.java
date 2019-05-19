package cloudant.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PlatformDocument extends NoSqlAbstractEntity {
	private static final long serialVersionUID = 2L;
	private static final String DOCUMENT_TYPE = "PLATFORM";

	/*
	 * { "name": "", "build": "", "support": "https://support.run.pivotal.io",
	 * "version": 0, "description": "Cloud Foundry sponsored by Pivotal",
	 * "authorization_endpoint": "https://login.run.pivotal.io",
	 * "token_endpoint": "https://uaa.run.pivotal.io", "min_cli_version":
	 * "6.22.0", "min_recommended_cli_version": "latest", "api_version":
	 * "2.106.0", "app_ssh_endpoint": "ssh.run.pivotal.io:2222",
	 * "app_ssh_host_key_fingerprint":
	 * "e7:13:4e:32:ee:39:62:df:54:41:d7:f7:8b:b2:a7:6b",
	 * "app_ssh_oauth_client": "ssh-proxy", "doppler_logging_endpoint":
	 * "wss://doppler.run.pivotal.io:443", "routing_endpoint":
	 * "https://api.run.pivotal.io/routing" }
	 */

	private String name;
	private String support;
	private long version;
	private String description;
	private String endpoint;
	private String authorization_endpoint;
	private String token_endpoint;
	private String min_cli_version;
	private String min_recommended_cli_version;
	private String api_version;
	private String app_ssh_endpoint;
	private String app_ssh_host_key_fingerprint;
	private String app_ssh_oauth_client;
	private String doppler_logging_endpoint;
	private String routing_endpoint;

	// ----- Default Constructor -----
	public PlatformDocument() {
		super(); // just for legibility

		this.setDoctype(PlatformDocument.DOCUMENT_TYPE);
		this.setDocversion(PlatformDocument.serialVersionUID);
	}

	// ----- Constructor With a JSON -----
	public PlatformDocument(JSONObject jsonData) throws IllegalArgumentException, IllegalAccessException, JSONException {
		this();
		this.populateAttributes(this, jsonData);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthorization_endpoint() {
		return authorization_endpoint;
	}

	public void setAuthorization_endpoint(String authorization_endpoint) {
		this.authorization_endpoint = authorization_endpoint;
	}

	public String getToken_endpoint() {
		return token_endpoint;
	}

	public void setToken_endpoint(String token_endpoint) {
		this.token_endpoint = token_endpoint;
	}

	public String getMin_cli_version() {
		return min_cli_version;
	}

	public void setMin_cli_version(String min_cli_version) {
		this.min_cli_version = min_cli_version;
	}

	public String getMin_recommended_cli_version() {
		return min_recommended_cli_version;
	}

	public void setMin_recommended_cli_version(String min_recommended_cli_version) {
		this.min_recommended_cli_version = min_recommended_cli_version;
	}

	public String getApi_version() {
		return api_version;
	}

	public void setApi_version(String api_version) {
		this.api_version = api_version;
	}

	public String getApp_ssh_endpoint() {
		return app_ssh_endpoint;
	}

	public void setApp_ssh_endpoint(String app_ssh_endpoint) {
		this.app_ssh_endpoint = app_ssh_endpoint;
	}

	public String getApp_ssh_host_key_fingerprint() {
		return app_ssh_host_key_fingerprint;
	}

	public void setApp_ssh_host_key_fingerprint(String app_ssh_host_key_fingerprint) {
		this.app_ssh_host_key_fingerprint = app_ssh_host_key_fingerprint;
	}

	public String getApp_ssh_oauth_client() {
		return app_ssh_oauth_client;
	}

	public void setApp_ssh_oauth_client(String app_ssh_oauth_client) {
		this.app_ssh_oauth_client = app_ssh_oauth_client;
	}

	public String getDoppler_logging_endpoint() {
		return doppler_logging_endpoint;
	}

	public void setDoppler_logging_endpoint(String doppler_logging_endpoint) {
		this.doppler_logging_endpoint = doppler_logging_endpoint;
	}

	public String getRouting_endpoint() {
		return routing_endpoint;
	}

	public void setRouting_endpoint(String routing_endpoint) {
		this.routing_endpoint = routing_endpoint;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
}