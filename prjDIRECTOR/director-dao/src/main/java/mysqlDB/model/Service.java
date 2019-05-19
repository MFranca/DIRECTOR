package mysqlDB.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import mysqlDB.AbstractEntity;
import utils.LogUtils;

@Entity(name = "Service")
@Table(name = "TB_USAAS")
public class Service extends AbstractEntity <Service> {
	private static final long serialVersionUID = 1L;
	
	private static final String QUERY_SERVICE_CANDIDATES_POSITIVE_FILTER_ONLY = 
			"Select DISTINCT S.id, S.nm_label "
			+ "From TB_USAAS S, TB_TAG T "
			+ "Where S.id = T.id_microservice "
			+ "And T.nm_feature in ({1}) "			
			+ "Order by S.nm_label";
	
	private static final String QUERY_SERVICE_CANDIDATES = 
			"Select DISTINCT S.id, S.nm_label "
			+ "From TB_USAAS S, TB_TAG T "
			+ "Where S.id = T.id_microservice "
			+ "And T.nm_feature in ({1}) "
			+ "And S.id not in ("
				+ "Select S.id "
				+ "From TB_USAAS S, TB_TAG T "
				+ "Where S.id = T.id_microservice "
				+ "And T.nm_feature in ({2})) "
			+ "Order by S.nm_label";
	
