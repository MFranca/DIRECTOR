package test.phddb_bkp;

import java.util.List;

import com.google.gson.JsonObject;

import cloudant.NoSqlDao;
import test.AbstractTest;

public class MoveToBluemix extends AbstractTest {

	public static final String ALL_SERVICE_SUMMARY_FROM_SCHEDULER_VIEW = "document/all-service-summary-from-workload-scheduler";
	
	public static void main(String[] args) {
		//start();
		
		//List<Document> documents = NoSqlDao.getAllDocumentsFromScheduler("phddb_bkp");
		List<JsonObject> documents = NoSqlDao.getAllJsonDocumentsFromView("phddb_bkp", ALL_SERVICE_SUMMARY_FROM_SCHEDULER_VIEW);

		//Total de documentos: 60922
		//org.lightcouch.Document@4d477b5f

		int registros = documents.size();
		System.out.println("Total de documentos: " + registros);
		//System.out.println("getAsString: " + documents.get(0).getAsString());
		//System.out.println("toString: " + documents.get(0).toString());		
		//System.out.println(documents.get(0));
		/*Total de documentos: 60922
		toString: {"_id":"00964bb3f9be4e84b523f78360b62e88","_rev":"1-60b9a8c2e82416b7c22f19ab7710b17b","guid":"c997d729-21cb-4669-9410-f77cef3ee3f4","url":"/v2/services/c997d729-21cb-4669-9410-f77cef3ee3f4","createdAt":"2017-05-17T16:48:27Z","label":"fss-predictive-scenario-analytics-service","active":true,"tags":["ibm_experimental","finance","ibm_created"],"entity":"{\"unique_id\":\"C43B3EB0-F56B-4753-92E4-093815814C1E\",\"description\":\"Create conditional scenarios to model how, given a change to a subset of factors the broader set of market factors are expected to change.\",\"active\":true,\"service_plans_url\":\"/v2/services/c997d729-21cb-4669-9410-f77cef3ee3f4/service_plans\",\"label\":\"fss-predictive-scenario-analytics-service\",\"long_description\":null,\"service_broker_guid\":\"f244a7c0-9ad0-492e-b9c1-ddb6cb921fdd\",\"documentation_url\":null,\"version\":null,\"url\":null,\"tags\":[\"ibm_experimental\",\"finance\",\"ibm_created\"],\"bindable\":true,\"plan_updateable\":false,\"provider\":null,\"extra\":\"{\\\"displayName\\\":\\\"Predictive Market Scenarios\\\",\\\"longDescription\\\":\\\"Thirty years of financial engineering expertise at your fingertips. IBM Algorithmics pricing models are trusted by some of the world's largest financial institutions to meet their risk, performance, and regulatory needs.\\\\n\\\\n    Generate what-if financial market scenarios for use in the valuation of financial securities. Predictive market scenarios allows users to understand how a broader set of market factors might change if a small subset of factors undergo the user defined change. An example is what-if oil moved up 5%, how would the equity markets, rates, credit change.\\\\n\\\\n    The scenario can then be applied to an investment portfolio to understand how it might react.\\\",\\\"providerDisplayName\\\":\\\"IBM\\\",\\\"locationDisplayName\\\":\\\"US South\\\",\\\"serviceMonitorApi\\\":\\\"https://fss-analytics.mybluemix.net/health/fss-predictive-scenario-analytics-service\\\",\\\"embeddableDashboard\\\":true,\\\"embeddableDashboardFullWidth\\\":true,\\\"documentationUrl\\\":\\\"https://console.ng.bluemix.net/docs/services/PredictiveMarketScenarios/index.html\\\",\\\"featuredImageUrl\\\":\\\"https://fss-analytics.mybluemix.net/public/img/64/predictive_package.png?version=4\\\",\\\"imageUrl\\\":\\\"https://fss-analytics.mybluemix.net/public/img/50/predictive_package.png?version=4\\\",\\\"instructionsUrl\\\":\\\"/services/PredictiveMarketScenarios/index.html\\\",\\\"mediumImageUrl\\\":\\\"https://fss-analytics.mybluemix.net/public/img/32/predictive_package.png?version=4\\\",\\\"serviceKeysSupported\\\":true,\\\"smallImageUrl\\\":\\\"https://fss-analytics.mybluemix.net/public/img/24/predictive_package.png?version=4\\\",\\\"bullets\\\":[{\\\"title\\\":\\\"What-If Scenarios\\\",\\\"description\\\":\\\"Generate 'what-if' financial market scenarios for use in the valuation of financial securities.\\\"},{\\\"title\\\":\\\"Market Standard Stress Testing\\\",\\\"description\\\":\\\"Industry accepted stress testing approach, used by large financial institutions.\\\"},{\\\"title\\\":\\\"Macroeconomic Projections\\\",\\\"description\\\":\\\"See how small, isolated movements can have an amplified effect across projected macroeconomic conditions.\\\"}],\\\"apiReferenceUrl\\\":\\\"764\\\",\\\"termsUrl\\\":\\\"https://www.ibm.com/software/sla/sladb.nsf/sla/bm-6620-02\\\",\\\"plansOrder\\\":\\\"4FEF1740-10DD-450C-8490-3A0E55742DD2\\\"}\",\"info_url\":null,\"requires\":[]}","doctype":"SERVICE","docversion":1,"source":"ibmWorkloadScheduler","timestamp":"20170622"}
		{"_id":"00964bb3f9be4e84b523f78360b62e88","_rev":"1-60b9a8c2e82416b7c22f19ab7710b17b","guid":"c997d729-21cb-4669-9410-f77cef3ee3f4","url":"/v2/services/c997d729-21cb-4669-9410-f77cef3ee3f4","createdAt":"2017-05-17T16:48:27Z","label":"fss-predictive-scenario-analytics-service","active":true,"tags":["ibm_experimental","finance","ibm_created"],"entity":"{\"unique_id\":\"C43B3EB0-F56B-4753-92E4-093815814C1E\",\"description\":\"Create conditional scenarios to model how, given a change to a subset of factors the broader set of market factors are expected to change.\",\"active\":true,\"service_plans_url\":\"/v2/services/c997d729-21cb-4669-9410-f77cef3ee3f4/service_plans\",\"label\":\"fss-predictive-scenario-analytics-service\",\"long_description\":null,\"service_broker_guid\":\"f244a7c0-9ad0-492e-b9c1-ddb6cb921fdd\",\"documentation_url\":null,\"version\":null,\"url\":null,\"tags\":[\"ibm_experimental\",\"finance\",\"ibm_created\"],\"bindable\":true,\"plan_updateable\":false,\"provider\":null,\"extra\":\"{\\\"displayName\\\":\\\"Predictive Market Scenarios\\\",\\\"longDescription\\\":\\\"Thirty years of financial engineering expertise at your fingertips. IBM Algorithmics pricing models are trusted by some of the world's largest financial institutions to meet their risk, performance, and regulatory needs.\\\\n\\\\n    Generate what-if financial market scenarios for use in the valuation of financial securities. Predictive market scenarios allows users to understand how a broader set of market factors might change if a small subset of factors undergo the user defined change. An example is what-if oil moved up 5%, how would the equity markets, rates, credit change.\\\\n\\\\n    The scenario can then be applied to an investment portfolio to understand how it might react.\\\",\\\"providerDisplayName\\\":\\\"IBM\\\",\\\"locationDisplayName\\\":\\\"US South\\\",\\\"serviceMonitorApi\\\":\\\"https://fss-analytics.mybluemix.net/health/fss-predictive-scenario-analytics-service\\\",\\\"embeddableDashboard\\\":true,\\\"embeddableDashboardFullWidth\\\":true,\\\"documentationUrl\\\":\\\"https://console.ng.bluemix.net/docs/services/PredictiveMarketScenarios/index.html\\\",\\\"featuredImageUrl\\\":\\\"https://fss-analytics.mybluemix.net/public/img/64/predictive_package.png?version=4\\\",\\\"imageUrl\\\":\\\"https://fss-analytics.mybluemix.net/public/img/50/predictive_package.png?version=4\\\",\\\"instructionsUrl\\\":\\\"/services/PredictiveMarketScenarios/index.html\\\",\\\"mediumImageUrl\\\":\\\"https://fss-analytics.mybluemix.net/public/img/32/predictive_package.png?version=4\\\",\\\"serviceKeysSupported\\\":true,\\\"smallImageUrl\\\":\\\"https://fss-analytics.mybluemix.net/public/img/24/predictive_package.png?version=4\\\",\\\"bullets\\\":[{\\\"title\\\":\\\"What-If Scenarios\\\",\\\"description\\\":\\\"Generate 'what-if' financial market scenarios for use in the valuation of financial securities.\\\"},{\\\"title\\\":\\\"Market Standard Stress Testing\\\",\\\"description\\\":\\\"Industry accepted stress testing approach, used by large financial institutions.\\\"},{\\\"title\\\":\\\"Macroeconomic Projections\\\",\\\"description\\\":\\\"See how small, isolated movements can have an amplified effect across projected macroeconomic conditions.\\\"}],\\\"apiReferenceUrl\\\":\\\"764\\\",\\\"termsUrl\\\":\\\"https://www.ibm.com/software/sla/sladb.nsf/sla/bm-6620-02\\\",\\\"plansOrder\\\":\\\"4FEF1740-10DD-450C-8490-3A0E55742DD2\\\"}\",\"info_url\":null,\"requires\":[]}","doctype":"SERVICE","docversion":1,"source":"ibmWorkloadScheduler","timestamp":"20170622"}
		 */
		
		for(JsonObject document : documents) {
			registros--;
			document.remove("_rev");
			document.remove("_id");
						
			//document.remove("source");
			document.addProperty("source", "workloadScheduler");
			
			//System.out.println("Salvo documento com id: " + NoSqlDao.saveJsonDocument("bluemix", document));
			NoSqlDao.saveJsonDocument("bluemix", document);
			System.out.println("Faltam: " + registros + " registros.");
		}
		
		
		System.out.println("Finalizado...");
		//end();
	}

	

	
	
}
