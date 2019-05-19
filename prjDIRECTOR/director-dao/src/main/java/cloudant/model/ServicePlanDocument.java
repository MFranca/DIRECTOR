package cloudant.model;

public class ServicePlanDocument extends NoSqlAbstractEntity {
	private static final long serialVersionUID = 2L;
	private static final String DOCUMENT_TYPE = "SERVICE_PLAN";
	
	// *** METADATA ***
	private String guid;
	private String url;
	private String createdAt;
	private String updatedAt;
	
	// *** ENTITY ***
	private String name;
	private boolean free;
	private String description;
	private String serviceGuid;
	private String extra; // v2

	private boolean publicPlan; //v2
	private boolean bindable; //v2
	private boolean active;
	
	private String serviceInstancesUrl;//v2
	
	//private String fullEntity;
	
	public ServicePlanDocument () {
		super(); // just for legibility
		
		this.setDoctype(ServicePlanDocument.DOCUMENT_TYPE);
		this.setDocversion(ServicePlanDocument.serialVersionUID);		
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

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	public String getFullEntity() {
		return fullEntity;
	}

	public void setFullEntity(String fullEntity) {
		this.fullEntity = fullEntity;
	}
	*/

	public String getServiceGuid() {
		return serviceGuid;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public boolean isPublicPlan() {
		return publicPlan;
	}

	public void setPublicPlan(boolean publicPlan) {
		this.publicPlan = publicPlan;
	}

	public boolean isBindable() {
		return bindable;
	}

	public void setBindable(boolean bindable) {
		this.bindable = bindable;
	}

	public String getServiceInstancesUrl() {
		return serviceInstancesUrl;
	}

	public void setServiceInstancesUrl(String serviceInstancesUrl) {
		this.serviceInstancesUrl = serviceInstancesUrl;
	}

	public void setServiceGuid(String serviceGuid) {
		this.serviceGuid = serviceGuid;
	}		
}
