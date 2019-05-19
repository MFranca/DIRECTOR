package test.dao;

import java.util.List;

import mysqlDB.model.Platform;
import mysqlDB.model.Service;
import test.AbstractTest;

public class PlatformDaoTest extends AbstractTest {

	public static void main(String[] args) {
		
		try {
			start();
			        			
			// --------------------------------------------------
			Platform dao = new Platform().findById(11L);			
			// --------------------------------------------------
			
			List<Service> resultado = dao.findActiveServices();
			System.out.println("Quantidade de candidatos ===>" + resultado.size());
			
			if (resultado.isEmpty())
				System.out.println("Nenhum serviço ativo foi encontrado...");
			
			else {
				for (Service s: resultado)
					System.out.println("Servico " + s.getName());
			}	

			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {			
			end();	
		}
	}
}
