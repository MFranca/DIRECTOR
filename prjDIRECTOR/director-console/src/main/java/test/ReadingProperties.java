package test;

import file.PropertyFile;

public class ReadingProperties {

	public static void main(String[] args) {
		PropertyFile configuration = new PropertyFile();
		
		System.out.println(configuration.getValue("info"));
	}

}
