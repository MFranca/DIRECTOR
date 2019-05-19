package perspectives.technical;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonObject;

import cloudant.dao.ServiceNoSqlDao;
import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import perspectives.AbstractAnalysis;
import perspectives.AbstractCandidateEvaluation;
import utils.LogUtils;

public class ObjectiveAnalysis extends AbstractAnalysis {

	public String perform( 
			List<String> candidates, 
			int agilityPriority, int assurancePriority, int financialPriority,			
			int performancePriority, int securityPriority, int usabilityPriority) throws Exception {

		String methodName = "perform";
		String source = "webApplication";
		String result = "";
		
		Service service1st = null;
		CandidateTechnicalEvaluation evaluation1st = null;
		Service service2nd = null;
		CandidateTechnicalEvaluation evaluation2nd = null;
		Service service3rd = null;
		CandidateTechnicalEvaluation evaluation3rd = null;

		try {	
			start(methodName, source);
			
			//LogUtils.logTrace("Stating perform method for candidates: " + Arrays.toString(candidates.toArray()));
			List<AbstractCandidateEvaluation> evaluations = new ArrayList<>();
			
			// evaluate
			for (String aCandidate: candidates) {
				aCandidate = aCandidate.trim();
				
				//LogUtils.logDebug(methodName, "Evaluating candidate: " + aCandidate);
				Service s = new Service().findById(Long.parseLong(aCandidate));
				Platform p = s.getPlatform();
				
				CandidateTechnicalEvaluation evaluation = new CandidateTechnicalEvaluation(
						agilityPriority, assurancePriority, financialPriority,
						performancePriority, securityPriority, usabilityPriority);
				
				evaluation.setServiceId(s.getId());				
				//LogUtils.logTrace("--------------------------------------------------------------------");
				//LogUtils.logTrace("Evaluating candidate: " + s.getId() + " - " + s.getName());
				LogUtils.logDebug(methodName, "Evaluating candidate: " + s.getId() + " - " + s.getName()  + " (" + s.getGuid() + ") " 
						+ "querying NoSql: " + p.getDbName());
								
				// ------ Transformations / Calculations ----------------------------------------
				//"requires": ["a", "b"], []
				String requires;
				if (s.getRequires() != null) {
					requires = s.getRequires();
					requires = requires.substring(1, requires.length() - 1); // remove brackets
				}
				else
					requires = "";
				//LogUtils.logTrace("Requires: " + requires);
				 
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date today = sdf.parse(sdf.format(new Date()));				
				long elapsedDaysLastUpdate =TimeUnit.DAYS.convert(today.getTime() - s.getUpdated().getTime(), TimeUnit.MILLISECONDS);
				long elapsedDaysCreate =TimeUnit.DAYS.convert(today.getTime() - s.getCreated().getTime(), TimeUnit.MILLISECONDS);
								
				// Inactive Days calculation (!!!)						
				ServiceNoSqlDao	dao = new ServiceNoSqlDao(p.getDbName());
				/*
				LogUtils.logTrace("Inactive Days calculation: ");		
				LogUtils.logTrace("Creation: " + s.getCreated().toLocaleString());
				LogUtils.logTrace("elapsedDaysCreation: " + elapsedDaysCreate);
				LogUtils.logTrace("PaaS running days: " + p.getInformation().getRunningDays());
				LogUtils.logTrace("uSaaS active days: " + dao.getServicesActiveDays(s.getGuid()));
				*/
				
				long inactiveDays =0;
				if (elapsedDaysCreate > p.getInformation().getRunningDays())					
					inactiveDays = p.getInformation().getRunningDays() - dao.getServicesActiveDays(s.getGuid());
				else
					inactiveDays = elapsedDaysCreate - dao.getServicesActiveDays(s.getGuid());
				if (inactiveDays < 0) // we have more information about the service than the platform
					inactiveDays = 0; 
				//LogUtils.logTrace("inactiveDays: " + inactiveDays);				
				
				//LogUtils.logTrace("Have finished transforming...");
				// attributes -------------------------------------------------------------------
				evaluation.setScalability(s.isPlanUpdateable()? 1 : 0);				
				evaluation.setPortability(requires.equals("")? 0 : requires.split(",").length * -1);
				evaluation.setFlexibility(Math.toIntExact(s.getServicePlansQuantity()));
				evaluation.setAdaptability(Math.toIntExact(elapsedDaysLastUpdate) * -1);
				evaluation.setAvailability(Math.toIntExact(inactiveDays) * -1);
				evaluation.setStability(Math.toIntExact(elapsedDaysCreate));
				evaluation.setOnGoingCost(s.isFree()? 1 : 0);
				evaluation.setFunctionality(s.getTags().size());
				evaluation.setAccessControlAndPrivilegeManagement(s.isBindable()? 1 : 0);				
				evaluation.setAccessibility(s.isPublic()? 1 : 0);
				evaluation.setLearnability((s.getDocumentationUrl() == null || s.getDocumentationUrl().trim().equals(""))? 0 : 1);
				
				/*
				if (evaluation.getLearnability() == 1)
					LogUtils.logTrace("Learnability: " + s.getDocumentationUrl());
				*/
				
				/*
				LogUtils.logTrace("Scalability: " + evaluation.getScalability());
				LogUtils.logTrace("Portability: " + evaluation.getPortability());
				LogUtils.logTrace("Flexibility: " + evaluation.getFlexibility());
				LogUtils.logTrace("Adaptability: " + evaluation.getAdaptability());
				LogUtils.logTrace("Availability: " + evaluation.getAvailability());
				LogUtils.logTrace("Stability: " + evaluation.getStability());
				LogUtils.logTrace("OnGoingCost: " + evaluation.getOnGoingCost());
				LogUtils.logTrace("Functionality: " + evaluation.getFunctionality());
				LogUtils.logTrace("AccessControlAndPrivilegeManagement: " + evaluation.getAccessControlAndPrivilegeManagement());
				LogUtils.logTrace("Accessibility: " + evaluation.getAccessibility());
				LogUtils.logTrace("Learnability: " + evaluation.getLearnability());
				*/
				evaluations.add(evaluation);
			}		
			
			//LogUtils.logTrace("\n\n***");
			//LogUtils.logTrace("Ranking " +  evaluations.size() + " candidates.");
			rankCandidates(evaluations);
			
			//LogUtils.logTrace("\n\n***");
			//LogUtils.logTrace("Ranking (part 2) " +  evaluations.size() + " candidates.");
			Long[] ids = sortCandidates(evaluations);
			
			// Prepare the response...
			service1st = new Service().findById(ids[0]);						
			service2nd = new Service().findById(ids[1]);			
			if (ids.length >= 3) 
				service3rd = new Service().findById(ids[2]);				
						
			for (AbstractCandidateEvaluation anEvaluation : evaluations) {
				if (anEvaluation.getServiceId() == service1st.getId()) { 
					evaluation1st = (CandidateTechnicalEvaluation) anEvaluation;
					continue;
				}
				if (anEvaluation.getServiceId() == service2nd.getId()) { 
					evaluation2nd = (CandidateTechnicalEvaluation) anEvaluation;
					continue;
				}
				if (service3rd != null && anEvaluation.getServiceId() == service3rd.getId()) { 
					evaluation3rd = (CandidateTechnicalEvaluation) anEvaluation;
					continue;
				}
			}
			result = this.prepareResponse(
					service1st, evaluation1st, 
					service2nd, evaluation2nd, 
					service3rd, evaluation3rd, 
					ids).toString();	
			
			/*
			result = "Most adequate service: " + s.getId() + " - " + s.getName() + " from PaaS: " + s.getPlatform().getName();			
			result += "\nJustification (outstanding qualities): \n";
			
			for (CandidateEvaluation e : evaluations) {
				if (e.getServiceId() == s.getId()) {
					if (e.getScalabilityPoints() == evaluations.size())
						result += ">> Scalability\n";
					
					if (e.getPortabilityPoints() == evaluations.size())
						result += ">> Portability\n";
					
					if (e.getFlexibilityPoints() == evaluations.size())
						result += ">> Flexibility\n";
					
					if (e.getAdaptabilityPoints() == evaluations.size())
						result += ">> Adaptability\n";
					
					if (e.getAvailabilityPoints() == evaluations.size())
						result += ">> Availability\n";
					
					if (e.getStabilityPoints() == evaluations.size())
						result += ">> Stability\n";
					
					if (e.getOnGoingCostPoints() == evaluations.size())
						result += ">> On-going cost\n";
					
					if (e.getFunctionalityPoints() == evaluations.size())
						result += ">> Functionality\n";
					
					if (e.getAccessControlAndPrivilegeManagementPoints() == evaluations.size())
						result += ">> Access control & privilege management\n";
					
					if (e.getAccessibilityPoints() == evaluations.size())
						result += ">> Accessibility\n";
					
					if (e.getLearnabilityPoints() == evaluations.size())
						result += ">> Learnability\n";		
					
					result += "resulting in a Global Score of: " + e.getGlobalScore();
				}
			}
			
			result += "\nFinal evaluation order: " + Arrays.toString(ids);
			*/
			
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
		int[] scalabilityValues = new int[maxPoints];
		int[] portabilityValues = new int[maxPoints];
		int[] flexibilityValues = new int[maxPoints];
		int[] adaptabilityValues = new int[maxPoints];
		int[] availabilityValues = new int[maxPoints];
		int[] stabilityValues = new int[maxPoints];
		int[] onGoingCostValues = new int[maxPoints];
		int[] functionalityValues = new int[maxPoints];
		int[] accessControlValues = new int[maxPoints];
		int[] accessibilityValues = new int[maxPoints];
		int[] learnabilityValues = new int[maxPoints];
		int value;
		
		// load vectors
		for (int i = 0; i< evaluations.size(); i++) {
			CandidateTechnicalEvaluation e = (CandidateTechnicalEvaluation) evaluations.get(i);
			
			scalabilityValues[i] = e.getScalability();;
			portabilityValues[i] = e.getPortability();
			flexibilityValues[i] = e.getFlexibility();
			adaptabilityValues[i] = e.getAdaptability();
			availabilityValues[i] = e.getAvailability();
			stabilityValues[i] = e.getStability();
			onGoingCostValues[i] = e.getOnGoingCost();
			functionalityValues[i] = e.getFunctionality();
			accessControlValues[i] = e.getAccessControlAndPrivilegeManagement();
			accessibilityValues[i] = e.getAccessibility();
			learnabilityValues[i] = e.getLearnability();
		}
				
		for (AbstractCandidateEvaluation anEvaluation: evaluations) {
			CandidateTechnicalEvaluation e = (CandidateTechnicalEvaluation) anEvaluation;
			
			// Scalability
			/*
			if (anEvaluation.getScalability() == 1)
				anEvaluation.setScalabilityPoints(maxPoints);
			else // 0 (2nd place)
				anEvaluation.setScalabilityPoints(maxPoints-1);
			*/
			value = e.getScalability();
			e.setScalabilityPoints(getRanking(value, scalabilityValues));
			
			// -(Portability)
			value = e.getPortability();
			e.setPortabilityPoints(getRanking(value, portabilityValues));
			
			// Flexibility
			value = e.getFlexibility();
			e.setFlexibilityPoints(getRanking(value, flexibilityValues));
			
			// -(Adaptability)
			value = e.getAdaptability();
			e.setAdaptabilityPoints(getRanking(value, adaptabilityValues));
			
			// -(Availability)
			value = e.getAvailability();
			e.setAvailabilityPoints(getRanking(value, availabilityValues));
			
			// Stability
			value = e.getStability();
			e.setStabilityPoints(getRanking(value, stabilityValues));
			
			// On-going cost
			/*
			if (anEvaluation.getOnGoingCost() == 1)
				anEvaluation.setOnGoingCostPoints(maxPoints);
			else // 0 (2nd place)
				anEvaluation.setOnGoingCostPoints(maxPoints-1);
			*/
			value = e.getOnGoingCost();
			e.setOnGoingCostPoints(getRanking(value, onGoingCostValues));
						
			// Functionality 
			value = e.getFunctionality();
			e.setFunctionalityPoints(getRanking(value, functionalityValues));
			
			// Access control & privilege management
			/*
			if (anEvaluation.getAccessControlAndPrivilegeManagement() == 1)
				anEvaluation.setAccessControlAndPrivilegeManagementPoints(maxPoints);
			else // 0 (2nd place)
				anEvaluation.setAccessControlAndPrivilegeManagementPoints(maxPoints-1);
			*/
			value = e.getAccessControlAndPrivilegeManagement();
			e.setAccessControlAndPrivilegeManagementPoints(getRanking(value, accessControlValues));
						
			// Accessibility
			/*
			if (anEvaluation.getAccessibility() == 1)
				anEvaluation.setAccessibilityPoints(maxPoints);
			else // 0 (2nd place)
				anEvaluation.setAccessibilityPoints(maxPoints-1);
			*/
			value = e.getAccessibility();
			e.setAccessibilityPoints(getRanking(value, accessibilityValues));
			
			// Learnability
			/*
			if (anEvaluation.getLearnability() == 1)
				anEvaluation.setLearnabilityPoints(maxPoints);
			else // 0 (2nd place)
				anEvaluation.setLearnabilityPoints(maxPoints-1);
			*/
			value = e.getLearnability();
			e.setLearnabilityPoints(getRanking(value, learnabilityValues));
			
			/*			
			LogUtils.logTrace("\nEvaluating service id: " + anEvaluation.getServiceId());
			LogUtils.logTrace("Scalability points scored: " + anEvaluation.getScalabilityPoints());
			LogUtils.logTrace("-(Portability) points scored: " + anEvaluation.getPortabilityPoints());
			LogUtils.logTrace("Flexibility points scored: " + anEvaluation.getFlexibilityPoints());
			LogUtils.logTrace("-(Adaptability) points scored: " + anEvaluation.getAdaptabilityPoints());			
			LogUtils.logTrace("-(Availability) points scored: " + anEvaluation.getAvailabilityPoints());
			LogUtils.logTrace("Stability points scored: " + anEvaluation.getStabilityPoints());
			LogUtils.logTrace("On-going cost points scored: " + anEvaluation.getOnGoingCostPoints());
			LogUtils.logTrace("Functionality points scored: " + anEvaluation.getFunctionalityPoints());
			LogUtils.logTrace("Access control points scored: " + anEvaluation.getAccessControlAndPrivilegeManagementPoints());
			LogUtils.logTrace("Accessibility points scored: " + anEvaluation.getAccessibilityPoints());
			LogUtils.logTrace("Learnability points scored: " + anEvaluation.getLearnabilityPoints());
			*/			
		}
	}
	
	private JsonObject prepareResponse(
			Service s1, CandidateTechnicalEvaluation e1, 
			Service s2, CandidateTechnicalEvaluation e2, 
			Service s3, CandidateTechnicalEvaluation e3, 
			Long[] rankedServiceIds) {
		
		JsonObject jsonResponse, jsonService1, jsonEvaluation1, jsonService2, jsonEvaluation2, jsonService3, jsonEvaluation3;
		String rationale;
			
		rationale = "These are the outstanding service's qualities: ";		
		if (e1.getScalabilityPoints() == rankedServiceIds.length)
			rationale += ">> Scalability, ";				
		if (e1.getPortabilityPoints() == rankedServiceIds.length)
			rationale += ">> Portability, ";				
		if (e1.getFlexibilityPoints() == rankedServiceIds.length)
			rationale += ">> Flexibility, ";		
		if (e1.getAdaptabilityPoints() == rankedServiceIds.length)
			rationale += ">> Adaptability, ";		
		if (e1.getAvailabilityPoints() == rankedServiceIds.length)
			rationale += ">> Availability, ";		
		if (e1.getStabilityPoints() == rankedServiceIds.length)
			rationale += ">> Stability, ";		
		if (e1.getOnGoingCostPoints() == rankedServiceIds.length)
			rationale += ">> On-going cost, ";		
		if (e1.getFunctionalityPoints() == rankedServiceIds.length)
			rationale += ">> Functionality, ";		
		if (e1.getAccessControlAndPrivilegeManagementPoints() == rankedServiceIds.length)
			rationale += ">> Access control & privilege management, ";		
		if (e1.getAccessibilityPoints() == rankedServiceIds.length)
			rationale += ">> Accessibility, ";		
		if (e1.getLearnabilityPoints() == rankedServiceIds.length)
			rationale += ">> Learnability, ";		
		rationale += " among all the evaluated QoS attributes.";
		
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
					.add("scalability", e1.getScalability())
					.add("scalabilityPoints", e1.getScalabilityPoints())
					.add("portability", e1.getPortability())
					.add("portabilityPoints", e1.getPortabilityPoints())
					.add("flexibility", e1.getFlexibility())
					.add("flexibilityPoints", e1.getFlexibilityPoints())
					.add("adaptability", e1.getAdaptability())
					.add("adaptabilityPoints", e1.getAdaptabilityPoints())
					.add("availability", e1.getAvailability())
					.add("availabilityPoints", e1.getAvailabilityPoints())
					.add("stability", e1.getStability())
					.add("stabilityPoints", e1.getStabilityPoints())
					.add("onGoingCost", e1.getOnGoingCost())
					.add("onGoingCostPoints", e1.getOnGoingCostPoints())					
					.add("functionality", e1.getFunctionality())
					.add("functionalityPoints", e1.getFunctionalityPoints())
					.add("accessControlAndPrivilegeManagement", e1.getAccessControlAndPrivilegeManagement())
					.add("accessControlAndPrivilegeManagementPoints", e1.getAccessControlAndPrivilegeManagementPoints())
					.add("accessibility", e1.getAccessibility())
					.add("accessibilityPoints", e1.getAccessibilityPoints())
					.add("learnability", e1.getLearnability())
					.add("learnabilityPoints", e1.getLearnabilityPoints())
              	.build();	
		
		// Preparing part of the response (Evaluation #2)...								
		jsonEvaluation2 =
				Json.createObjectBuilder() // implicit root
					.add("scalability", e2.getScalability())
					.add("scalabilityPoints", e2.getScalabilityPoints())
					.add("portability", e2.getPortability())
					.add("portabilityPoints", e2.getPortabilityPoints())
					.add("flexibility", e2.getFlexibility())
					.add("flexibilityPoints", e2.getFlexibilityPoints())
					.add("adaptability", e2.getAdaptability())
					.add("adaptabilityPoints", e2.getAdaptabilityPoints())
					.add("availability", e2.getAvailability())
					.add("availabilityPoints", e2.getAvailabilityPoints())
					.add("stability", e2.getStability())
					.add("stabilityPoints", e2.getStabilityPoints())
					.add("onGoingCost", e2.getOnGoingCost())
					.add("onGoingCostPoints", e2.getOnGoingCostPoints())					
					.add("functionality", e2.getFunctionality())
					.add("functionalityPoints", e2.getFunctionalityPoints())
					.add("accessControlAndPrivilegeManagement", e2.getAccessControlAndPrivilegeManagement())
					.add("accessControlAndPrivilegeManagementPoints", e2.getAccessControlAndPrivilegeManagementPoints())
					.add("accessibility", e2.getAccessibility())
					.add("accessibilityPoints", e2.getAccessibilityPoints())
					.add("learnability", e2.getLearnability())
					.add("learnabilityPoints", e2.getLearnabilityPoints())
              	.build();	

		if (s3 == null)
			jsonEvaluation3 = Json.createObjectBuilder().build();
		else
			// Preparing part of the response (Evaluation #2)...								
			jsonEvaluation3 =
					Json.createObjectBuilder() // implicit root
						.add("scalability", e3.getScalability())
						.add("scalabilityPoints", e3.getScalabilityPoints())
						.add("portability", e3.getPortability())
						.add("portabilityPoints", e3.getPortabilityPoints())
						.add("flexibility", e3.getFlexibility())
						.add("flexibilityPoints", e3.getFlexibilityPoints())
						.add("adaptability", e3.getAdaptability())
						.add("adaptabilityPoints", e3.getAdaptabilityPoints())
						.add("availability", e3.getAvailability())
						.add("availabilityPoints", e3.getAvailabilityPoints())
						.add("stability", e3.getStability())
						.add("stabilityPoints", e3.getStabilityPoints())
						.add("onGoingCost", e3.getOnGoingCost())
						.add("onGoingCostPoints", e3.getOnGoingCostPoints())					
						.add("functionality", e3.getFunctionality())
						.add("functionalityPoints", e3.getFunctionalityPoints())
						.add("accessControlAndPrivilegeManagement", e3.getAccessControlAndPrivilegeManagement())
						.add("accessControlAndPrivilegeManagementPoints", e3.getAccessControlAndPrivilegeManagementPoints())
						.add("accessibility", e3.getAccessibility())
						.add("accessibilityPoints", e3.getAccessibilityPoints())
						.add("learnability", e3.getLearnability())
						.add("learnabilityPoints", e3.getLearnabilityPoints())
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