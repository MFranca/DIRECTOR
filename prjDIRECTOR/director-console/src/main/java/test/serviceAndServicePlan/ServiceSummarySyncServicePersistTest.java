package test.serviceAndServicePlan;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cloudFoundry.CFService;
import cloudant.dao.ServiceNoSqlDao;
import cloudant.dao.SummaryNoSqlDao;
import cloudant.model.ServiceDocument;
import cloudant.model.SummaryDocument;
import mysqlDB.model.Platform;
import mysqlDB.model.PlatformInformation;
import test.AbstractTest;

public class ServiceSummarySyncServicePersistTest extends AbstractTest {

	private static Platform platformDao;
	private static CFService daoCFService;

	public static void main(String[] args) {		
		start();
		
		// Get all available platforms (active)		
		List<Platform> platforms = platformDao.findActive();

		if (platforms != null)
			for (Platform p : platforms) {				
				System.out.println("\n***\n[Plat]: " + p.toString());
				
				// ler todos os serviços disponíveis hoje para esta plataforma.
				daoCFService = new CFService(p.getEndpoint());

				String json = daoCFService.getFirstJson();
				prettyPrintServicesSummary(p, json); // System.out.println("JSON:" + json);
				
				prettyPrintServices(p, json);

				while (!daoCFService.getNextUrl().equals("")) {
					json = daoCFService.getNextJson(); // System.out.println("JSON (next): " + json);
					prettyPrintServices(p, json);
				}
			}

		end();
	}

	private static void prettyPrintServicesSummary(Platform p, String jsonText) {
		/*
		 * { "total_results": 180, "total_pages": 4, "prev_url": null,
		 * "next_url":
		 * "/v2/services?order-direction=asc&page=2&results-per-page=50",
		 * "resources": [ {
		 */

		// transform/navigate the JSON.
		JSONObject json = new JSONObject(jsonText);

		// ---------------------- Services Summary --------------------------------------
		if (json.has("total_results") && !json.isNull("total_results")) {
			/*
			 * SummaryDocument aSummary = new SummaryDocument();
			 * aSummary.setTotalResults(json.getInt("total_results"));
			 * aSummary.setTotalPages(json.getInt("total_pages"));
			 */

			try {
				// armazenar documentos (sumário) no NoSql (CouchDB/Cloudant) - no banco desta plataforma.				
				SummaryDocument aSummary = new SummaryDocument(json);
				//aSummary.setSource("manualTesting");
				
				System.out.println("A services summary at " + p.getName() + " was found: " + "Total Result - "
						+ aSummary.getTotalResults() + " in " + aSummary.getTotalPages() + " pages.");

				// Persistir no NoSql ----------------------------------------
				SummaryNoSqlDao noSqlDao = new SummaryNoSqlDao(p.getDbName());
				//noSqlDao.setDbName(p.getDbName());

				System.out.println("Salvando o documento no banco de dados NOSQL...");
				String id = noSqlDao.save(aSummary);
				System.out.println("Salvo com o document ID:" + id);
				
				// se está na hora de atualizar o banco relacional, fazê-lo (qtd de microserviços.). ----------
				PlatformInformation info; // premissa: sempre irá rolar um SYNC da plataforma antes do SYNC de serviços.
				// For each platform, check if there is information is up to date (today)
				info = p.getInformation();
				
				if (info.isPlatformInformationOutOfDate()) {
					System.out.println("Atualizando o info no banco de dados...");
					
                    // update and persist info for today.
					info.setServicesQuantity(aSummary.getTotalResults());
                    info.setSyncDate(new Date()); // today                                          
                    info.save();
                }				

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Erro: " + e.getMessage());
			}

		} else {
			System.out.println("No services summary at " + p.getName() + " were found.");
			return;
		}
	}

