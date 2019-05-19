package mysqlDB.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import mysqlDB.AbstractEntity;
import utils.LogUtils;

@Entity(name = "Information")
@Table(name = "TB_INFO")
public class PlatformInformation extends AbstractEntity <PlatformInformation> {
	private static final long serialVersionUID = 1L;

	public static final long DAYS_FOR_UPDATING_INFORMATION = 20;
	public static final long DAYS_FOR_UPDATING_SERVICE_INFORMATION = 10;
	public static final long DAYS_FOR_UPDATING_SERVICE_PLAN_INFORMATION = 5;
		
	/*
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	*/

	//@OneToOne(targetEntity = Platform.class)
	//@JoinColumn(name = "id_platform", nullable = false)
	@OneToOne(fetch = FetchType.EAGER)//LAZY
    @JoinColumn(name = "id_platform")
	private Platform platform;
	
	@Column(name = "ds_auth_endpoint", nullable = false, length = 255)
	private String authorizationEndpoint;
		
	@Column(name = "ds_document", nullable = true, length = 255)
	private String document;
	
	//`qt_usaas` INT NOT NULL DEFAULT 0 COMMENT
	@Column(name = "qt_usaas", nullable = false) //, columnDefinition="INT default '0'"
	private Long servicesQuantity = 0L; // of microservices on this platform
	
	//`qt_days_mon` INT NOT NULL DEFAULT 0 
	@Column(name = "qt_days_mon", nullable = false) 
	private Long days = 0L; // this platform is being monitored
			
	@Temporal(TemporalType.DATE) //@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_sync", nullable = true)
	private Date syncDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "dt_sync_services", nullable = true)
	private Date servicesSyncDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "dt_sync_service_plans", nullable = true)
	private Date servicePlansSyncDate;
	
	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public Date getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}
	
	public Date getServicesSyncDate() {
		return servicesSyncDate;
	}

	public void setServicesSyncDate(Date servicesSyncDate) {
		this.servicesSyncDate = servicesSyncDate;
	}

	public String getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}

	public void setAuthorizationEndpoint(String authorizationEndpoint) {
		this.authorizationEndpoint = authorizationEndpoint;
	}

	public Long getServicesQuantity() {
		return servicesQuantity;
	}

	public void setServicesQuantity(Long quantity) {
		this.servicesQuantity = quantity;
	}
	
	public Long getRunningDays() {
		return days;
	}

	public void setRunningDays(Long days) {
		this.days = days;
	}
	
	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}
	
	public Date getServicePlansSyncDate() {
		return servicePlansSyncDate;
	}

	public void setServicePlansSyncDate(Date servicePlansSyncDate) {
		this.servicePlansSyncDate = servicePlansSyncDate;
	}
	
	// ================================================
	public boolean isPlatformInformationOutOfDate() {	                	
    	Date today = null;
    	Date informationDate = this.getSyncDate();
    	
		try {
			if (informationDate == null)
	    		return true;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			today = sdf.parse(sdf.format(new Date()));
		
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}                	
    	                	
    	long elapsedDays =TimeUnit.DAYS.convert(today.getTime() - informationDate.getTime(), TimeUnit.MILLISECONDS);
        LogUtils.logTrace("Elapsed days since last sync: " + elapsedDays);
        
        return (elapsedDays > DAYS_FOR_UPDATING_INFORMATION);
	}
	
	public boolean isServiceInformationOutOfDate() {	                	
    	Date today = null;
    	Date informationDate = this.getServicesSyncDate();
    	
		try {
			if (informationDate==null)
				return true;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			today = sdf.parse(sdf.format(new Date()));
		
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}                	
    	                	
    	long elapsedDays =TimeUnit.DAYS.convert(today.getTime() - informationDate.getTime(), TimeUnit.MILLISECONDS);
    	LogUtils.logTrace("Elapsed days since last sync: " + elapsedDays);
    	
        return (elapsedDays > DAYS_FOR_UPDATING_SERVICE_INFORMATION);
	}
	
	public boolean isServicePlanInformationOutOfDate() {	                	
    	Date today = null;
    	Date informationDate = this.getServicePlansSyncDate();
    	
		try {
			if (informationDate==null)
				return true;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			today = sdf.parse(sdf.format(new Date()));
		
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}                	
    	                	
    	long elapsedDays =TimeUnit.DAYS.convert(today.getTime() - informationDate.getTime(), TimeUnit.MILLISECONDS);
    	LogUtils.logTrace("Elapsed days since last sync: " + elapsedDays);
    	
        return (elapsedDays > DAYS_FOR_UPDATING_SERVICE_PLAN_INFORMATION);
	}	
}