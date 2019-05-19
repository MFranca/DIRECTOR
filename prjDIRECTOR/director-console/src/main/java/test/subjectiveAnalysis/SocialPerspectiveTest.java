package test.subjectiveAnalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import perspectives.social.SubjectiveAnalysis;
import test.AbstractTest;

public class SocialPerspectiveTest extends AbstractTest {

	public static void main(String[] args) {
		
		try {
			start();

			//ObjectiveAnalysis analise = new ObjectiveAnalysis();
			List<String> candidates = new ArrayList<String>(Arrays.asList("1341,1781,1451,2001,1571,1241,2011,1791,1161,121,1,311,1401,1641,621,931,1291".split(",")));
			
			String resultado = new SubjectiveAnalysis().perform(candidates);
			
			System.out.println("Resultado: " + resultado);
			
			System.out.println("Finalizou com sucesso.");
			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {			
			end();	
		}
		

	}

}
