package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import application.services.ServiceResource;

public class LogUtils { //TODO: tornar generic
			
	private static final Logger log = LogManager.getLogger(LogUtils.class.getName());	
	private static final Logger dbLog = LogManager.getLogger("db");
	//private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LogUtils.class.getName());
	
	/*
	public static Logger getL4jLogger() {
		return LogUtils.log4jLogger;
	}
	*/
	
	public static void logTrace(String message) {
		LogManager.getRootLogger().trace(message);
	}
	
	public static void logDebug(String method, String message) {
		log.debug("[" + method + "] " + message);
	}
	
	public static void logInformation(String method, String message) {
		//System.out.println("[" + method + "] " + message);
		//logger.log(java.util.logging.Level.INFO, "[" + method + "] " + message);
		doLog4jInformation(method, message);
	}
	
	public static void logInformation(Object object, String method, String message) {
		method = object.getClass().toString() + "." + method;
		//System.out.println("[" + method + "] " + message);
		//logger.log(java.util.logging.Level.INFO, "[" + method + "] " + message);		
		doLog4jInformation(method, message);
	}
	
	public static void logWarning(String method, String message) {
		//logger.log(java.util.logging.Level.WARNING, "[" + method + "] " + message);
		doLog4jWarning(method, message);
	}
	
	public static void logError(String method, String message) {
		//logger.log(java.util.logging.Level.WARNING, "[" + method + "] " + message);
		doLog4jError(method, message);
	}
		
	private static void doLog4jInformation(String method, String message) {
		dbLog.info("[" + method + "] " + message);
		//dbLogger.info("[" + method + "] " + message);
	}
	
	private static void doLog4jWarning(String method, String message) {
		//log.warn("[" + method + "] " + message);
		dbLog.warn("[" + method + "] " + message);
	}
	
	private static void doLog4jError(String method, String message) {
		//log.error("[" + method + "] " + message);
		dbLog.error("[" + method + "] " + message);
	}
}