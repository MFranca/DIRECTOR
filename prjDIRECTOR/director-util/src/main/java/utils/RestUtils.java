package utils;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
//TODO: actually, we should refactor this to JsonObject (javax.json.JsonObject)
import org.json.JSONObject; 

public class RestUtils {
	
	public static final String IBM_WS_SSL_CONFIG = "com.ibm.ws.jaxrs.client.ssl.config"; // server.xml
	
	public static String callRest(String endpoint, String operation) {
		ClientBuilder cb = ClientBuilder.newBuilder();
		cb.property(IBM_WS_SSL_CONFIG, "mySSLRefId");
			
		Client client = cb.build();		
		
		String response = client.target(endpoint + operation)
		        .request(MediaType.APPLICATION_JSON)		        
		        .get(String.class);	
		
		return response;
	}	
	
	public static String callRestGet(String endpoint, String operation, String token) {
		ClientBuilder cb = ClientBuilder.newBuilder();
		cb.property(IBM_WS_SSL_CONFIG, "mySSLRefId");
			
		Client client = cb.build();		
		
		String response = client.target(endpoint + operation)
		        .request(MediaType.APPLICATION_JSON)		
		        .header("Authorization", token)
		        .get(String.class);	
		
		return response;
	}
		
	public static String callRestPost(String endpoint, String operation, String token, String[][]formValues) {
		String jsonResponse="";
		ClientBuilder cb = ClientBuilder.newBuilder();
		cb.property(IBM_WS_SSL_CONFIG, "mySSLRefId");
			
		Client client = cb.build();	
		
		JSONObject jsonObject = new JSONObject();
		for (String[] valuePair : formValues) {			
			jsonObject.put(valuePair[0], valuePair[1]);	
		}
		
		Response httpResponse = client.target(endpoint + operation)
				.request(MediaType.APPLICATION_JSON)				
				.header("Authorization", token)
				.header("Host", "franca.pro.br")
				.post(Entity.json(jsonObject.toString()), Response.class);				
		
		jsonResponse = httpResponse.readEntity(String.class);
		if (httpResponse.getStatus() < 200 || httpResponse.getStatus() > 299) 		{	
			LogUtils.logWarning("callRestPost", "Not ok... Return Code: " + httpResponse.getStatus() +  " - Response: " + jsonResponse);
			return "";
		} else {
			LogUtils.logInformation("callRestPost", "Ok... Return Code: " + httpResponse.getStatus());// +  " - Response: " + jsonResponse);					
			return jsonResponse;
		}	
	}	
	
	/*
	public static String callRest(String endpoint, String operation, String username, String password) {
		HttpAuthenticationFeature basic = HttpAuthenticationFeature.basic(username, password);
		
		ClientBuilder cb = ClientBuilder.newBuilder().register(basic);
		cb.property("com.ibm.ws.jaxrs.client.ssl.config", "mySSLRefId");
			
		Client client = cb.build();		
		
		String response = client.target(endpoint + operation)
		        .request(MediaType.APPLICATION_JSON)		        
		        .get(String.class);	
		
		return response;
	}
	*/
	
	/*
	public static String callRest(String endpoint, String operation, String username, String password, MultivaluedMap<String, Object> headers) {
		HttpAuthenticationFeature basic = HttpAuthenticationFeature.basic(username, password);
		
		ClientBuilder cb = ClientBuilder.newBuilder().register(basic);
		cb.property("com.ibm.ws.jaxrs.client.ssl.config", "mySSLRefId");			
		Client client = cb.build();		
				
		String response = client
			.target(endpoint + operation)
		    .request(MediaType.APPLICATION_JSON)
		    .headers(headers)		    
		    .get(String.class);	
		
		return response;
	}
	*/
		
	public static String callRest(String endpoint, String operation, String username, String password, MultivaluedMap<String, Object> headers, Form body) {
		HttpAuthenticationFeature basic = HttpAuthenticationFeature.basic(username, password);
		
		ClientBuilder cb = ClientBuilder.newBuilder().register(basic);
		cb.property(IBM_WS_SSL_CONFIG, "mySSLRefId");			
		Client client = cb.build();		
				
		String response = client
			.target(endpoint + operation)
		    .request(MediaType.APPLICATION_JSON)
		    .headers(headers)
		    
		    .post(
		    	Entity.entity(body, MediaType.APPLICATION_FORM_URLENCODED_TYPE),
		    	String.class);	
		
		return response;
	}
		
	public static String callRestDelete(String endpoint, String operation, String token) {
		String jsonResponse="";
		ClientBuilder cb = ClientBuilder.newBuilder();
		cb.property(IBM_WS_SSL_CONFIG, "mySSLRefId");
			
		Client client = cb.build();	
		
		Response httpResponse = client.target(endpoint + operation)
				.request(MediaType.APPLICATION_JSON)				
				.header("Authorization", token)				
				.header("Host", "franca.pro.br")
				.delete(Response.class);				
		
		jsonResponse = httpResponse.readEntity(String.class);
		if (httpResponse.getStatus() < 200 || httpResponse.getStatus() > 299) 		{	
			LogUtils.logWarning("callRestDelete", "Not ok... Return Code: " + httpResponse.getStatus()  +  " - Response: " + jsonResponse);
			return "";
		} else {
			LogUtils.logInformation("callRestDelete", "Ok... Return Code: " + httpResponse.getStatus());					
			return jsonResponse;
		}	
	}
	
