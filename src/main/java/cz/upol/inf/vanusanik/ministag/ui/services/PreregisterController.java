package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Course;
import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;

/**
 * Handles preregister.xhtml
 * @author enerccio
 *
 */
@ApplicationScoped
@Named("preregister")
public class PreregisterController {

	@SessionScoped
	@Named("activePreregister")
	public static class ActivePreregister implements Serializable {
		private static final long serialVersionUID = -7909077009774084563L;
		
		private Course currentlyEdited;

		public void clear() {
			
		}

		public Course getCurrentlyEdited() {
			return currentlyEdited;
		}

		public void setCurrentlyEdited(Course currentlyEdited) {
			this.currentlyEdited = currentlyEdited;
		}		
	}
	
	@Inject private ActivePreregister ap;
	@Inject	private MinistagRepository repository;
	
	/**
	 * Clears preregister cache and displays preregister.xhtml
	 * @return
	 */
	public String showIndex() {
		ap.clear();
		return "preregister.xhtml";
	}
	
	/**
	 * Returns the list of courses without ones that are signed up by student
	 * @param u
	 * @return
	 */
	public List<Course> getUnregisteredCourses(User u) {
		if (u.getRole() == Roles.STUDENT) {
			List<Course> ac = repository.getAllCourses();
			ac.removeAll(repository.getCoursesForStudent(u));
			return ac; 
		}
		return new ArrayList<Course>();
	}
	
	/**
	 * Loads the course as currently being registered/deregistered
	 * @param c
	 * @return
	 */
	public String updateCourseRegistration(Course c) {
		ap.setCurrentlyEdited(c);
		return "";
	}
}
