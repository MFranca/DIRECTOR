package services;

import java.time.LocalDate;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/test")
public class TestResource extends AbstractResource {

	
	@Override
	public String setMetadata(String source) {
		return null;
	}

	@Override
	public String synchronize(String source) {
		return null;
	}
	
	@Override
	public String getInformation(String source) {
		return "This is just a dummy service...";
	}	

	// ********************
	// *** TEST SERVICE ***
	// ********************
	// http://localhost:9080/director-services/api/info/test
	// http://localhost:9080/director-services/api/info/test?source=justAnotherTest
	// https://director-services.mybluemix.net/api/info/test?source=justAnotherTest
	@GET
	@Produces("application/json")
	@Path("/test")
	public String getTestInformation(
			@DefaultValue("unknown") 
			@QueryParam("source") 
			String source) {
		
		String methodName = "getTestInformation";
		String jsonResponse;
		
		try {			
			start(methodName, source);
					
			// http://docs.oracle.com/javaee/7/api/javax/json/JsonObject.html
			// https://dzone.com/articles/java-api-json-processing-%E2%80%93
			// https://stackoverflow.com/questions/23871265/how-to-add-jsonobjects-to-javax-json-jsonarray-using-loop-dynamically
				
			JsonObject jsonTest =
					Json.createObjectBuilder() // implicit root
						.add("source", source)
						.add("message", "DIRECTOR is up and running...")
						.add("timestamp", LocalDate.now().toString()
						)
                  	.build();			
			
			JsonObject response =
				    Json.createObjectBuilder()
				        .add("information", Json.createObjectBuilder() // root
				        		.add("endpoint", "/test")
				                .add("test", jsonTest) 
				        		)
				        .build();
			
			jsonResponse =response.toString(); 

		} catch (Exception ex) {
			
			JsonObject response =  error(ex, this.getClass().getName(), methodName);
			jsonResponse =response.toString();
			
		} finally {
			end(methodName);			
		}
		
		return jsonResponse;
	}	
	
	// ********************
	// *** TEST SERVICE ***
	// ********************
	// http://localhost:9080/director-services/api/info/test2?source=postman
	@GET
	@Produces("application/json")
	@Path("/test2")
	public String getTestInformationForLoop(
			@DefaultValue("unknown") 
			@QueryParam("source") 
			String source) {
		
		String methodName = "getTestInformationForLoop";
		String jsonResponse;
		
		try {			
			start(methodName, source);
					
			// http://docs.oracle.com/javaee/7/api/javax/json/JsonObject.html
			// https://dzone.com/articles/java-api-json-processing-%E2%80%93		
			// https://stackoverflow.com/questions/23871265/how-to-add-jsonobjects-to-javax-json-jsonarray-using-loop-dynamically
			JsonArrayBuilder builder = Json.createArrayBuilder();						
			JsonObject jsonTest;
			JsonObject response;
			
			jsonTest = Json.createObjectBuilder() // implicit root
					.add("source", source)
					.add("message", "DIRECTOR is up and running...")
					.add("timestamp", LocalDate.now().toString()
					)
				.build();		
			
			for (int i = 0; i<10; i++) {
				JsonObject jsonI =
						Json.createObjectBuilder() // implicit root
							.add("i", i)							
	                  	.build();		
				
				 builder.add(jsonI);
			}
					
			response = Json.createObjectBuilder()
					  .add("information", Json.createObjectBuilder() // root
							  .add("endpoint", "/test2")
				              .add("test", jsonTest) 
							  .add("array", builder.build()) // array of objects
							  )
							  .build(); 		
			
			jsonResponse =response.toString(); 

		} catch (Exception ex) {
			
			JsonObject response =  error(ex, this.getClass().getName(), methodName);
			jsonResponse =response.toString();
			
		} finally {
			end(methodName);			
		}
		
		return jsonResponse;
	}
}