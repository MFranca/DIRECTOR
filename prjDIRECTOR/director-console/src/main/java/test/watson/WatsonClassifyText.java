package test.watson;

import ibmWatson.NLC;
import test.AbstractTest;

public class WatsonClassifyText extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();
			
			NLC watson = new NLC();
			
			//System.out.println(watson.getClassifiers());
			/*{
			  "classifiers" : [ {
			    "classifier_id" : "122608x455-nlc-2922",
			    "url" : "https://gateway.watsonplatform.net/natural-language-classifier/api/v1/classifiers/122608x455-nlc-2922",
			    "name" : "director_nlc",
			    "language" : "en",
			    "created" : "2018-10-21T15:58:42.397Z"
			  } ]
			}*/

			//System.out.println(watson.getClassifierDetails("122608x455-nlc-2922"));
			/*{
			  "classifier_id" : "122608x455-nlc-2922",
			  "name" : "director_nlc",
			  "language" : "en",
			  "created" : "2018-10-21T15:58:42.397Z",
			  "url" : "https://gateway.watsonplatform.net/natural-language-classifier/api/v1/classifiers/122608x455-nlc-2922",
			  "status_description" : "The classifier instance is now available and is ready to take classifier requests.",
			  "status" : "Available"
			}*/
			
			System.out.println(watson.classify("122608x455-nlc-2922", "I need to store json objects in a nosql database."));
			/*{
			  "classifier_id" : "122608x455-nlc-2922",
			  "url" : "https://gateway.watsonplatform.net/natural-language-classifier/api/v1/classifiers/122608x455-nlc-2922",
			  "text" : "I need to store json objects in a nosql database.",
			  "top_class" : "1741_redis-replaced",
			  "classes" : [ {
			    "class_name" : "1741_redis-replaced",
			    "confidence" : 0.16651333680067804
			  }, {
			    "class_name" : "2151_databases-for-redis",
			    "confidence" : 0.1285264593954601
			  }, {
			    "class_name" : "1051_compose-for-mongodb",
			    "confidence" : 0.09920557165043611
			  }, {
			    "class_name" : "1061_compose-for-postgresql",
			    "confidence" : 0.0726590269394225
			  }, {
			    "class_name" : "2153_databases-for-postgresql",
			    "confidence" : 0.05688615709255145
			  }, {
			    "class_name" : "741_mongodb-replaced",
			    "confidence" : 0.03504542971043878
			  }, {
			    "class_name" : "381_compose-for-scylladb",
			    "confidence" : 0.028680977161050013
			  }, {
			    "class_name" : "1201_GEO Web Services",
			    "confidence" : 0.028041871902179154
			  }, {
			    "class_name" : "791_compose-for-rethinkdb",
			    "confidence" : 0.01971636792266196
			  }, {
			    "class_name" : "1251_cloudantNoSQLDB",
			    "confidence" : 0.018813002747846624
			  } ]
			}*/

		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();

		} finally {
			end();
		}
		
	}

}
