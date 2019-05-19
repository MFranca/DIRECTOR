package test.couchDb;

import cloudant.dao.ServiceNoSqlDao;
import cloudant.dao.ServicePlanNoSqlDao;

public class CountTest {

	public static void main(String[] args) {
		String dbName = "bluemix";
		
		/*
		SummaryNoSqlDao summaryDesign = new SummaryNoSqlDao(dbName);		
		System.out.println(String.format("Estamos trabalhando há %d dias...", summaryDesign.getWorkingDays()));
			
		System.out.println("serviços ativos hoje: ");
		ServiceNoSqlDao serviceDao = new ServiceNoSqlDao(dbName);
		System.out.println(serviceDao.getServicesQuantity());
		*/
		
		
		
		//String view = "commercial-plan-quantity-count";
		/*function (doc) {
  			if (doc.doctype == "SERVICE_PLAN" &&  doc.source == "workloadScheduler")
    		emit([doc.timestamp, doc.serviceGuid], 1);
		}*/
		
		ServiceNoSqlDao dao = new ServiceNoSqlDao(dbName);
		
		String service = "038b44f7-5fe3-43b8-90cc-759422db9b65";//"0299549f-8e09-44fc-9d7f-94d771d13122";
		System.out.println("Quantidade de dias ativos: ");		
		System.out.println(dao.getServicesActiveDays(service));
		
		//"serviceGuid": "0299549f-8e09-44fc-9d7f-94d771d13122",
		// "timestamp": "20180730"
		
		/*
		String service = "038b44f7-5fe3-43b8-90cc-759422db9b65";//"0299549f-8e09-44fc-9d7f-94d771d13122";
		System.out.println("Quantidade de planos comerciais: ");		
		System.out.println(dao.getServicePlansQuantity(service));
		
		//dbName = "pivotal";
		//dao = new ServicePlanNoSqlDao(dbName);
		
		System.out.println("Quantidade de service plans hoje..."); //311
		System.out.println(dao.getServicePlansQuantityOnDate(ServicePlanNoSqlDao.getTimestampForToday()));
		*/		
	}
}