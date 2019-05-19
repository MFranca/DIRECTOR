package test.couchDb;

import cloudant.dao.ServiceNoSqlDao;
import test.AbstractTest;

public class ServicesTest extends AbstractTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServiceNoSqlDao	dao = new ServiceNoSqlDao("bluemix");
		System.out.println(dao.getServicesActiveDays("d14f3880-6a1d-4c41-806d-6f7c0769e0e8"));		
	}
}
