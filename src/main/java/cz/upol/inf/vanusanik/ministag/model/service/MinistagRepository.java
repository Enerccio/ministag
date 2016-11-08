package cz.upol.inf.vanusanik.ministag.model.service;

import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import cz.upol.inf.vanusanik.ministag.model.entities.AppSettings;
import cz.upol.inf.vanusanik.ministag.model.entities.BasicEntity;
import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.entities.User;

@ManagedBean(name = "ministag")
@ApplicationScoped
public class MinistagRepository {

	private EntityManager em;
	
	public MinistagRepository() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("ministag");
		em = factory.createEntityManager();
	}
	
	public <T extends BasicEntity> T find(final Object key, final Class<T> clazz) {
		return inTransaction(new InTransaction(em) {
			
			@Override
			Object doInTransaction() throws Exception {
				return em.find(clazz, key);
			}
		});
	}

	public <T extends BasicEntity> T save(final T entity) {
		return inTransaction(new InTransaction(em) {
			
			@Override
			Object doInTransaction() throws Exception {
				try {
					if (em.contains(entity)) {
						return em.merge(entity);
					} else {
						em.persist(entity);
						return entity;
					}
				} finally {
					em.flush();
				}
			}
		});		
	}
	
	public <T extends BasicEntity> void remove(final T entity) {
		inTransaction(new InTransaction(em) {
			
			@Override
			Object doInTransaction() throws Exception {
				if (em.contains(entity)) {
			        em.remove(entity);
			    } else {
			        BasicEntity ee = em.getReference(entity.getClass(), entity.getPrimaryKeyValue());
			        em.remove(ee);
			    }
				return null;
			}
		});
	}
	
	public AppSettings getAppSettings() {
		return inTransaction(new InTransaction(em) {
			
			@Override
			Object doInTransaction() throws Exception {
				if (em.createQuery("SELECT count(apps) FROM AppSettings apps", Long.class)
						.getSingleResult() == 0) {
					synchronized (em) {
						if (em.createQuery("SELECT count(apps) FROM AppSettings apps", Long.class)
								.getSingleResult() == 0) {
							AppSettings as = new AppSettings();
							
							Calendar c = Calendar.getInstance();
							c.set(2016, 8, 19, 0, 0);
							as.setStartOfYear(c);
							as.setNumWeeks(13);							
							save(as);
						}
					}
				}
				return em.createQuery("SELECT apps FROM AppSettings apps", AppSettings.class)
						.setMaxResults(1).getResultList().iterator().next();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private <T> T inTransaction(InTransaction inTransaction) {
		return (T) inTransaction.inTransaction();
	}

	public List<User> getUsers() {
		return inTransaction(new InTransaction(em) {
			
			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT u FROM User u ORDER BY u.login").getResultList();
			}
		});
	}

	public List<Department> getDepartments() {
		return inTransaction(new InTransaction(em){

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT d FROM Department d ORDER BY d.shortName", Department.class).getResultList();
			}
			
		});
	}
}

abstract class InTransaction {
	
	private EntityManager em;
	
	InTransaction(EntityManager em) {
		this.em = em;
	}
	
	Object inTransaction() {
		try {
			boolean activeTransaction = em.getTransaction().isActive();
			if (!activeTransaction)
				em.getTransaction().begin();
			Object v = doInTransaction();
			if (!activeTransaction)
				em.getTransaction().commit();
			return v;
		} catch (RuntimeException e) {
			em.getTransaction().setRollbackOnly();
			throw e;
		} catch (Exception e) {
			em.getTransaction().setRollbackOnly();
			return null;
		}
	}
	
	abstract Object doInTransaction() throws Exception;
}