	@ManyToOne(fetch = FetchType.EAGER) //LAZY
    @JoinColumn(name = "id_platform")
	private Platform platform;

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)	
	private List<ServiceTag> tags;
	
	@Column(name = "ds_guid", nullable = false, length = 80)
	private String guid;
	
	@Column(name = "ds_url", nullable = false, length = 200)
	private String url;
	
	@Temporal(TemporalType.DATE) 
	@Column(name = "dt_created", nullable = true)
	private Date created;
		
	@Temporal(TemporalType.DATE) 
	@Column(name = "dt_updated", nullable = true)
	private Date updated;
	
	@Column(name = "nm_label", nullable = false, length = 100)
	private String name; // label
	
	@Column(name = "ds_description", nullable = true, length = 200)
	private String description;
	
	@Column(name = "ds_long_description", nullable = true, length = 255)
	private String longDescription;
	
	@Column(name = "ds_version", nullable = true, length = 40)
	private String version;
	
	@Column(name = "ds_info_url", nullable = true, length = 200)
	private String informationUrl;
	
	@Column(name = "ds_documentation_url", nullable = true, length = 200)
	private String documentationUrl;
	
	@Column(name = "st_active", nullable = false) // columnDefinition="TINYINT(1) default '0'"
	private boolean active = false;
	
	@Column(name = "st_bindable", nullable = true)
	private boolean bindable;
		
	@Column(name = "st_plan_updateable", nullable = true)
	private boolean planUpdateable;
	
	@Column(name = "ds_extra", nullable = true, length = 255)
	private String additionalInformation; // extra
	
	@Column(name = "ds_service_plans_url", nullable = true, length = 200)
	private String servicePlansUrl; 
	
	@Column(name = "qt_service_plans", nullable = false) // columnDefinition="INT default '1'"
	private Long servicePlansQuantity = 1L;
		
	@Temporal(TemporalType.DATE) 
	@Column(name = "dt_sync", nullable = false, columnDefinition = "DATETIME")
	private Date sync; // date of this information
	
	@Column(name = "ds_requires", nullable = true, length = 255)
	private String requires;
	
	@Column(name = "st_has_free_plan", nullable = false) // columnDefinition="TINYINT(1) default '0'"
	private boolean free = false;
	
	@Column(name = "st_has_public_plan", nullable = false) // columnDefinition="TINYINT(1) default '0'"
	private boolean publicPlan = false;

	// ------------------------------- METHODS -----------------------------------------------
	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = StringUtils.left(description, 200);
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = StringUtils.left(additionalInformation, 255);
	}

	public Date getSyncAt() {
		return sync;
	}

	public void setSyncAt(Date syncAt) {
		this.sync = syncAt;
	}
	
	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = StringUtils.left(longDescription, 255);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getInformationUrl() {
		return informationUrl;
	}

	public void setInformationUrl(String informationUrl) {
		this.informationUrl = informationUrl;
	}

	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}
	
	public String getServicePlansUrl() {
		return servicePlansUrl;
	}

	public void setServicePlansUrl(String servicePlansUrl) {
		this.servicePlansUrl = servicePlansUrl;
	}

	public Long getServicePlansQuantity() {
		return servicePlansQuantity;
	}

	public void setServicePlansQuantity(Long servicePlansQuantity) {
		this.servicePlansQuantity = servicePlansQuantity;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isBindable() {
		return bindable;
	}

	public void setBindable(boolean bindable) {
		this.bindable = bindable;
	}

	public boolean isPlanUpdateable() {
		return planUpdateable;
	}

	public void setPlanUpdateable(boolean planUpdateable) {
		this.planUpdateable = planUpdateable;
	}
	
	public List<ServiceTag> getTags() {
		return tags;
	}

	public void setTags(List<ServiceTag> tags) {
		this.tags = tags;
	}

	public String getRequires() {
		return requires;
	}

	public void setRequires(String requires) {
		this.requires = requires;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public boolean isPublic() {
		return publicPlan;
	}

	public void setPublic(boolean publicPlan) {
		this.publicPlan = publicPlan;
	}
	
	// ================================================================================
	
	public static Service findFirstByPlatformAndLabel(Long platformId, String label) {
		String queryString = "SELECT s FROM Platform p JOIN p.services s Where p.id=" + platformId + " AND s.name='" + label + "'";
		//"SELECT s FROM Service s WHERE s.id_platform=" + platformId + " AND s.label='" + label + "'"
		
		TypedQuery<Service> query = 
				entityManager.createQuery(queryString, Service.class);
		
		List<Service> result = findList(query);
		if (result.isEmpty())
			return null;
		else 
			return result.get(0);
	}	
	
	public static Service findFirstByPlatformAndGuid(Long platformId, String guid) {
		String queryString = "SELECT s FROM Platform p JOIN p.services s Where p.id=" + platformId + " AND s.guid='" + guid + "'";
		
		TypedQuery<Service> query = 
				entityManager.createQuery(queryString, Service.class);
		
		List<Service> result = findList(query);
		if (result.isEmpty())
			return null;
		else 
			return result.get(0);
	}
	
	public void clearTags() {
		if (this.getTags().isEmpty())
			return;
		
		String queryString = "DELETE FROM Tag t WHERE t.service.id = :serviceId";
		
		executeDeleteUpdate(queryString, "serviceId", this.getId());
		
		/*
		entityManager.flush();
		entityManager.clear();		
		 
		Query q = entityManager.createQuery("DELETE ServiceTag st WHERE st.Servicer.id = :serviceId");
		q.setParameter("serviceId", this.getId());
		q.executeUpdate();
		 
		order = em.find(PurchaseOrder.class, orderId);
		em.remove(order);
		*/
	} 
	
	public static void deactivateInexistentServices() {
		String methodName = "inactivateService";
		
		//https://www.objectdb.com/java/jpa/query/jpql/update
		/*
		 Query query = em.createQuery(
			      "UPDATE Country SET population = population * 11 / 10 " +
			      "WHERE c.population < :p");
			  int updateCount = query.setParameter(p, 100000).executeUpdate();
		 */
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dateWithoutTime;		
			dateWithoutTime = sdf.parse(sdf.format(new Date()));
		
			//If we didn't find the service in the platform, anymore, then it is inactive...
			String queryString = "UPDATE Service s "
					+ "SET s.active = false "
					+ "WHERE s.active = true "
					+ "AND s.sync < :today"; //updatedAt		
			
			int rowsAffected = executeDeleteUpdate(queryString, "today", dateWithoutTime);
			//LogUtils.logTrace("SQL: " + queryString + " - result: " + rowsAffected);
		
		} catch (ParseException ex) {
			LogUtils.logError(methodName, ex.getMessage());
		}
	}
	
	// return the id and the label of the service candidates...
	public static Map<String, String> findCandidates(List<String> positiveFilter, List<String> negativeFilter) {
		String methodName = "findCandidates";
		
		String query;
		
		//{1} "dbaas", "nosql", "data store", "data stores", "dba"
		//String parameter1 = String.join(",", positiveFilter);
		String parameter1 = String.join(", ", positiveFilter
				.stream()
	            .map(name -> ("'" + name.trim() + "'"))
	            .collect(Collectors.toList()));		
		
		//{2} "ibm_deprecated", "key-value", "caching"
		String parameter2 = String.join(", ", negativeFilter
				.stream()
	            .map(name -> ("'" + name.trim() + "'"))
	            .collect(Collectors.toList()));
		
		Map<String, String> candidates = new HashMap<String, String>();
		
		if (negativeFilter.size() == 0)
			query = QUERY_SERVICE_CANDIDATES_POSITIVE_FILTER_ONLY.replace("{1}", parameter1);
		else
			query = QUERY_SERVICE_CANDIDATES.replace("{1}", parameter1).replace("{2}", parameter2);
		
		try {
			List<Object[]> result = executeQuery(query);
			
			for (Object[] t : result) {
				candidates.put(String.valueOf(t[0]), String.valueOf(t[1]));
			}
			
		} catch (Exception ex) {
			LogUtils.logError(methodName, ex.getMessage());			
		} 
		
		return candidates;		
	}
}
