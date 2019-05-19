package test;

import mysqlDB.model.PlatformInformation;

public class DependencyTest {

	public static void main(String[] args) {
		System.out.println("Running a test...");
		PlatformInformation dao = new PlatformInformation();
		
		dao.save();
		System.out.println("Ending a test...");
	}

}
