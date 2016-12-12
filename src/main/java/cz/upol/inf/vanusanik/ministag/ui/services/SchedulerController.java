package cz.upol.inf.vanusanik.ministag.ui.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Course;
import cz.upol.inf.vanusanik.ministag.model.entities.RequiredBlock;
import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.schedule.ScheduleRequest;
import cz.upol.inf.vanusanik.ministag.ui.tools.Utils;

/**
 * Handles requests for scheduler. Fills the session info and renders the image
 * 
 * @author enerccio
 *
 */
@ApplicationScoped
@Named("schedulerController")
public class SchedulerController {

	@Inject
	private MinistagRepository repository;
	@Inject
	private ScheduleRequest schedule;

	/**
	 * Returns the image link for request. Fills the schedule session data.
	 * 
	 * @param o
	 * @param w
	 * @param h
	 * @param encType
	 * @return
	 * @throws Exception
	 */
	public String showSchedule(Object o, int w, int h, String encType) throws Exception {
		if (o instanceof User) {
			User u = (User) o;
			if (u.getRole() == Roles.TEACHER || u.getRole() == Roles.GARANT) {
				schedule.setTitle("Rozvrh učitele " + u.getName());
				schedule.setDrawList(repository.getTimetableForTeacher(u));
				return Utils.showSchedule(w, h, encType);
			}
			if (u.getRole() == Roles.STUDENT) {
				schedule.setTitle("Rozvrh studenta " + u.getName());
				schedule.setDrawList(repository.getTimetableForStudent(u));
				return Utils.showSchedule(w, h, encType);
			}
		}
		if (o instanceof Course) {
			Course c = (Course) o;
			schedule.setTitle("Rozvrh předmětu " + c.getDept().displayShort() + "/" + c.displayShort());
			schedule.setDrawList(repository.getTimetableForCourse(c));
			return Utils.showSchedule(w, h, encType);
		}
		if (o instanceof RequiredBlock) {
			RequiredBlock b = (RequiredBlock) o;
			schedule.setTitle("Rozvrh bloku " + b.displayShort() + " pro předmět "
					+ b.getTaughtClass().getDept().displayShort() + "/" + b.getTaughtClass().displayShort());
			schedule.setDrawList(b.getTimetableChoices());
			return Utils.showSchedule(w, h, encType);
		}
		throw new Exception("not a valid object");
	}

	/**
	 * Returns relevant courses for specified user
	 * 
	 * @param u
	 * @return
	 */
	public List<Course> getRelevantCourses(User u) {
		if (u.getRole() == Roles.STUDENT) {
			return repository.getCoursesForStudent(u);
		}
		if (u.getRole() == Roles.TEACHER || u.getRole() == Roles.GARANT) {
			return repository.getCoursesForTeacher(u);
		}
		return null;
	}
}
