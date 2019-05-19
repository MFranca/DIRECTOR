package perspectives.social;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import perspectives.AbstractAnalysis;
import perspectives.AbstractCandidateEvaluation;
import perspectives.technical.CandidateTechnicalEvaluation;
import stackExchange.SEQuestion;
import utils.LogUtils;

public class SubjectiveAnalysis extends AbstractAnalysis {

	private static final int YEARS_FROM_TODAY_QUESTION_FILTER = -1; // negative paramenter = past
	
	public String perform( 
			List<String> candidates
			) throws Exception {

		String methodName = "perform";
		String source = "webApplication";
		String result = "";
		
		//result = "{ \"commingSoon\" : true }";
				
		Service service1st = null;
		CandidateSocialEvaluation evaluation1st = null;
		Service service2nd = null;
		CandidateSocialEvaluation evaluation2nd = null;
		Service service3rd = null;
		CandidateSocialEvaluation evaluation3rd = null;
				
		int hitCount;
		int ownerReputationSum;
		int viewHitCountSum;
		int scoreSum;
		int answeredHitCount;
		
		try {	
			start(methodName, source);
			
			LogUtils.logInformation(methodName, "Stating perform method for candidates: " + Arrays.toString(candidates.toArray()));
			List<AbstractCandidateEvaluation> evaluations = new ArrayList<>();
			
			// evaluate
			for (String aCandidate: candidates) {
				aCandidate = aCandidate.trim();				
				Service s = new Service().findById(Long.parseLong(aCandidate));
				Platform p = s.getPlatform();
				
				CandidateSocialEvaluation evaluation = new CandidateSocialEvaluation(s.getId());												
				LogUtils.logDebug(methodName, "Evaluating candidate: " + s.getId() + " - " + s.getName()  + " (" + s.getGuid() + ")"
						+ ", from PaaS " + p.getName() + ", "
						+ "by querying StackExchange!");
				
				SEQuestion query = new SEQuestion();
				Calendar lastYear = Calendar.getInstance();
				lastYear.add(Calendar.YEAR, YEARS_FROM_TODAY_QUESTION_FILTER); // activity during this last year				
				String response = query.list(lastYear.getTime(), s.getName());
				//LogUtils.logTrace("Response from querying StackExchange: " + response);				
								
				// ------ Transformations / Calculations ----------------------------------------
				
				// transform/navigate the JSON.
				JSONObject json; 
				JSONArray hitsJson;
				json = new JSONObject(response);
				
				hitCount = 0;
				ownerReputationSum = 0;
				viewHitCountSum = 0;
				scoreSum = 0;
				answeredHitCount = 0;
				
				if (json.has("items") && !json.isNull("items")) {
					hitsJson = (JSONArray) json.get("items");
					hitCount = hitsJson.length();					
					
					for (int i = 0; i < hitsJson.length(); i++) { // this "page"
						JSONObject jsonItem = hitsJson.getJSONObject(i);
						JSONObject jsonOwner = jsonItem.getJSONObject("owner");
						
						ownerReputationSum += jsonOwner.getInt("reputation");
						viewHitCountSum += jsonItem.getInt("view_count");
						scoreSum += jsonItem.getInt("score");
						answeredHitCount += jsonItem.getBoolean("is_answered")? 1 : 0; 
					}
					
				} else 
					LogUtils.logWarning(methodName, "Not able to query for " + s.getName() + " on StackExchange at this time.");
					
				//LogUtils.logTrace("Have finished transforming...");
								
				// attributes -------------------------------------------------------------------				
				evaluation.setHitCount(hitCount);
				//LogUtils.logTrace("setHitCount: " + hitCount);
				evaluation.setOwnerReputationSum(ownerReputationSum);
				//LogUtils.logTrace("setOwnerReputationSum: " + ownerReputationSum);
				evaluation.setViewHitCountSum(viewHitCountSum);
				//LogUtils.logTrace("setViewHitCountSum: " + viewHitCountSum);
				evaluation.setScoreSum(scoreSum);
				//LogUtils.logTrace("setScoreSum: " + scoreSum);
				evaluation.setAnsweredHitCount(answeredHitCount);
				//LogUtils.logTrace("setAnsweredHitCount: " + answeredHitCount);
							
				evaluations.add(evaluation);
			}		
			
			//LogUtils.logTrace("\n\n***");
			LogUtils.logTrace("Ranking " +  evaluations.size() + " candidates.");
			rankCandidates(evaluations);
			
			LogUtils.logTrace("Ranking (part 2) " +  evaluations.size() + " candidates.");
			Long[] ids = sortCandidates(evaluations);
			
			// Prepare the response...
			service1st = new Service().findById(ids[0]);						
			service2nd = new Service().findById(ids[1]);			
			if (ids.length >= 3) 
				service3rd = new Service().findById(ids[2]);				
						
			for (AbstractCandidateEvaluation anEvaluation : evaluations) {
				if (anEvaluation.getServiceId() == service1st.getId()) { 
					evaluation1st = (CandidateSocialEvaluation) anEvaluation;
					continue;
				}
				if (anEvaluation.getServiceId() == service2nd.getId()) { 
					evaluation2nd = (CandidateSocialEvaluation) anEvaluation;
					continue;
				}
				if (service3rd != null && anEvaluation.getServiceId() == service3rd.getId()) { 
					evaluation3rd = (CandidateSocialEvaluation) anEvaluation;
					continue;
				}
			}
			
			result = this.prepareResponse(
					service1st, evaluation1st, 
					service2nd, evaluation2nd, 
					service3rd, evaluation3rd, 
					ids).toString();
			
		} catch (Exception ex) {			
			LogUtils.logError(methodName, ex.getMessage());
			throw ex;
			
		} finally {
			end(methodName);	
		}			
		
		return result;
	}
	
