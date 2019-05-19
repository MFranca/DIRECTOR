package mysqlDB.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import mysqlDB.AbstractEntity;

@Entity(name = "Platform")
@Table(name = "TB_PAAS")
public class Platform extends AbstractEntity <Platform> {
	private static final long serialVersionUID = 1L;
	
	private static final String QUERY_ACTIVE_PLATFORMS = 
			"SELECT P "
			+ "FROM Platform P "
			+ "WHERE P.status='A'";
	
	private static final String QUERY_ACTIVE_PLATFORM_SERVICES = 
			"Select S "
			+ "From Platform P JOIN P.services S " // case sensitive					
			+ "Where S.active = true " 
			+ "And P.id = {1}";
	
	/*
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	*/

	//@OneToOne(fetch=FetchType.EAGER) // LAZY (not cascaded), the default is EAGER
	@OneToOne(mappedBy = "platform", cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true) // optional = false	
	private PlatformInformation information;
	
	@OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, fetch = FetchType.LAZY)	
	private List<Service> services;
	
	@Column(name = "nm_platform", nullable = false, unique = true, length = 60)
	private String name;

	@Column(name = "ds_platform", length = 255)
	private String description;

	@Column(name = "ds_endpoint_api", nullable = false, unique = true, length = 100)
	private String endpoint;

	@Column(name = "nm_username", nullable = false, length = 45)
	private String username;

	@Column(name = "nm_password", nullable = false, length = 45)
	private String password;

	@Column(name = "nm_client_id", length = 45)
	private String clientId;

	@Column(name = "nm_client_secret", length = 45)
	private String clientSecret;

	@Column(name = "ds_obs", length = 200)
	private String observation;

	@Column(name = "st_platform", nullable = false, length = 1)
	private String status;
	
	@Column(name = "nm_nosql_db", length = 20)
	private String dbName;
	
	/*
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	*/	

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
		this.description = description;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public PlatformInformation getInformation() {
		return information;
	}

	public void setInformation(PlatformInformation information) {
		this.information = information;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}	
	
	public List<Service> getServices() {
		return services;
	}
	

	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	public static List<Platform> findActive() {	
		TypedQuery<Platform> query = entityManager.createQuery(QUERY_ACTIVE_PLATFORMS, Platform.class);
				
		return findList(query);
	}
	
	public List<Service> findActiveServices() {		
		TypedQuery<Service> query = entityManager.createQuery(QUERY_ACTIVE_PLATFORM_SERVICES.replace("{1}", this.getId().toString()), Service.class);
		
		return findList(query);
	}	
}