package test.stackExchange;

import java.util.Calendar;

import stackExchange.SEQuestion;
import test.AbstractTest;

public class StackExchangeInfoTest extends AbstractTest {

	public static void main(String[] args) {
		
		try {						
			//start();
			System.out.println("Iniciou os testes.");
			
			//RestUtils request = new RestUtils();
			/*
			Map<String, String> queryStrings = new HashMap<>();
			queryStrings.put("site", "stackoverflow");
			queryStrings.put("key", "V5Q2Ipm0kibNYJryVTVtqg((");
			*/			
						
			//System.out.println(request.callRestGet("https://jsonplaceholder.typicode.com", "/todos/1", ""));
			//System.out.println(RestUtils.callRestGet("https://jsonplaceholder.typicode.com/todos/1", ""));
			//System.out.println(RestUtils.callRestGet("http://api.stackexchange.com/2.2/info", "", queryStrings));
			
			/*		
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet("http://api.stackexchange.com/2.2/info?site=stackoverflow");
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			
			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();			
			String json = EntityUtils.toString(entity1);
						
			System.out.println(json);
			
			response1.close();
			httpclient.close();
			*/
			
			/*
			String resposta = RestUtils.callRestGet("http://api.stackexchange.com/2.2/info?site=stackoverflow", "");
			System.out.println("Resposta: " + resposta);
			
			resposta = RestUtils.callRestGet("https://api.stackexchange.com/2.2/info?site=stackoverflow&key=V5Q2Ipm0kibNYJryVTVtqg((", "");
			System.out.println("Resposta (2): " + resposta);
			
			resposta = RestUtils.callRestGet("https://api.sta1ckexchange.com/2.2/info?site=stackoverflow", "");
			System.out.println("Resposta (3): " + resposta);
			*/
			
			SEQuestion q = new SEQuestion();
			String resposta = q.getStatistics();
			System.out.println("Resposta: " + resposta);
						
			/*
			StackExchangeApiQueryFactory fabrica = StackExchangeApiQueryFactory
					.newInstance(SEQuestion.APPLICATION_KEY, 
					StackExchangeSite.STACK_OVERFLOW);
			*/
			/*StatisticsApiQuery consulta = fabrica.newStatisticsApiQuery();
						
			System.out.println("App key" + consulta.getApplicationKey());
			System.out.println("Token: " + consulta.getAccessToken());
			System.out.println("Current Rate Limit" + consulta.getCurrentRateLimit());
			System.out.println("Max Rate Limit" + consulta.getMaxRateLimit());			
			System.out.println("API Provider" + consulta.getApiProvider().toString());
			
			PagedList<Statistics> estatisticas = consulta.list();
			System.out.println("paginas: " + estatisticas.size());
			*/
			
			/*
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			cal.add(Calendar.YEAR, -1); // to get previous year add -1
			Date lastYear = cal.getTime();						
			TimePeriod period = new TimePeriod(today, lastYear);
				
			List<Question> hits = fabrica.newQuestionApiQuery()  
					//.withSort(Question.SortOrder.LEAST_HOT)
					//.withPaging(new Paging(1, 10))  
					.withTimePeriod(period)
					.withTitle("cloudant")
					//.withFilter("")
					.withTags("ibm-bluemix", "cloudfoundry")
					.list();
			System.out.println("Hits: " + hits.size());
			*/
			
			Calendar lastYear = Calendar.getInstance();
			lastYear.add(Calendar.YEAR, -1); // to get previous years
			
			//resposta = q.list(lastYear.getTime(), "cloudant");
			//System.out.println("Resposta: " + resposta);
			
			resposta = q.list(lastYear.getTime(), "dashDB");
			System.out.println("Resposta (dash): " + resposta);
			/*{
  "items": [
    {
      "tags": [
        "db2",
        "ibm-cloud",
        "dashdb"
      ],
      "owner": {
        "reputation": 1,
        "user_id": 9864711,
        "user_type": "registered",
        "profile_image": "https://www.gravatar.com/avatar/13ae5bf3f7b53eac63d4290579519e55?s=128&d=identicon&r=PG&f=1",
        "display_name": "behemoth18",
        "link": "https://stackoverflow.com/users/9864711/behemoth18"
      },
      "is_answered": true,
      "view_count": 59,
      "answer_count": 1,
      "score": 0,
      "last_activity_date": 1527750184,
      "creation_date": 1527603237,
      "last_edit_date": 1527750184,
      "question_id": 50586582,
      "link": "https://stackoverflow.com/questions/50586582/accessing-logs-for-dashdb-on-bluemix-cloud",
      "title": "accessing logs for dashdb on bluemix cloud"
    },
    {
      "tags": [
        "db2",
        "ibm-cloud",
        "dashdb",
        "ibm-cloud-tools"
      ],
      "owner": {
        "reputation": 175,
        "user_id": 2504156,
        "user_type": "registered",
        "accept_rate": 70,
        "profile_image": "https://www.gravatar.com/avatar/649d45e23edce65ed1269b1ae7ee8b68?s=128&d=identicon&r=PG",
        "display_name": "Gana",
        "link": "https://stackoverflow.com/users/2504156/gana"
      },
      "is_answered": true,
      "view_count": 88,
      "answer_count": 2,
      "score": 0,
      "last_activity_date": 1524148715,
      "creation_date": 1524104251,
      "question_id": 49911699,
      "link": "https://stackoverflow.com/questions/49911699/backup-of-dashdb-on-bluemix-what-options-available",
      "title": "Backup of Dashdb on Bluemix - what options available"
    },
    {
      "tags": [
        "db2",
        "ibm-cloud",
        "dashdb"
      ],
      "owner": {
        "reputation": 384,
        "user_id": 1000921,
        "user_type": "registered",
        "accept_rate": 50,
        "profile_image": "https://www.gravatar.com/avatar/fb68ebbde827087f41db46c99fcf73af?s=128&d=identicon&r=PG",
        "display_name": "ipbd",
        "link": "https://stackoverflow.com/users/1000921/ipbd"
      },
      "is_answered": false,
      "view_count": 108,
      "answer_count": 0,
      "score": 0,
      "last_activity_date": 1516369152,
      "creation_date": 1516369152,
      "question_id": 48342425,
      "link": "https://stackoverflow.com/questions/48342425/cannot-use-column-names-in-sql-queries-of-dashdb-db2-warehouse-on-cloud",
      "title": "Cannot use column names in SQL queries of DashDB/&quot;Db2 Warehouse on Cloud&quot;"
    },
    {
      "tags": [
        "ibm-cloud",
        "dashdb"
      ],
      "owner": {
        "reputation": 441,
        "user_id": 5319114,
        "user_type": "registered",
        "accept_rate": 64,
        "profile_image": "https://i.stack.imgur.com/6pXta.jpg?s=128&g=1",
        "display_name": "Leo",
        "link": "https://stackoverflow.com/users/5319114/leo"
      },
      "is_answered": false,
      "view_count": 64,
      "answer_count": 1,
      "score": 0,
      "last_activity_date": 1514856328,
      "creation_date": 1514651308,
      "question_id": 48035749,
      "link": "https://stackoverflow.com/questions/48035749/trying-to-create-a-new-table-column-in-dashdb-but-getting-a-timeout-error",
      "title": "Trying to create a new table column in DashDB but getting a timeout error"
    },
    {
      "tags": [
        "db2",
        "ibm-cloud",
        "dashdb"
      ],
      "owner": {
        "reputation": 1679,
        "user_id": 1255620,
        "user_type": "registered",
        "accept_rate": 53,
        "profile_image": "https://www.gravatar.com/avatar/9fb6b27b3da169cd281f75162f3fbed6?s=128&d=identicon&r=PG",
        "display_name": "daniely",
        "link": "https://stackoverflow.com/users/1255620/daniely"
      },
      "is_answered": false,
      "view_count": 68,
      "answer_count": 0,
      "score": 0,
      "last_activity_date": 1510065186,
      "creation_date": 1510005570,
      "last_edit_date": 1510065186,
      "question_id": 47146556,
      "link": "https://stackoverflow.com/questions/47146556/specify-key-columns-when-creating-table-on-dashdb-db2-cloud",
      "title": "Specify key columns when creating table on DashDB/DB2 Cloud"
    },
    {
      "tags": [
        "ibm-cloud",
        "dashdb"
      ],
      "owner": {
        "reputation": 21,
        "user_id": 4775034,
        "user_type": "registered",
        "profile_image": "https://lh3.googleusercontent.com/-VmN8aQBhR5k/AAAAAAAAAAI/AAAAAAAAB6c/hf47xA3IVCQ/photo.jpg?sz=128",
        "display_name": "Pandu Ranga Rao Mutyala",
        "link": "https://stackoverflow.com/users/4775034/pandu-ranga-rao-mutyala"
      },
      "is_answered": true,
      "view_count": 83,
      "answer_count": 1,
      "score": 1,
      "last_activity_date": 1508286506,
      "creation_date": 1507748260,
      "last_edit_date": 1508286506,
      "question_id": 46695648,
      "link": "https://stackoverflow.com/questions/46695648/enabling-automatic-client-reroute-for-availability-when-using-db2oncloudaka-das",
      "title": "Enabling Automatic Client Reroute for Availability when using DB2onCloud(aka dashDB for Transactions) HA plan"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9891
}*/
			
			resposta = q.list(lastYear.getTime(), "mongodb");
			System.out.println("Resposta (mongo): " + resposta);
			
			/*{
  "items": [
    {
      "tags": [
        "mongodb",
        "cloudfoundry",
        "swisscomdev"
      ],
      "owner": {
        "reputation": 35,
        "user_id": 6502952,
        "user_type": "registered",
        "profile_image": "https://www.gravatar.com/avatar/a4ccb9c6d53f17aafe25e644c127ad42?s=128&d=identicon&r=PG",
        "display_name": "Andr&#225;s Tornai",
        "link": "https://stackoverflow.com/users/6502952/andr%c3%a1s-tornai"
      },
      "is_answered": true,
      "view_count": 52,
      "accepted_answer_id": 52198521,
      "answer_count": 1,
      "score": 0,
      "last_activity_date": 1536295655,
      "creation_date": 1536179983,
      "last_edit_date": 1536295655,
      "question_id": 52193095,
      "link": "https://stackoverflow.com/questions/52193095/mongodb-couldnt-add-user-not-authorized-cloudfoundry",
      "title": "MongoDB: Couldn&#39;t add user - not authorized (CloudFoundry)"
    },
    {
      "tags": [
        "mongodb",
        "cloudfoundry",
        "swisscomdev"
      ],
      "owner": {
        "reputation": 44,
        "user_id": 10208987,
        "user_type": "registered",
        "profile_image": "https://lh3.googleusercontent.com/-pX5DL59VksY/AAAAAAAAAAI/AAAAAAAABt0/SwVr0elQSG0/photo.jpg?sz=128",
        "display_name": "Quentin Herzig",
        "link": "https://stackoverflow.com/users/10208987/quentin-herzig"
      },
      "is_answered": true,
      "view_count": 40,
      "accepted_answer_id": 52093233,
      "answer_count": 1,
      "score": 1,
      "last_activity_date": 1535620269,
      "creation_date": 1535619559,
      "question_id": 52092985,
      "link": "https://stackoverflow.com/questions/52092985/update-service-mongodb-failed",
      "title": "Update service MongoDB failed"
    },
    {
      "tags": [
        "mongodb",
        "ibm-cloud",
        "compose"
      ],
      "owner": {
        "reputation": 36,
        "user_id": 2363917,
        "user_type": "registered",
        "profile_image": "https://i.stack.imgur.com/GFxRP.jpg?s=128&g=1",
        "display_name": "Patrick P.",
        "link": "https://stackoverflow.com/users/2363917/patrick-p"
      },
      "is_answered": true,
      "view_count": 71,
      "answer_count": 1,
      "score": 1,
      "last_activity_date": 1533734318,
      "creation_date": 1533653071,
      "last_edit_date": 1533734318,
      "question_id": 51729578,
      "link": "https://stackoverflow.com/questions/51729578/how-to-access-oplog-database-on-mongodb-on-compose-ibm-cloud",
      "title": "How to access oplog database on MongoDB on compose (IBM Cloud)"
    },
    {
      "tags": [
        "mongodb",
        "cloud",
        "cloudfoundry"
      ],
      "owner": {
        "reputation": 42,
        "user_id": 7799449,
        "user_type": "registered",
        "accept_rate": 83,
        "profile_image": "https://i.stack.imgur.com/NdwSZ.jpg?s=128&g=1",
        "display_name": "LucasBrazi06",
        "link": "https://stackoverflow.com/users/7799449/lucasbrazi06"
      },
      "is_answered": true,
      "view_count": 36,
      "accepted_answer_id": 50989323,
      "answer_count": 1,
      "score": 1,
      "last_activity_date": 1529674893,
      "creation_date": 1529602905,
      "question_id": 50974641,
      "link": "https://stackoverflow.com/questions/50974641/how-to-access-the-mongodb-local-database-in-cloud-foundy",
      "title": "How to access the MongoDB &#39;local&#39; database in Cloud Foundy?"
    },
    {
      "tags": [
        "mongodb",
        "ibm-cloud",
        "node-red"
      ],
      "owner": {
        "reputation": 138,
        "user_id": 5593661,
        "user_type": "registered",
        "accept_rate": 39,
        "profile_image": "https://lh6.googleusercontent.com/-gMBGuz-0YK0/AAAAAAAAAAI/AAAAAAAAALU/RwIzt5Urux8/photo.jpg?sz=128",
        "display_name": "Hat hout",
        "link": "https://stackoverflow.com/users/5593661/hat-hout"
      },
      "is_answered": false,
      "view_count": 21,
      "answer_count": 0,
      "score": 0,
      "last_activity_date": 1529408111,
      "creation_date": 1529393240,
      "last_edit_date": 1529408111,
      "question_id": 50922798,
      "link": "https://stackoverflow.com/questions/50922798/how-to-configure-mongodb-with-nodered-on-bluemix",
      "title": "How to configure mongodb with nodered on bluemix?"
    },
    {
      "tags": [
        "php",
        "mongodb",
        "cloudfoundry",
        "swisscomdev",
        "composer.json"
      ],
      "owner": {
        "reputation": 92,
        "user_id": 7845320,
        "user_type": "registered",
        "accept_rate": 100,
        "profile_image": "https://i.stack.imgur.com/CDhct.png?s=128&g=1",
        "display_name": "Julien W.",
        "link": "https://stackoverflow.com/users/7845320/julien-w"
      },
      "is_answered": true,
      "view_count": 81,
      "accepted_answer_id": 49255740,
      "answer_count": 1,
      "score": 1,
      "last_activity_date": 1520942712,
      "creation_date": 1520842139,
      "question_id": 49230643,
      "link": "https://stackoverflow.com/questions/49230643/how-to-update-mongodb-version-in-swisscom-application-cloud",
      "title": "How to update MongoDB version in Swisscom Application Cloud?"
    },
    {
      "tags": [
        "mongodb",
        "github",
        "cloudfoundry"
      ],
      "owner": {
        "reputation": 1,
        "user_id": 3319049,
        "user_type": "registered",
        "profile_image": "https://www.gravatar.com/avatar/65ebe0ee5738d8d6b2e7df172720c173?s=128&d=identicon&r=PG&f=1",
        "display_name": "user3319049",
        "link": "https://stackoverflow.com/users/3319049/user3319049"
      },
      "is_answered": false,
      "view_count": 36,
      "answer_count": 0,
      "score": 0,
      "last_activity_date": 1515570101,
      "creation_date": 1515570101,
      "question_id": 48182469,
      "link": "https://stackoverflow.com/questions/48182469/how-to-migrate-mongodb-data-from-local-machine-to-sap-cloud-foundry",
      "title": "how to migrate mongodb data from local machine to sap cloud foundry?"
    },
    {
      "tags": [
        "cloudfoundry",
        "mongodump",
        "swisscomdev"
      ],
      "owner": {
        "reputation": 43,
        "user_id": 3910937,
        "user_type": "registered",
        "accept_rate": 60,
        "profile_image": "https://www.gravatar.com/avatar/924f016806ac9f23d21c94c667cb1648?s=128&d=identicon&r=PG&f=1",
        "display_name": "jerem0808",
        "link": "https://stackoverflow.com/users/3910937/jerem0808"
      },
      "is_answered": true,
      "view_count": 64,
      "accepted_answer_id": 48073735,
      "answer_count": 2,
      "score": 1,
      "last_activity_date": 1515320402,
      "creation_date": 1514967179,
      "last_edit_date": 1515320402,
      "question_id": 48073524,
      "link": "https://stackoverflow.com/questions/48073524/migrate-mongodb-container-service-mongodump-command-not-found",
      "title": "migrate MongoDB container service - mongodump command not found"
    },
    {
      "tags": [
        "node.js",
        "mongodb",
        "cloudfoundry",
        "swisscomdev"
      ],
      "owner": {
        "reputation": 43,
        "user_id": 3910937,
        "user_type": "registered",
        "accept_rate": 60,
        "profile_image": "https://www.gravatar.com/avatar/924f016806ac9f23d21c94c667cb1648?s=128&d=identicon&r=PG&f=1",
        "display_name": "jerem0808",
        "link": "https://stackoverflow.com/users/3910937/jerem0808"
      },
      "is_answered": true,
      "view_count": 223,
      "accepted_answer_id": 46928900,
      "answer_count": 2,
      "score": 1,
      "last_activity_date": 1508924124,
      "creation_date": 1507017797,
      "last_edit_date": 1507039965,
      "question_id": 46539850,
      "link": "https://stackoverflow.com/questions/46539850/cloud-foundry-mongodb-error-econnrefused",
      "title": "Cloud Foundry MongoDB Error ECONNREFUSED"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9889
}*/
			
			System.out.println("Finalizou com sucesso.");
			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();

		} finally {			
			//end();	
		}		
	}
}
