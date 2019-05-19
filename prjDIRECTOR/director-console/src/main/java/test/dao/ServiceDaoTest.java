package test.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mysqlDB.model.Service;
import test.AbstractTest;

public class ServiceDaoTest extends AbstractTest {

	public static void main(String[] args) {
		
		try {
			start();
			        			
			// --------------------------------------------------
			Service dao = new Service();			
			// --------------------------------------------------
			
			// testar filtro
			List<String> filtroPositivo = new ArrayList<>();
			List<String> filtroNegativo = new ArrayList<>();

			filtroPositivo.add("dbaas");
			filtroPositivo.add("nosql");
			filtroPositivo.add("data store");
			filtroPositivo.add("data stores");
			filtroPositivo.add("dba");
			
			// só com filtro positivo
			System.out.println("*** FILTRO POSITIVO ***");
			Map<String, String> resultado = dao.findCandidates(filtroPositivo, filtroNegativo);			
			System.out.println("Quantidade de candidatos ===>" + resultado.size());
			
			if (resultado.isEmpty())
				System.out.println("Nenhum candidato foi encontrado para os parâmetros fornecidos...");
			else {
				Iterator it = resultado.entrySet().iterator();
				
			    while (it.hasNext()) {			        
					Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
			        
			        System.out.println(pair.getKey() + " = " + pair.getValue());
			        //it.remove(); // avoids a ConcurrentModificationException
			    }		    
			}	
			
			filtroNegativo.add("ibm_deprecated");
			filtroNegativo.add("key-value");
			filtroNegativo.add("caching");
			
			// com filtro negativo
			System.out.println("*** FILTRO NEGATIVO ***");
			resultado = dao.findCandidates(filtroPositivo, filtroNegativo);			
			System.out.println("Quantidade de candidatos ===>" + resultado.size());
			
			if (resultado.isEmpty())
				System.out.println("Nenhum candidato foi encontrado para os parâmetros fornecidos...");
			else {
				Iterator it = resultado.entrySet().iterator();
				
			    while (it.hasNext()) {			        
					Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
			        
			        System.out.println(pair.getKey() + " = " + pair.getValue());
			        //it.remove(); // avoids a ConcurrentModificationException
			    }		    
			}
			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {			
			end();	
		}
	}
}
