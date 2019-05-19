package mysqlDB;

import java.io.Serializable;
import java.util.List;

public interface MySqlCrudServices <T> { //, PK extends Serializable
	// Ref.: https://stackoverflow.com/questions/3888575/single-dao-generic-crud-methods-jpa-hibernate-spring
	public Long getId();
	public void setId(Long id);
	
	public T create(T t);
	//public T update(T t);
	public void update();
	public void delete(T t);
	
	public List<T> findAll();	
	public T findById (long id) throws Exception;
}