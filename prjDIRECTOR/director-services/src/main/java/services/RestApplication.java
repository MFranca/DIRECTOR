package services;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/*")
public class RestApplication extends Application {
	
	@Override
	public Set<Class<?>> getClasses() {
		return null;
	}
	
}