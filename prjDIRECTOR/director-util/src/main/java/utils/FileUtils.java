package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.opencsv.CSVWriter;

public class FileUtils {
	//https://www.geeksforgeeks.org/writing-a-csv-file-in-java-using-opencsv/
	//https://stackoverflow.com/questions/10136343/opencsv-csvwriter-using-utf-8-doesnt-seem-to-work-for-multiple-languages
		
	//private OutputStreamWriter file;
	private CSVWriter writer;
	
	public OutputStreamWriter createFile (String filePath) {
		String methodName = "createFile";
		File file;
		OutputStreamWriter outputfile = null;
		
		try { 
			// first create file object for file placed at location 
			// specified by filepath 
			file = new File(filePath);			
			LogUtils.logDebug(methodName, "File path: " + file.getPath());
	    
	        // create FileWriter object with file as parameter			
	        //outputfile = new FileWriter(file, false); // overwrite mode
	        //https://stackoverflow.com/questions/9852978/write-a-file-in-utf-8-using-filewriter-java
	        outputfile = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
	        //this.file = outputfile;
	        
		} catch (Exception ex) { 
			LogUtils.logError(methodName, ex.getMessage());
		} 
	        
	    return outputfile;
	}
	
	public CSVWriter createCSVFile (String filePath) {
		String methodName = "createCSVFile";
		CSVWriter writer = null;
		
		try { 
			OutputStreamWriter outputfile = this.createFile(filePath);
			
			// create CSVWriter object filewriter object as parameter 
	        writer = new CSVWriter(outputfile);
	        this.writer = writer;
	        
		} catch (Exception ex) { 
			LogUtils.logError(methodName, ex.getMessage());
		} 
		
		return writer;
	}
	
	public void writeCSV (String[] csvLine) {
		String methodName = "writeCSV";
		//LogUtils.logTrace("Line to be written: " + Arrays.toString(csvLine));
		
		try { 
			if (this.writer == null) {
				LogUtils.logWarning(methodName, "Writer not ready!");
				return;
			}
			
	        // add data to csv 
	        //String[] data1 = { "Aman", "10", "620" }; 
	        this.writer.writeNext(csvLine);	        
	        
		} catch (Exception ex) { 
			//ex.printStackTrace();
			LogUtils.logError(methodName, ex.getMessage());
		}
	}
	
	public void closeFile() {
		String methodName = "closeFile";
				
		try { 
			// closing writer connection 
	        this.writer.close(); 
	        
	        //LogUtils.logTrace("Flush and close the file stream...");
	        //this.file.flush();
	        //this.file.close();
	        
		} catch (Exception ex) { 
			LogUtils.logError(methodName, ex.getMessage());
		}
	}	
}