	private static void prettyPrintServices(Platform p, String jsonText) {
		/*
		 * { "total_results": 180, "total_pages": 4, "prev_url": null,
		 * "next_url":
		 * "/v2/services?order-direction=asc&page=2&results-per-page=50",
		 * "resources": [ { "metadata": { "guid":
		 * "bed0b74d-6d3e-47b4-ade5-b1407a5b1795", "url":
		 * "/v2/services/bed0b74d-6d3e-47b4-ade5-b1407a5b1795", "created_at":
		 * "2014-04-15T20:58:58Z", "updated_at": "2018-02-13T18:52:34Z" },
		 * "entity": { "label": "blazemeter", "provider": null, "url": null,
		 */

		// transform/navigate the JSON.
		JSONObject json = new JSONObject(jsonText);
		JSONArray servicesJson;

		// ---------------------- Services
		// --------------------------------------
		if (json.has("resources") && !json.isNull("resources"))
			servicesJson = (JSONArray) json.get("resources");
		else {
			System.out.println("No services at " + p.getName() + " were found.");
			return;
		}

		for (int i = 0; i < servicesJson.length(); i++) { // this "page"
			// armazenar documentos (servicos) no NoSql (CouchDB/Cloudant) desta plataforma.
						
			JSONObject jsonService = servicesJson.getJSONObject(i);
			JSONObject jsonServiceMetadata = jsonService.getJSONObject("metadata");
			JSONObject jsonServiceEntity = jsonService.getJSONObject("entity");

			ServiceDocument aService = new ServiceDocument();
			//aService.setSource("manualTesting");

			// *** Metadata ***
			aService.setGuid(jsonServiceMetadata.getString("guid"));
			aService.setUrl(jsonServiceMetadata.getString("url"));
			aService.setCreatedAt(jsonServiceMetadata.getString("created_at"));
			if (jsonServiceMetadata.has("updated_at") && !jsonServiceMetadata.isNull("updated_at"))
				aService.setUpdatedAt(jsonServiceMetadata.getString("updated_at"));

			// *** Entity ***
			if (jsonServiceEntity.has("label") && !jsonServiceEntity.isNull("label"))
				aService.setLabel(jsonServiceEntity.getString("label"));

			if (jsonServiceEntity.has("url") && !jsonServiceEntity.isNull("url"))
				aService.setEntityUrl(jsonServiceEntity.getString("url"));
			if (jsonServiceEntity.has("description") && !jsonServiceEntity.isNull("description"))
				aService.setDescription(jsonServiceEntity.getString("description"));
			if (jsonServiceEntity.has("long_description") && !jsonServiceEntity.isNull("long_description"))
				aService.setLongDescription(jsonServiceEntity.getString("long_description"));
			if (jsonServiceEntity.has("version") && !jsonServiceEntity.isNull("version"))
				aService.setVersion(jsonServiceEntity.getString("version"));
			if (jsonServiceEntity.has("info_url") && !jsonServiceEntity.isNull("info_url"))
				aService.setInfoUrl(jsonServiceEntity.getString("info_url"));
			if (jsonServiceEntity.has("active") && !jsonServiceEntity.isNull("active"))
				aService.setActive(jsonServiceEntity.getBoolean("active"));
			if (jsonServiceEntity.has("bindable") && !jsonServiceEntity.isNull("bindable"))
				aService.setBindable(jsonServiceEntity.getBoolean("bindable"));

			if (jsonServiceEntity.has("extra") && !jsonServiceEntity.isNull("extra"))
				aService.setExtra(jsonServiceEntity.getString("extra"));
			// Tags -------------------------------------------------------
			/*
			 * "tags": [ "mysql", "relational", "data_management",
			 * "ibm_experimental" ],
			 */
			if (jsonServiceEntity.has("tags") && !jsonServiceEntity.isNull("tags")) {
				JSONArray tagsArray = (JSONArray) jsonServiceEntity.get("tags");
				String tagsAsString = tagsArray.toString().replaceAll("[\\[\\]\"]", "");// [<specific
																						// characters>]
				String[] tagsStringArray = tagsAsString.split(",");
				aService.setTags(tagsStringArray);
			}
			if (jsonServiceEntity.has("requires") && !jsonServiceEntity.isNull("requires")) {
				JSONArray requiresArray = (JSONArray) jsonServiceEntity.get("requires");
				String requiresAsString = requiresArray.toString().replaceAll("[\\[\\]\"]", "");// [<specific
																								// characters>]
				String[] requiresStringArray = requiresAsString.split(",");
				aService.setRequires(requiresStringArray);
			}
			if (jsonServiceEntity.has("documentation_url") && !jsonServiceEntity.isNull("documentation_url"))
				aService.setExtra(jsonServiceEntity.getString("documentation_url"));

			if (jsonServiceEntity.has("plan_updateable") && !jsonServiceEntity.isNull("plan_updateable"))
				aService.setPlanUpdateable(jsonServiceEntity.getBoolean("plan_updateable"));
			if (jsonServiceEntity.has("service_plans_url") && !jsonServiceEntity.isNull("service_plans_url"))
				aService.setServicePlansUrl(jsonServiceEntity.getString("service_plans_url"));

			// aService.setFullEntity(jsonServiceEntity.toString());

			System.out.println("A service at " + p.getName() + " was found: " + aService.getLabel() + " - "
					+ aService.getDescription());
			
			// Persistir no NoSql ----------------------------------------
			ServiceNoSqlDao noSqlDao = new ServiceNoSqlDao(p.getDbName());
			//noSqlDao.setDbName(p.getDbName());

			System.out.println("Salvando o documento no banco de dados NOSQL...");
			String id = noSqlDao.save(aService);
			System.out.println("Salvo com o document ID:" + id);
			
			// atualizar o banco relacional diariamente (microserviços.). ----------
			// deixar pra um 2o momento. -> Ler os documentos de serviço do NOSQL e refletir no RELACIONAL. 1x por semana.			
		}
	}

}
