package test.objectiveAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import perspectives.technical.ObjectiveAnalysis;
import test.AbstractTest;

public class TechnicalPerspectiveTest extends AbstractTest {

	public static void main(String[] args) {
		
		try {
			start();

			//ObjectiveAnalysis analise = new ObjectiveAnalysis();
			List<String> candidates = new ArrayList<String>(Arrays.asList("1341,1781,1451,2001,1571,1241,2011,1791,1161,121,1,311,1401,1641,621,931,1291".split(",")));
			//int agilityPriority = 1, assurancePriority = 1, financialPriority = 5, performancePriority = 1, securityPriority = 5, usabilityPriority = 1;
			int agilityPriority = 5, assurancePriority = 5, financialPriority = 1, performancePriority = 5, securityPriority = 1, usabilityPriority = 4;
			
			String resultado = new ObjectiveAnalysis().perform(candidates, agilityPriority, assurancePriority, financialPriority, performancePriority, securityPriority, usabilityPriority);
			
			System.out.println("Resultado: " + resultado);
			/*Resultado: Most adequate service: 2011 - elephantsql from PaaS: Pivotal Cloud Foundry
			Justification (outstanding qualities): 
			>> Scalability
			>> Portability
			>> Flexibility
			>> Availability
			>> On-going cost
			>> Access control & privilege management
			>> Accessibility
			
			Final evaluation order: [2011, 1781, 1791, 2001, 931, 1451, 121, 1, 1401, 1341, 1241, 1641, 1161, 621, 311, 1291, 1571]*/
			
			System.out.println("Finalizou com sucesso.");
			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {			
			end();	
		}
		

	}

}
