package cloudant.model;

public class ServiceDocument extends NoSqlAbstractEntity {
	private static final long serialVersionUID = 4L;
	private static final String DOCUMENT_TYPE = "SERVICE";
	
	//metadata
	private String guid;
	private String url;
	private String createdAt;
	private String updatedAt;
	
	//entity
	private String label;

	private String entityUrl; //v4
	private String description;
	private String longDescription; //v4
	private String version; //v4
	private String infoUrl; //v4	
	private boolean active;
	private boolean bindable;
	
	private String extra; //v4
	private String[] tags;
	private String[] requires; //v4
	private String documentationUrl; //v4
		
	private boolean planUpdateable; //v4
	private String servicePlansUrl; //v4
	//private String fullEntity; //v4

	public ServiceDocument () {
		super(); // just for legibility
		
		this.setDoctype(ServiceDocument.DOCUMENT_TYPE);
		this.setDocversion(ServiceDocument.serialVersionUID);		
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isBindable() {
		return bindable;
	}

	public void setBindable(boolean bindable) {
		this.bindable = bindable;
	}
	
	/*
	public String getFullEntity() {
		return fullEntity;
	}

	public void setFullEntity(String entity) {
		this.fullEntity = entity;
	}
	*/	

	public String getEntityUrl() {
		return entityUrl;
	}

	public void setEntityUrl(String entityUrl) {
		this.entityUrl = entityUrl;
	}
	
	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}
	
	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	public String[] getRequires() {
		return requires;
	}

	public void setRequires(String[] requires) {
		this.requires = requires;
	}
	
	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	public boolean isPlanUpdateable() {
		return planUpdateable;
	}

	public void setPlanUpdateable(boolean planUpdateable) {
		this.planUpdateable = planUpdateable;
	}

	public String getServicePlansUrl() {
		return servicePlansUrl;
	}

	public void setServicePlansUrl(String servicePlansUrl) {
		this.servicePlansUrl = servicePlansUrl;
	}

}
