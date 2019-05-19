package test.serviceAndServicePlan;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cloudant.NoSqlDao;
import cloudant.dao.ServiceNoSqlDao;
import cloudant.model.ServiceDocument;
import mysqlDB.model.Platform;
import mysqlDB.model.PlatformInformation;
import mysqlDB.model.Service;
import mysqlDB.model.ServiceTag;
import test.AbstractTest;
import utils.LogUtils;

public class ServiceSyncTest extends AbstractTest {

	public static void main(String[] args) {
		try {
			start();

			
			Platform p = new Platform().findById(11);
			System.out.println("\n***\n[Plat]: " + p.getName() + " [Plat] Id: " + p.getId());
			ServiceNoSqlDao serviceNoSqlDao = new ServiceNoSqlDao(p.getDbName());
			
			List<ServiceDocument> services = serviceNoSqlDao.getActiveServiceOnDate("20180827");
			
			for (ServiceDocument aService : services) {
				//System.out.println("Um serviço do NoSql: " + aService.getLabel());
				
				//if (!aService.getGuid().equals("5d647166-56cc-41a6-a660-53219a05f3d7")) // "APIConnect"
				//	continue;
				
				String requires = Arrays.toString(aService.getRequires());
				//System.out.println("Requires 1: " + requires);
				requires = requires.substring(1, requires.length() - 1); 
				//System.out.println("Requires 2: " + requires);
				
				if (requires.equals(""))
					continue;
				
				// Verificar se existe no banco relacional...
				Service service = Service.findFirstByPlatformAndGuid(p.getId(), aService.getGuid());

				if (service == null) {
					// senão, incluir.
					System.out.println("Serviço NÃO encontrado no banco relacional...");
					
				} else {
					System.out.println("Serviço id [" + service.getId() + "] ja existe no banco relacional, atualizar...");
					
					//service.setPlatform(p);
					//service.setName(aService.getLabel());
					//service.setGuid(aService.getGuid());
					
					System.out.println("\n************************************");
					System.out.println("Requires: " + requires); //Arrays.toString(aService.getRequires()));
					System.out.println("Size: " + requires.split(",").length);//Arrays.toString(aService.getRequires()).split(",").length);					
					System.out.println("************************************\n");
					
					
					
					/*
					service.setRequires(Arrays.toString(aService.getRequires()));
					
					service.setSyncAt(new Date()); // dt_sync
					service.setActive(aService.isActive());
					service.update();
					
					Service.deactivateInexistentServices();
					*/
					
					/*
					// update and persist info for today.
			        LogUtils.logTrace("Update and persist sync info for today...");        
			        PlatformInformation info = new PlatformInformation();
			        info = info.findById(p.getInformation().getId());
			        info.setServicesSyncDate(new Date()); // today
			        info.update();					
					 */
					
					
					/*
					service.setActive(aService.isActive());							
					service.setBindable(aService.isBindable());
					service.setSyncAt(new Date()); // today
					service.setDescription(aService.getDescription());
					service.setDocumentationUrl(aService.getDocumentationUrl());
					
					service.setInformationUrl(aService.getInfoUrl());
					service.setLongDescription(aService.getLongDescription());							
					service.setPlanUpdateable(aService.isPlanUpdateable());
					service.setServicePlansUrl(aService.getServicePlansUrl());
					service.setUrl(aService.getUrl());
					service.setVersion(aService.getVersion());
					service.setUpdated(NoSqlDao.getDate(aService.getUpdatedAt()));
					service.setCreated(NoSqlDao.getDate(aService.getCreatedAt()));
					service.setAdditionalInformation(aService.getExtra()); // 255
					
								
					service.save();
					
					// limpar tags somente se o número for diferente (premissa)
					if (service.getTags().size() != aService.getTags().length) {
						System.out.println("Removendo " + service.getTags().size() +  " tags do banco relacional...");
						service.clearTags();
						
						// incluir tags
						for (String aTag: aService.getTags()) {
							ServiceTag tag = new ServiceTag();
							tag.setService(service);
							tag.setName(aTag);
							System.out.println("Reincluindo uma tag no banco relacional...");
							tag.save();
						}
					}	
					*/						
				}
			}

			
		} catch (Exception ex) {
			System.out.println("Erro: " + ex.getMessage());
			ex.printStackTrace();
			
		} finally {
			end();
		}

		/*
		 * // ler do NoSql - todos os serviços de hoje List<ServiceDocument>
		 * services = listAvailableServices("20180520", "sap");
		 * 
		 * // atualizar todos os serviços (encontrados) no banco relacional
		 * //TODO: REFAZER CLASSE ORM SERVICO // COMO LOCALIZAR UM SERVIÇO A
		 * PARTIR DA PLATAFORMA (id_platform) E DO LABEL (nm_label)? Platform
		 * dao = new Platform(); dao.setupEntityManager();
		 * //System.out.println(dao.findExample().getDbName()); dao.dispose();
		 */
	}

	
	/*
	 * 
	 * Platform platformDao = new Platform();
			ServiceNoSqlDao serviceNoSqlDao;

			// para cada uma das plataformas ativas...
			List<Platform> platforms = platformDao.findActive();
			if (platforms != null)
				for (Platform p : platforms) {
					System.out.println("\n***\n[Plat]: " + p.getName() + " [Plat] Id: " + p.getId());

					serviceNoSqlDao = new ServiceNoSqlDao(p.getDbName());
					Date hoje = new Date();
					// hoje.setMonth(5);
					// hoje.setDate(2);

					
					// verificar se está na hora de atualizar os serviços,,,
					if (!p.getInformation().isServiceInformationOutOfDate()) {
						System.out.println("Esta plataforma está atualizada, passando para a próxima...");
						continue;
					} else {
						p.getInformation().setServicesSyncDate(hoje);
						p.getInformation().save();
					}
					
					// Salvar/Sincronizar os serviços...				

					// ler todos os serviços disponíveis HOJE para esta
					// plataforma a partir do NoSql.
					String timestamp = NoSqlDao.getTimestamp(hoje);
					//System.out.println("Hoje: " + timestamp);

					List<ServiceDocument> services = serviceNoSqlDao.getActiveServiceOnDate(timestamp);

					for (ServiceDocument aService : services) {
						// Para cada serviço, verificar se existe e
						// adicionar/incluir...
						System.out.println("Um serviço do NoSql: " + aService.getLabel());

						// Verificar se existe no banco relacional...
						Service service = Service.findFirstByPlatformAndLabel(p.getId(), aService.getLabel());

						if (service == null) {
							// senão, incluir.
							System.out.println("Incluindo serviço no banco relacional...");
							
							service = new Service();
							service.setPlatform(p);
							service.setName(aService.getLabel());
							
							service.setActive(aService.isActive());							
							service.setBindable(aService.isBindable());
							service.setSyncAt(new Date()); // today
							service.setDescription(aService.getDescription());
							service.setDocumentationUrl(aService.getDocumentationUrl());
							service.setGuid(aService.getGuid());
							service.setInformationUrl(aService.getInfoUrl());
							service.setLongDescription(aService.getLongDescription());							
							service.setPlanUpdateable(aService.isPlanUpdateable());							
							service.setServicePlansUrl(aService.getServicePlansUrl());
							service.setUrl(aService.getUrl());
							service.setVersion(aService.getVersion());
							service.setUpdated(NoSqlDao.getDate(aService.getUpdatedAt()));
							service.setCreated(NoSqlDao.getDate(aService.getCreatedAt()));
							service.setAdditionalInformation(aService.getExtra()); // 255

							// TODO: incluir quantidade de planos
							// service.setServicePlans(1L);
							
							service.save();
							
							// incluir tags
							for (String aTag: aService.getTags()) {
								ServiceTag tag = new ServiceTag();
								tag.setService(service);
								tag.setName(aTag);
								System.out.println("Incluindo uma tag no banco relacional...");
								tag.save();
							}

						} else {
							System.out.println("Serviço id [" + service.getId() + "] ja existe no banco relacional, atualizar...");
							
							//service.setPlatform(p);
							//service.setName(aService.getLabel());
							
							service.setActive(aService.isActive());							
							service.setBindable(aService.isBindable());
							service.setSyncAt(new Date()); // today
							service.setDescription(aService.getDescription());
							service.setDocumentationUrl(aService.getDocumentationUrl());
							service.setGuid(aService.getGuid());
							service.setInformationUrl(aService.getInfoUrl());
							service.setLongDescription(aService.getLongDescription());							
							service.setPlanUpdateable(aService.isPlanUpdateable());
							service.setServicePlansUrl(aService.getServicePlansUrl());
							service.setUrl(aService.getUrl());
							service.setVersion(aService.getVersion());
							service.setUpdated(NoSqlDao.getDate(aService.getUpdatedAt()));
							service.setCreated(NoSqlDao.getDate(aService.getCreatedAt()));
							service.setAdditionalInformation(aService.getExtra()); // 255
							
							// TODO: incluir quantidade de planos
							// service.setServicePlans(1L);
							
							service.save();
							
							// limpar tags somente se o número for diferente (premissa)
							if (service.getTags().size() != aService.getTags().length) {
								System.out.println("Removendo " + service.getTags().size() +  " tags do banco relacional...");
								service.clearTags();
								
								// incluir tags
								for (String aTag: aService.getTags()) {
									ServiceTag tag = new ServiceTag();
									tag.setService(service);
									tag.setName(aTag);
									System.out.println("Reincluindo uma tag no banco relacional...");
									tag.save();
								}
							}							
						}
					}
				}*/
					/*
					 * daoCFService = new CFService(p.getEndpoint());
					 * 
					 * String json = daoCFService.getFirstJson();
					 * prettyPrintServicesSummary(p, json); //
					 * System.out.println("JSON:" + json);
					 * 
					 * prettyPrintServices(p, json);
					 * 
					 * while (!daoCFService.getNextUrl().equals("")) { json =
					 * daoCFService.getNextJson(); //
					 * System.out.println("JSON (next): " + json);
					 * prettyPrintServices(p, json); }
					 */
				
	
	
	
	private static List<ServiceDocument> listAvailableServices(String timestamp, String dbname) {
		ServiceNoSqlDao noSqlDao = new ServiceNoSqlDao(dbname);
		List<ServiceDocument> services = noSqlDao.getActiveServiceOnDate(timestamp);

		System.out.println("Number of available services (service documents) on the date ==> " + services.size()
				+ System.getProperty("line.separator"));
		System.out.println("================================================================"
				+ System.getProperty("line.separator"));

		for (ServiceDocument s : services) {
			System.out.println("[" + s.getLabel() + " service was available on: " + timestamp + "]");
			System.out.println(">> Guid: " + s.getGuid());
			System.out.println(">> Source: " + s.getSource());
			System.out.println(">> Created At: " + s.getCreatedAt());
			System.out.println(">> Updated At: " + s.getUpdatedAt());
			System.out.println(">> Tags: " + Arrays.toString(s.getTags()));

			System.out.println("----------------------------------------------------------------");
		}

		return services;
	}

}
