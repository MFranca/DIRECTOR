package utils;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

	public static String toUnixEpochTime (Date date) {
		/*
		 * All dates in the API are in unix epoch time, which is the number of seconds since midnight UTC January 1st, 1970. 
		 * The API does not accept or return fractional times, everything should be rounded to the nearest whole second.
		 * The fromdate and todate parameters are accepted by many methods, and are always dates. min and max parameters may accept dates, 
		 * depending on the sort used.
		 *
		 *  https://stackoverflow.com/questions/7784421/getting-unix-timestamp-from-date
		 *  getTime() retrieves the milliseconds since Jan 1, 1970 passed to the constructor.
		 *  It should not be too hard to get the Unix time (same, but in seconds) from that.
		 *  */
		
		return Long.toString(date.getTime() / 1000);
	}
	
	public static String toUnixEpochTime (Calendar date) {
		
		return Long.toString(date.getTimeInMillis() / 1000);
	}
}