	private static void rankCandidates(List<AbstractCandidateEvaluation> evaluations) throws Exception {
		int maxPoints = evaluations.size();
		int value;
		
		int[] hitCountValues = new int[maxPoints];
		int[] ownerReputationSumValues = new int[maxPoints];
		int[] viewHitCountSumValues = new int[maxPoints];
		int[] scoreSumValues = new int[maxPoints];
		int[] answeredHitCountValues = new int[maxPoints];
		
		// load vectors
		for (int i = 0; i< evaluations.size(); i++) {
			CandidateSocialEvaluation e = (CandidateSocialEvaluation) evaluations.get(i);			
			
			hitCountValues[i] = e.getHitCount();
			ownerReputationSumValues[i] = e.getOwnerReputationSum();
			viewHitCountSumValues[i] = e.getViewHitCountSum();
			scoreSumValues[i] = e.getScoreSum();
			answeredHitCountValues[i] = e.getAnsweredHitCount();	
		}
				
		for (AbstractCandidateEvaluation anEvaluation: evaluations) {
			CandidateSocialEvaluation e = (CandidateSocialEvaluation) anEvaluation;
			
			value = e.getHitCount();
			e.setHitPoints(getRanking(value, hitCountValues));
			value = e.getOwnerReputationSum();
			e.setOwnerReputationPoints(getRanking(value, ownerReputationSumValues));			
			value = e.getViewHitCountSum();
			e.setViewHitPoints(getRanking(value, viewHitCountSumValues));
			value = e.getScoreSum();
			e.setScorePoints(getRanking(value, scoreSumValues));
			value = e.getAnsweredHitCount();
			e.setAnsweredHitPoints(getRanking(value, answeredHitCountValues));
		}
	}
	
