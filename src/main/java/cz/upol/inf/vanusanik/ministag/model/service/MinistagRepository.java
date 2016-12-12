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
import cz.upol.inf.vanusanik.ministag.model.entities.Course;
import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.entities.Timetable;
import cz.upol.inf.vanusanik.ministag.model.entities.User;

/**
 * Handles all entity persistency functions
 * 
 * @author enerccio
 *
 */
@ManagedBean(name = "ministag")
@ApplicationScoped
public class MinistagRepository {

	/** EntityManager instance */
	private EntityManager em;

	public MinistagRepository() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("ministag");
		em = factory.createEntityManager();
	}

	/**
	 * Finds an object by class and primary key value
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T extends BasicEntity> T find(final Object key, final Class<T> clazz) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.find(clazz, key);
			}
		});
	}

	/**
	 * Saves object to persistency
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends BasicEntity> T save(final T entity) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				if (em.contains(entity)) {
					Object o = em.merge(entity);
					em.flush();
					return o;
				} else {
					em.persist(entity);
					em.flush();
					return entity;
				}

			}
		});
	}

	/**
	 * Deletes object from persistency
	 * 
	 * @param entity
	 */
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

	/**
	 * Returns AppSettings singleton from database
	 * 
	 * @return
	 */
	public AppSettings getAppSettings() {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				if (em.createQuery("SELECT count(apps) FROM AppSettings apps", Long.class).getSingleResult() == 0) {
					synchronized (em) {
						if (em.createQuery("SELECT count(apps) FROM AppSettings apps", Long.class)
								.getSingleResult() == 0) {
							AppSettings as = new AppSettings();

							Calendar c = Calendar.getInstance();
							c.set(2016, 8, 19, 0, 0);
							as.setStartOfYear(c);
							as.setNumWeeks(13);
							return save(as);
						}
					}
				}
				return em.createQuery("SELECT apps FROM AppSettings apps", AppSettings.class).setMaxResults(1)
						.getResultList().iterator().next();
			}
		});
	}

	/**
	 * Handles code in transaction
	 * 
	 * @param inTransaction
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T inTransaction(InTransaction inTransaction) {
		return (T) inTransaction.inTransaction();
	}

	/**
	 * Returns all users in list
	 * 
	 * @return
	 */
	public List<User> getUsers() {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT u FROM User u ORDER BY u.login").getResultList();
			}
		});
	}

	/**
	 * Returns all departments in list
	 * 
	 * @return
	 */
	public List<Department> getDepartments() {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT d FROM Department d ORDER BY d.shortName", Department.class)
						.getResultList();
			}

		});
	}

	/**
	 * Returns list of users of specified role
	 * 
	 * @param role
	 * @return
	 */
	public List<User> getUsersByRole(final Roles role) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT u FROM User u WHERE u.role = ?", User.class).setParameter(1, role)
						.getResultList();
			}

		});
	}

	/**
	 * Returns list of departments user is garant of
	 * 
	 * @param garant
	 * @return
	 */
	public List<Department> getDepartmentByGarant(final User garant) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT d FROM Department d WHERE ? in d.garant", Department.class)
						.setParameter(1, garant).getResultList();
			}

		});
	}

	/**
	 * Checks whether course abbreviation is unique
	 * 
	 * @param dabbr
	 *            department abbreviation
	 * @param cabbr
	 *            course abbreviation
	 * @return
	 */
	public boolean uniqueCourseName(final String dabbr, final String cabbr) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em
						.createQuery("SELECT COUNT(distinct c) FROM Department d JOIN d.courses c "
								+ "WHERE d.shortName = ?1 AND c.shortName = ?2", Long.class)
						.setParameter(1, dabbr).setParameter(2, cabbr).getSingleResult().equals(0L);
			}

		});
	}

	/**
	 * Returns all timetable entries in a list for specified teacher
	 * 
	 * @param u
	 * @return
	 */
	public List<Timetable> getTimetableForTeacher(final User u) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT distinct t FROM Timetable t JOIN t.teacher u " + "WHERE u = ?1",
						Timetable.class).setParameter(1, u).getResultList();
			}

		});
	}

	/**
	 * Returns all timetable entries in a list for specified student
	 * 
	 * @param u
	 * @return
	 */
	public List<Timetable> getTimetableForStudent(final User u) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT distinct t FROM Timetable t JOIN t.students s " + "WHERE s = ?1",
						Timetable.class).setParameter(1, u).getResultList();
			}

		});
	}

	/**
	 * Returns list of courses for specified teacher
	 * 
	 * @param u
	 * @return
	 */
	public List<Course> getCoursesForTeacher(User u) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT distinct c FROM Course c JOIN c.blocks b JOIN b.timetableChoices tc "
						+ "WHERE tc.teacher = ?1", Course.class).setParameter(1, u).getResultList();
			}

		});
	}

	/**
	 * Returns list of courses for specified student
	 * 
	 * @param u
	 * @return
	 */
	public List<Course> getCoursesForStudent(User u) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery(
						"SELECT distinct c FROM Course c JOIN c.blocks b JOIN b.timetableChoices tc JOIN tc.students s "
								+ "WHERE s = ?1",
						Course.class).setParameter(1, u).getResultList();
			}

		});
	}

	/**
	 * Returns list of timetable entries for course
	 * 
	 * @param c
	 * @return
	 */
	public List<Timetable> getTimetableForCourse(final Course c) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery(
						"SELECT distinct t FROM Course c JOIN c.blocks b JOIN b.timetableChoices t " + "WHERE c = ?1",
						Timetable.class).setParameter(1, c).getResultList();
			}

		});
	}

	/**
	 * Returns all courses in a list
	 * 
	 * @return
	 */
	public List<Course> getAllCourses() {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT c FROM Course c", Course.class).getResultList();
			}

		});
	}
}

/**
 * Transaction helper.
 * 
 * Code executed inside the doInTransaction() method is handled inside the
 * transaction
 * 
 * @author enerccio
 *
 */
abstract class InTransaction {

	private EntityManager em;

	InTransaction(EntityManager em) {
		this.em = em;
	}

	/**
	 * Manages the transactions
	 * 
	 * @return
	 */
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

	/**
	 * Code executed here is done in transaction, if there is no transaction
	 * open already
	 * 
	 * @return
	 * @throws Exception
	 */
	abstract Object doInTransaction() throws Exception;
}