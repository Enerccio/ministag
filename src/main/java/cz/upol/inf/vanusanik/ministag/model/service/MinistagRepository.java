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
				if (em.createQuery("SELECT count(apps) FROM AppSettings apps", Long.class).getSingleResult() == 0) {
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
				return em.createQuery("SELECT apps FROM AppSettings apps", AppSettings.class).setMaxResults(1)
						.getResultList().iterator().next();
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
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT d FROM Department d ORDER BY d.shortName", Department.class)
						.getResultList();
			}

		});
	}

	public List<User> getUsersByRole(final Roles role) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT u FROM User u WHERE u.role = ?", User.class).setParameter(1, role)
						.getResultList();
			}

		});
	}

	public List<Department> getDepartmentByGarant(final User garant) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT d FROM Department d WHERE ? in d.garant", Department.class)
						.setParameter(1, garant).getResultList();
			}

		});
	}

	public boolean uniqueCourseName(final String shortName, final String value) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em
						.createQuery("SELECT COUNT(distinct c) FROM Department d JOIN d.courses c "
								+ "WHERE d.shortName = ?1 AND c.shortName = ?2", Long.class)
						.setParameter(1, shortName).setParameter(2, value).getSingleResult().equals(0L);
			}

		});
	}

	public List<Timetable> getTimetableForTeacher(final User u) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT distinct t FROM Timetable t JOIN t.teacher u " + "WHERE u = ?1",
						Timetable.class).setParameter(1, u).getResultList();
			}

		});
	}
	
	public List<Timetable> getTimetableForStudent(final User u) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT distinct t FROM Timetable t JOIN t.students s " + "WHERE s = ?1",
						Timetable.class).setParameter(1, u).getResultList();
			}

		});
	}

	public List<Course> getCoursesForTeacher(User u) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT distinct c FROM Course c JOIN c.blocks b JOIN b.timetableChoices tc "
						+ "WHERE tc.teacher = ?1", Course.class).setParameter(1, u).getResultList();
			}

		});
	}
	
	public List<Course> getCoursesForStudent(User u) {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery("SELECT distinct c FROM Course c JOIN c.blocks b JOIN b.timetableChoices tc JOIN tc.students s "
						+ "WHERE s = ?1", Course.class).setParameter(1, u).getResultList();
			}

		});
	}

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

	public List<Course> getAllCourses() {
		return inTransaction(new InTransaction(em) {

			@Override
			Object doInTransaction() throws Exception {
				return em.createQuery(
						"SELECT c FROM Course c",
						Course.class).getResultList();
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