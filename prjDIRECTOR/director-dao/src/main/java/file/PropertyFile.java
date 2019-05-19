package file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFile {
	// src/main/resources
	public static final String DEFAULT_PROPERTY_FILE = "cloud-foundry.properties";
	public static final String STACK_EXCHANGE_PROPERTY_FILE = "stack-exchange.properties";
	public static final String IBM_WATSON_PROPERTY_FILE = "ibm-watson.properties";
	
	private String resourceName;
	private Properties properties = new Properties();
	private InputStream resourceStream = null;	
	
	public PropertyFile () {
		resourceName = DEFAULT_PROPERTY_FILE;
		setupDirectory();
	}
	
	public PropertyFile (String fileName) {
		this.resourceName = fileName;
		setupDirectory();
	}
	
	private void setupDirectory() {
		if (resourceStream == null) {
			try {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				resourceStream = loader.getResourceAsStream(resourceName);
				// load a properties file
				properties.load(resourceStream);
				
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}		
		}
	}
	
	public String getValue(String key) {
		String value = "";
		
		try {			
			
			// get the property value and print it out
			value=properties.getProperty(key);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
		
		return value;
	}
	
	public void dispose(){
		if (resourceStream != null)
			try {
				resourceStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
