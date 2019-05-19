package services.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public interface CFService {
	// healthcheck
	@Path("/healthcheck")
	public boolean isAlive(@DefaultValue("unknown") @QueryParam("source") String source);
	
	// retrieve the metadata from the PaaS and save it on the NoSql database...
	@Path("/save")
	public String setMetadata(@DefaultValue("unknown") @QueryParam("source") String source);
	
	// it is time to synchronize the NoSql and the RDMS (retreive the metadata from the NoSql and save it on the Relational database)...
	@Path("/update")
	public String synchronize(@DefaultValue("unknown") @QueryParam("source") String source);
		
	// get information from the RDMS...
	@Path("/info")
	public String getInformation(@DefaultValue("unknown") @QueryParam("source") String source);		
}