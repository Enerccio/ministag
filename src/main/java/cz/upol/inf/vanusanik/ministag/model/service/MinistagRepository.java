package cz.upol.inf.vanusanik.ministag.model.service;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import cz.upol.inf.vanusanik.ministag.model.entities.AppSettings;
import cz.upol.inf.vanusanik.ministag.model.entities.BasicEntity;

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
				if (em.createQuery("SELECT count(apps) FROM AppSettings", Long.class)
						.getSingleResult() == 0) {
					synchronized (em) {
						if (em.createQuery("SELECT count(apps) FROM AppSettings", Long.class)
								.getSingleResult() == 0) {
							AppSettings as = new AppSettings();
							// TODO: defaults for app settings
							save(as);
						}
					}
				}
				return em.createQuery("SELECT apps FROM AppSettings", AppSettings.class)
						.setMaxResults(1).getResultList().iterator().next();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private <T> T inTransaction(InTransaction inTransaction) {
		return (T) inTransaction.inTransaction();
	}
}

abstract class InTransaction {
	
	private EntityManager em;
	
	InTransaction(EntityManager em) {
		this.em = em;
	}
	
	Object inTransaction() {
		try {
			em.getTransaction().begin();
			Object v = doInTransaction();
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