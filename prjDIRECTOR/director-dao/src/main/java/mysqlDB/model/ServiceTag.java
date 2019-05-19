package mysqlDB.model;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import mysqlDB.AbstractEntity;
import utils.LogUtils;

@Entity(name = "Tag")
@Table(name = "TB_TAG")
public class ServiceTag extends AbstractEntity <ServiceTag> {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_microservice")
	private Service service;

	@Column(name = "nm_feature", nullable = false, length = 200)
	private String name; // tag
	
	// ------------------------------- METHODS -----------------------------------------------
	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	public static Map<String, Long> getAllTagsWithCount() {
		String methodName = "getAllTags";
		//https://stackoverflow.com/questions/12716779/multiple-column-structure-java
		Map<String, Long> tags = new HashMap<String, Long>();
		String query = "Select nm_feature, Count(*) "
				+ "From TB_TAG "
				+ "Group by nm_feature "
				+ "Order by nm_feature";
		
		try {
			List<Object[]> result = executeQuery(query);
			
			for (Object[] t : result) {
				tags.put(String.valueOf(t[0]), Long.parseLong(String.valueOf(t[1])));
			}
			
		} catch (Exception ex) {
			LogUtils.logError(methodName, ex.getMessage());
			
		} 
		
		return tags;
	}
}
