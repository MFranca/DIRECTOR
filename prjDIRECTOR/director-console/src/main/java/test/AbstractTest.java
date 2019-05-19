package test;

import mysqlDB.AbstractEntity;

public abstract class AbstractTest {	
	
	protected static void start() {
		System.out.println("Início do teste...\n");
		AbstractEntity.setupEntityManager(); // /director-dao/src/main/resources/META-INF/persistence.xml
		
		/*
		 * [EL Info]: query: 2018-05-13
		 * 11:44:08.484--UnitOfWork(583490561)--Communication failure detected
		 * when attempting to perform read query outside of a transaction.
		 * Attempting to retry query. Error was: Exception [EclipseLink-4002]
		 * (Eclipse Persistence Services - 2.6.0.v20150309-bf26070):
		 * org.eclipse.persistence.exceptions.DatabaseException Internal
		 * Exception: com.mysql.jdbc.exceptions.jdbc4.CommunicationsException:
		 * Communications link failure
		 * 
		 * Exception in thread "main" javax.persistence.PersistenceException:
		 * Exception [EclipseLink-4002] (Eclipse Persistence Services -
		 * 2.6.0.v20150309-bf26070):
		 * org.eclipse.persistence.exceptions.DatabaseException Internal
		 * Exception: com.mysql.jdbc.exceptions.jdbc4.CommunicationsException:
		 * Communications link failure
		 */
	}

	protected static void end() {
		AbstractEntity.dispose();
		System.out.println("Fim do teste...\n");
	}
}
