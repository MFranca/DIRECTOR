package watson;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.watson.developer_cloud.http.Headers;
import com.ibm.watson.developer_cloud.http.HttpHeaders;
import com.ibm.watson.developer_cloud.http.Response;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.ClassifierList;
import com.ibm.watson.developer_cloud.service.exception.NotFoundException;
import com.ibm.watson.developer_cloud.service.exception.RequestTooLargeException;
import com.ibm.watson.developer_cloud.service.exception.ServiceResponseException;

import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import mysqlDB.model.ServiceTag;
import utils.FileUtils;
import utils.LogUtils;

public class WatsonNLC {
	// https://console.bluemix.net/docs/services/natural-language-classifier/using-your-data.html#data-preparation
	
	public static final String TRAINING_DATA_FILE_PATH = "src/main/resources/<filename>";
	private int numberOfTrainingRecords = 0;
	
	// https://console.bluemix.net/services/natural-language-classifier/fa3307f0-ff98-4603-b161-9930ec689278?paneId=credentials
	private static final String NLC_ENDPOINT = "https://gateway.watsonplatform.net/natural-language-classifier/api";
	private static final String NLC_USERNAME = "5e012be3-a193-4961-821d-f6a2f65fefd1";
	private static final String NLC_PASSWORD= "IexJNRhKHwlA";
		
	private static final String CLASSIFIER_NAME = "director_nlc";
	private NaturalLanguageClassifier ibmWatson;
	
	// https://console.bluemix.net/docs/services/natural-language-classifier/using-your-data.html#data-preparation
	public void prepareAndSaveServicesData () {
		String methodName = "saveServicesData";
		String recordClass;
		String filename;
		String today;
		List<Platform> platforms = Platform.findActive();
		
		today = new SimpleDateFormat("yyyyMMdd").format(new Date());
		filename = "service_data_" + today + ".csv";
		filename = TRAINING_DATA_FILE_PATH.replaceAll("<filename>", filename);
		
		FileUtils filePreparation = new FileUtils();
		LogUtils.logInformation(methodName, "Creating file " + filename);
		filePreparation.createCSVFile(filename);
		
		for (Platform p : platforms) {
			LogUtils.logTrace("Platform: " + p.getName());
			List<Service> services = p.findActiveServices();
			
			for (Service s : services) {
				recordClass = s.getId() + "_" + s.getName();				
				LogUtils.logTrace("Service: " + recordClass);
				
				// description
				writeLine(filePreparation, s.getDescription(), recordClass);				
				// long description
				writeLine(filePreparation, s.getLongDescription(), recordClass);				
				
				// tags
				for (ServiceTag t : s.getTags()) 
					writeLine(filePreparation, t.getName(), recordClass);
			}
		}
		
		filePreparation.closeFile();
		LogUtils.logInformation(methodName, "Number of training data records: " + this.numberOfTrainingRecords);
	}
	
	public void createAndTrain (String trainDataFile) {
		String methodName = "createAndTrain";
		
		try {
		    // Invoke a Natural Language Classifier method
			this.ibmWatson = new NaturalLanguageClassifier();
			
			//setup
			this.configure();
					
			// delete all classifiers
			this.deleteClassifier();
						
			this.createClassifier(trainDataFile);
			
		} catch (NotFoundException e) {
		    // Handle Not Found (404) exception
			LogUtils.logError(methodName, "<NotFoundException> Service returned status code " + e.getStatusCode() + ": " + e.getMessage());
		    e.printStackTrace();
		    
		} catch (RequestTooLargeException e) {
		    // Handle Request Too Large (413) exception
			LogUtils.logError(methodName, "<RequestTooLargeException> Service returned status code " + e.getStatusCode() + ": " + e.getMessage());
		    e.printStackTrace();
		    
		} catch (ServiceResponseException e) {
		    // Base class for all exceptions caused by error responses from the service
		    LogUtils.logError(methodName, "<ServiceResponseException> Service returned status code " + e.getStatusCode() + ": " + e.getMessage());
		    e.printStackTrace();
		    
		} catch (Exception other) {
			LogUtils.logError(methodName, other.getMessage());			
			other.printStackTrace();
			
		} finally {
			this.ibmWatson = null;	
		}
		
	}
	
	// private methods
	private void writeLine(FileUtils file, String recordText, String recordClass) {
		if (this.numberOfTrainingRecords == 20000) //The training data must have at least five records (rows) and no more than 20,000 records.
			return;
		
		String[] line = new String[2];
		
		if (recordText == null || recordText.trim().isEmpty()) // 2 classes are incomplete. Each class must have at least 1 text example.
			return;
		
		if (recordText.length()>1024) // The maximum total length of a text value is 1024 characters.
			recordText = recordText.substring(0, 1024);		
		
		if (recordClass.length()>50) // Class name should be shorter than 50 characters.
			recordClass = recordClass.substring(0, 50);		  		
		recordClass = recordClass.replaceAll("[^A-Za-z0-9 ()_-]", "-");
		
		line[0] = recordText;
		line[1] = recordClass;
		file.writeCSV(line);
		this.numberOfTrainingRecords++;
	}
	
	private void configure () {
		ibmWatson.setApiKey("");
		ibmWatson.setEndPoint(NLC_ENDPOINT);
		ibmWatson.setUsernameAndPassword(NLC_USERNAME, NLC_PASSWORD);
		
		// https://www.ibm.com/watson/developercloud/natural-language-classifier/api/v1/java.html?java#additional-headers
		Map<String, String> headers = new HashMap<String, String>();
		headers.putIfAbsent(HttpHeaders.X_WATSON_LEARNING_OPT_OUT, "true"); // "1"
		ibmWatson.setDefaultHeaders(headers);
		// All the api calls from now on will send the default headers
		
		LogUtils.logTrace("Configuration completed.");
	}
	
	private void deleteClassifier() {	
		String methodName = "deleteClassifiersIfExist";
		
		if (this.ibmWatson == null)
			return;
	
		ClassifierList classifierList = ibmWatson.getClassifiers().execute();
		List<Classifier> classifiers = classifierList.getClassifiers();
						
		for (Classifier c: classifiers) {
			LogUtils.logDebug(methodName, "Found a classifier: " + c.getName());
			
			if (c.getName().equalsIgnoreCase(CLASSIFIER_NAME)) {
				LogUtils.logWarning(methodName, "Deleting classifier Id/Name: " + c.getClassifierId() + " / " + c.getName());
				this.ibmWatson.deleteClassifier(c.getClassifierId()).execute();				
			}
		}
	}
	
	private void createClassifier(String filename) throws FileNotFoundException, RuntimeException {
		String methodName = "createClassifier";
		Classifier classifier;
		//Response<Classifier> classifierResponse = null;
		
		final File trainingData = new File(TRAINING_DATA_FILE_PATH.replaceAll("<filename>", filename));		
		final String classifierName = CLASSIFIER_NAME;
		
		LogUtils.logInformation(methodName, "Using training data from file " + trainingData.getPath());		
		
		
		classifier = ibmWatson.createClassifier(classifierName, "en", trainingData).execute();
		//classifierResponse = ibmWatson.createClassifier(classifierName, "en", trainingData).executeWithDetails();
			
		// Access response from methodName
		//classifier = classifierResponse.getResult();
			
		
		// Access information in response headers
		//Headers responseHeaders = classifierResponse.getHeaders();
		//LogUtils.logTrace(responseHeaders.toString());
				
		//service.deleteClassifier("10D41B-nlc-1").execute();
		LogUtils.logInformation(methodName, "Classifier created with id: " + classifier.getClassifierId()); // 122608x455-nlc-2922		
	}
}