package perspectives.semantical;

import ibmWatson.NLC;
import perspectives.AbstractAnalysis;
import utils.LogUtils;

public class CognitiveAnalysis extends AbstractAnalysis {

	public String perform( 
			String classifierId, 
			String text) throws Exception {

		String methodName = "perform";
		String source = "webApplication";
		String result = "";
		
		try {	
			start(methodName, source);
			
			NLC watson = new NLC();
			
			result = watson.classify(classifierId, text);			
			
		} catch (Exception ex) {			
			LogUtils.logError(methodName, ex.getMessage());
			throw ex;
			
		} finally {
			end(methodName);	
		}	
		
		return result;
	}
	
		
}