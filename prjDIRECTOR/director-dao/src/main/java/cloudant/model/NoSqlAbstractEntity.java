package cloudant.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDate; // Java 8
import java.time.format.DateTimeFormatter; // Java 8

import org.json.JSONException;
import org.json.JSONObject;

public abstract class NoSqlAbstractEntity implements Serializable {
	private static final long serialVersionUID = 0L;

	private String doctype;
	private long docversion;

	private String source; // manual, test, scheduler
	private String timestamp;

	// ----- Default Constructor -----
	public NoSqlAbstractEntity() {
		this.setDoctype("unknown");
		this.setDocversion(NoSqlAbstractEntity.serialVersionUID);
		this.setSource("unknown"); // manual, test, scheduler
		this.setTimestampFromDate(LocalDate.now());
	}

	protected void populateAttributes(final Object instance, final JSONObject jsonData)
			throws IllegalArgumentException, IllegalAccessException, JSONException {
		Class<?> c = instance.getClass();
		for (Field f : c.getDeclaredFields()) {
			f.setAccessible(true);
			String attributeName = f.getName();

			if (jsonData.has(attributeName) && !jsonData.isNull(attributeName)) {
				// sets the value of the attribute from the JSON

				if (f.getType() == int.class)
					f.set(instance, jsonData.getInt(attributeName));

				else if (f.getType() == long.class)
					f.set(instance, jsonData.getLong(attributeName));

				else if (f.getType() == String.class)
					f.set(instance, jsonData.getString(attributeName));

			}
		}
	}

	public LocalDate getTimestampAsDate() {
		// convert String to LocalDate
		LocalDate localDate = LocalDate.parse(this.timestamp, DateTimeFormatter.BASIC_ISO_DATE);

		return localDate;
	}

	public void setTimestampFromDate(LocalDate timestamp) {
		// https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
		// https://dotnetcodr.com/2015/01/11/formatting-dates-in-java-8-using-datetimeformatter/
		// https://dzone.com/articles/deeper-look-java-8-date-and
		String dateAsString = timestamp.format(DateTimeFormatter.BASIC_ISO_DATE); // BASIC_ISO_DATE
																					// Basic
																					// ISO
																					// date
																					// '20111203'

		this.timestamp = dateAsString;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public long getDocversion() {
		return docversion;
	}

	public void setDocversion(long docversion) {
		this.docversion = docversion;
	}
}
