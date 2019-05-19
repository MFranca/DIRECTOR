package mysqlDB;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import utils.LogUtils;

@MappedSuperclass
public abstract class AbstractEntity<T> implements MySqlCrudServices<T>, Serializable {
	// Ref.: https://stackoverflow.com/questions/3888575/single-dao-generic-crud-methods-jpa-hibernate-spring
	public static final String PERSISTENCE_UNIT = "AuroraDB PU";
	
	@Transient
	protected Class<T> entityClass;	
	
	@Transient
	private static final long serialVersionUID = 1L;

	// This you might want to get injected by the container.
	@Transient
	private static EntityManagerFactory ENTITY_MANAGER_FACTORY; //final 	
	@Transient
	protected static EntityManager entityManager;
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	// Constructor
	@SuppressWarnings("unchecked")
	public AbstractEntity() {
		entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		//entityManager = getEntityManagerFactory().createEntityManager();
	}

	// DAO
	public static EntityManagerFactory getEntityManagerFactory() {
		// Create an EntityManagerFactory if one does not exist...
		if (ENTITY_MANAGER_FACTORY == null)
			ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);

		return ENTITY_MANAGER_FACTORY;
	}
		
	public static void setEntityManager(EntityManager em) {
		entityManager = em;
	}
	
	public static void setupEntityManager() {		
		if (entityManager == null) {			
			entityManager = getEntityManagerFactory().createEntityManager();
			LogUtils.logTrace("\nA new (static) entityManager (& Factory) has been created using " + PERSISTENCE_UNIT + "...\n");
		}
	}
	
	public static void dispose() {
		if (entityManager != null) {	
			entityManager.close();
			entityManager = null;
			LogUtils.logTrace("\nThe (static) entityManager (& Factory) has been closed...\n");
		}

		// NEVER FORGET TO CLOSE THE ENTITY_MANAGER_FACTORY
		if (ENTITY_MANAGER_FACTORY!= null && ENTITY_MANAGER_FACTORY.isOpen() ) {
			ENTITY_MANAGER_FACTORY.close();
			ENTITY_MANAGER_FACTORY = null;
			//System.out.println("The (static) ENTITY_MANAGER_FACTORY has been closed...\n\n");
		}
	}

	//private static void closeEntityManagerFactory() {	}

	@Override
	public String toString() {
		return "A " + this.getClass().getCanonicalName() + " object with id: " + getId();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/*
	 * @SuppressWarnings("unchecked") public List<E> findAll() { List<E>
	 * entities = null;
	 * 
	 * // Create an EntityManager entityManager =
	 * getEntityManagerFactory().createEntityManager(); EntityTransaction
	 * transaction = null;
	 * 
	 * try { // Get a transaction transaction = entityManager.getTransaction();
	 * // Begin the transaction transaction.begin();
	 * 
	 * // Get a List of objects... Query q =
	 * entityManager.createQuery("SELECT e FROM " +
	 * persistentClass..getSimpleName() + " e", persistentClass.getClass());
	 * entities = q.getResultList();
	 * 
	 * // Commit the transaction transaction.commit(); } catch (Exception ex) {
	 * // If there are any exceptions, roll back the changes if (transaction !=
	 * null) { transaction.rollback(); } // Print the Exception
	 * ex.printStackTrace(); } finally { // Close the EntityManager
	 * entityManager.close();
	 * 
	 * closeEntityManagerFactory(); }
	 * 
	 * return entities; }
	 */
	
	// ---------------------------------- CRUD --------------------------------------------------------
	@Transactional
	public T create(T t) {
        AbstractEntity.entityManager.persist(t);
        return t;
    }
	
	@Transactional
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
    public void update() {
    	EntityTransaction transaction = entityManager.getTransaction();
    	transaction.begin();
    	entityManager.merge(this);		     
		entityManager.flush();
		transaction.commit();
    }
	
	@Transactional
    public void delete(T t) {
        t = AbstractEntity.entityManager.merge(t);
        AbstractEntity.entityManager.remove(t);
    }
	
	@Transactional
	public void save() {
		/*
		if (this.entityManager == null) {			
			entityManager = getEntityManagerFactory().createEntityManager();
			System.out.println("New entityManager created.");
		}
		*/
		
		entityManager.getTransaction().begin();
		entityManager.persist(this);
		entityManager.getTransaction().commit();
	}

	// ---------------------------------- FIND --------------------------------------------------------
	public List<T> findAll() {
		/*
		 * if (log.isDebugEnabled()) { log.debug("Finding all " + entityClass);
		 * }
		 */
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
		criteriaQuery.from(entityClass);
		TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
		
		//LogUtils.logTrace(query.toString());
		
		return findList(query);
	}
	
	@Transactional
	public T findById (long id) throws Exception {
		T o = AbstractEntity.entityManager.find(entityClass, id);
		
		if (o == null)
			throw new Exception ("Id not found...");
		
		return o;
	}
	
	/*
	@Transactional
	@SuppressWarnings("unchecked")
	public static Object findById (Class c, long id) {		
		Object o = AbstractEntity.entityManager.find(c, id);
		return (Class) o;
	}
	*/	
	
	@Transactional
	protected static <U> List<U> findList(TypedQuery<U> query) {
		List<U> result = query.getResultList();
		if (result == null) {
			result = new ArrayList<U>();
		}
		return result;
	}
	
	/*
	@Transactional
	protected E find(TypedQuery<E> query) { // <E extends AbstractEntity>
		E result = query.getResultList().get(0);
		
		return result;
	}
	*/
	
	// ---------------------------------- QUERIES --------------------------------------------------------
	@Transactional
	public static int executeDeleteUpdate(String queryString, String parameterName, Long parameterValue) {
		int rowsAffected;
		
		// javax.persistence.TransactionRequiredException: 
		// Exception Description: No transaction is currently active		
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
				
		entityManager.flush();
		entityManager.clear();		
		 
		Query q = entityManager.createQuery(queryString);
		q.setParameter(parameterName, parameterValue);
		rowsAffected = q.executeUpdate();
		
		tx.commit();
		
		return rowsAffected;		
	} 	
	
	@Transactional
	public static int executeDeleteUpdate(String queryString, String parameterName, Date parameterValue) {
		int rowsAffected;
		
		// javax.persistence.TransactionRequiredException: 
		// Exception Description: No transaction is currently active		
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();
				
		entityManager.flush();
		entityManager.clear();		
		 
		Query q = entityManager.createQuery(queryString);
		q.setParameter(parameterName, parameterValue, TemporalType.DATE);
		rowsAffected = q.executeUpdate();
		
		tx.commit();
		
		return rowsAffected;		
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public static List<Object[]> executeQuery(String nativeQuery) {
		LogUtils.logTrace(nativeQuery);
		
		//https://www.thoughts-on-java.org/jpa-native-queries/		
		Query q = entityManager.createNativeQuery(nativeQuery);
		
		return q.getResultList();		 
	}
}