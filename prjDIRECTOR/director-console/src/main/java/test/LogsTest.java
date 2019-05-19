package test;

import utils.LogUtils;

public class LogsTest {

	public static void main(String[] args) {
		LogUtils.logTrace("Entering application...");
		LogUtils.logDebug("debugging", "Entering application...");
		
		LogUtils.logInformation("main", "A information...");
		LogUtils.logInformation(new LogsTest(), "main", "A information...");
		
		LogUtils.logWarning("main", "A Warning...");
				
		LogUtils.logError("erro", "An error in the application...");
	}
}