	/*
	public static String callRestGet(String endpointOperation, String token, Map<String, String> queryStrings) {
		LogUtils.logTrace("New method (2)...");
		ClientBuilder cb = ClientBuilder.newBuilder();
		cb.property("com.ibm.ws.jaxrs.client.ssl.config", "mySSLRefId");
		
		//WebTarget target = client.target("http://commerce.com/customers/{id}")
        //	.resolveTemplate("id", "123")
        //  .queryParam("verbose", true);
		 		
		Client client = cb.build();		
		WebTarget target = client.target(endpointOperation);
				
		for (String key: queryStrings.keySet()) {		
			String value = queryStrings.get(key);
			target = target.queryParam(key, value);  //It is important to know queryParam method won't update current WebTarget object, but return a new one.				
		}		
		
		Builder invocation = target.request().accept(MediaType.APPLICATION_JSON).header("Authorization", token);
		String response = invocation.get(String.class);	
				
		return response;
	}
	*/	
	
	// -------------------------- APACHE HTTP CLIENT --------------------------------------------
	// https://hc.apache.org/httpcomponents-client-ga/index.html
	public static String callRestGet(String endpointOperation) {
		String methodName = "callRestGetApache";
		String json = "{ }";
		
		try {
			//white spaces
			endpointOperation = endpointOperation.replace(" ", "%20");
			
			//HttpClientBuilder cb = HttpClientBuilder.create();
			//cb.property("com.ibm.ws.jaxrs.client.ssl.config", "mySSLRefId");
			
			CloseableHttpClient client = HttpClients.createDefault();
			//CloseableHttpClient client = cb.build(); 
			
			LogUtils.logInformation("callRestGetApache", "Calling endpoint: " + endpointOperation);		
			HttpGet request = new HttpGet(endpointOperation);

			//Set the API media type in http accept header
	        request.addHeader("accept", "application/json");
			
			CloseableHttpResponse response = client.execute(request);		
			LogUtils.logTrace(response.getStatusLine().toString());
			
			HttpEntity body = response.getEntity();			
			json = EntityUtils.toString(body);
			
			response.close();
			client.close();			
		}
		catch (Exception ex) {
			json = "{ \"method\" : \"" + methodName + "\" , ";
			json += "\"error\" : \"" + ex.getMessage() + "\" }";
			LogUtils.logError(methodName, ex.getMessage());
		}
		//finally {}
				
		return json;
	}
	
	// https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/authentication.html
	public static String callRestGetBasicAuth(String endpointOperation, String username, String password) {
		String methodName = "callRestGetBasicAuth";
		String json = "{ }";
		
		try {
			//white spaces
			endpointOperation = endpointOperation.replace(" ", "%20");
			
			// host
			URI uri = new URI(endpointOperation);
		    String domain = uri.getHost();
		    //LogUtils.logTrace("Domain: " + domain);
			
			/* Basic:  Basic authentication scheme as defined in RFC 2617. This authentication scheme is insecure, as the credentials are transmitted in clear text. 
			Despite its insecurity Basic authentication scheme is perfectly adequate if used in combination with the TLS/SSL encryption.*/
		    UsernamePasswordCredentials serviceCredentials = new UsernamePasswordCredentials(username, password);
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();		    
			credentialsProvider.setCredentials(
					new AuthScope(domain, 443), // assume https
		            serviceCredentials);
		        			
			//CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpClient client = HttpClients.custom()
	                .setDefaultCredentialsProvider(credentialsProvider)
	                .build();
			
			LogUtils.logInformation("callRestGetApache", "Calling endpoint: " + endpointOperation);		
			HttpGet request = new HttpGet(endpointOperation);

			//Set the API media type in http accept header
	        request.addHeader("accept", "application/json");
			
			CloseableHttpResponse response = client.execute(request);		
			LogUtils.logTrace(response.getStatusLine().toString());
			
			HttpEntity body = response.getEntity();			
			json = EntityUtils.toString(body);
			
			response.close();
			client.close();			
		}
		catch (Exception ex) {
			json = "{ \"method\" : \"" + methodName + "\" , ";
			json += "\"error\" : \"" + ex.getMessage() + "\" }";
			LogUtils.logError(methodName, ex.getMessage());
		}
		//finally {}
				
		return json;
	}
	
	/*
	public static String callRestGet(String endpointOperation, String token) {
		String methodName = "callRestGetApache";
		String json = "{ }";
		
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			
			LogUtils.logInformation("callRestGetApache", "Calling endpoint: " + endpointOperation);		
			HttpGet request = new HttpGet(endpointOperation);

			//Set the API media type in http accept header
	        request.addHeader("accept", "application/json");	        
			request.addHeader("Authorization", token);			
			
			CloseableHttpResponse response = client.execute(request);		
			LogUtils.logTrace(response.getStatusLine().toString());
			
			HttpEntity body = response.getEntity();			
			json = EntityUtils.toString(body);
			
			response.close();
			client.close();			
		}
		catch (Exception ex) {
			json = "{ \"method\" : \"" + methodName + "\" , ";
			json += "\"error\" : \"" + ex.getMessage() + "\" }";
			LogUtils.logError(methodName, ex.getMessage());
		}
		//finally {}
				
		return json;
	}
	*/
}