	private JsonObject prepareResponse(
			Service s1, CandidateSocialEvaluation e1, 
			Service s2, CandidateSocialEvaluation e2, 
			Service s3, CandidateSocialEvaluation e3, 
			Long[] rankedServiceIds) {
		
		JsonObject jsonResponse, jsonService1, jsonEvaluation1, jsonService2, jsonEvaluation2, jsonService3, jsonEvaluation3;
		String rationale;
			
		rationale = "These are the outstanding social characteristics of this service: ";		
		if (e1.getHitPoints() == rankedServiceIds.length)
			rationale += ">> # of Questions, ";				
		if (e1.getOwnerReputationPoints() == rankedServiceIds.length)
			rationale += ">> Reputation of the Owner, ";				
		if (e1.getViewHitCountSum() == rankedServiceIds.length)
			rationale += ">> # of Views, ";		
		if (e1.getScorePoints() == rankedServiceIds.length)
			rationale += ">> # of Votes, ";		
		if (e1.getAnsweredHitPoints() == rankedServiceIds.length)
			rationale += ">> # of Answered Questions, ";		
				
		rationale += "i.e., this candidate has the strongest community engagement, being used on a Cloud Foundry context.";
		
		// Preparing part of the response (Service)...								
		jsonService1 =
				Json.createObjectBuilder() // implicit root
					.add("id", s1.getId())
					.add("guid", s1.getGuid())
					.add("name", s1.getName())
					.add("fromPaas", s1.getPlatform().getName())
              	.build();
		
		// Preparing part of the response (Service #2)...								
		jsonService2 =
				Json.createObjectBuilder() // implicit root
					.add("id", s2.getId())
					.add("guid", s2.getGuid())
					.add("name", s2.getName())
					.add("fromPaas", s2.getPlatform().getName())
              	.build();
		if (s3 == null)
			jsonService3 = Json.createObjectBuilder().build();
		else
			// Preparing part of the response (Service #3)...								
			jsonService3 =
					Json.createObjectBuilder() // implicit root
						.add("id", s3.getId())
						.add("guid", s3.getGuid())
						.add("name", s3.getName())
						.add("fromPaas", s3.getPlatform().getName())
	              	.build();		
		
		// Preparing part of the response (Evaluation)...								
		jsonEvaluation1 =
				Json.createObjectBuilder() // implicit root
					.add("hit", e1.getHitCount())
					.add("hitPoints", e1.getHitPoints())
					.add("ownerReputation", e1.getOwnerReputationSum())
					.add("ownerReputationPoints", e1.getOwnerReputationPoints())
					.add("view", e1.getViewHitCountSum())
					.add("viewPoints", e1.getViewHitPoints())
					.add("score", e1.getScoreSum())
					.add("scorePoints", e1.getScorePoints())
					.add("answer", e1.getAnsweredHitCount())
					.add("answerPoints", e1.getAnsweredHitPoints())					
              	.build();	
		
		// Preparing part of the response (Evaluation #2)...								
		jsonEvaluation2 =
				Json.createObjectBuilder() // implicit root
					.add("hit", e2.getHitCount())
					.add("hitPoints", e2.getHitPoints())
					.add("ownerReputation", e2.getOwnerReputationSum())
					.add("ownerReputationPoints", e2.getOwnerReputationPoints())
					.add("view", e2.getViewHitCountSum())
					.add("viewPoints", e2.getViewHitPoints())
					.add("score", e2.getScoreSum())
					.add("scorePoints", e2.getScorePoints())
					.add("answer", e2.getAnsweredHitCount())
					.add("answerPoints", e2.getAnsweredHitPoints())
              	.build();	

		if (s3 == null)
			jsonEvaluation3 = Json.createObjectBuilder().build();
		else
			// Preparing part of the response (Evaluation #2)...								
			jsonEvaluation3 =
					Json.createObjectBuilder() // implicit root
						.add("hit", e3.getHitCount())
						.add("hitPoints", e3.getHitPoints())
						.add("ownerReputation", e3.getOwnerReputationSum())
						.add("ownerReputationPoints", e3.getOwnerReputationPoints())
						.add("view", e3.getViewHitCountSum())
						.add("viewPoints", e3.getViewHitPoints())
						.add("score", e3.getScoreSum())
						.add("scorePoints", e3.getScorePoints())
						.add("answer", e3.getAnsweredHitCount())
						.add("answerPoints", e3.getAnsweredHitPoints())
	              	.build();	
		
		// Preparing the final response...								
		jsonResponse =
				Json.createObjectBuilder() // implicit root
					.add("mostAdequateService", jsonService1)					
					.add("justification)", rationale)
					.add("globalScore", e1.getGlobalScore())
					.add("globalRank", e1.getGlobalRank())
					.add("evaluation", jsonEvaluation1)
					
					.add("secondAdequateService", jsonService2)
					.add("secondGlobalScore", e2.getGlobalScore())
					.add("secondGlobalRank", e2.getGlobalRank())
					.add("secondEvaluation", jsonEvaluation2)
					
					.add("thirdAdequateService", jsonService3)
					.add("thirdGlobalScore", e3 == null? 0 : e3.getGlobalScore())
					.add("thirdGlobalRank", e3 == null? 0 : e3.getGlobalRank())
					.add("thirdEvaluation", jsonEvaluation3)
					
					.add("finalServicesRank", Arrays.toString(rankedServiceIds))
              	.build();
		
		return jsonResponse;
	}	
}