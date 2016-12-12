package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Course;
import cz.upol.inf.vanusanik.ministag.model.entities.RequiredBlock;
import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.entities.Timetable;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.ui.services.SecurityController.ActiveSession;

/**
 * Handles preregister.xhtml
 * 
 * @author enerccio
 *
 */
@ApplicationScoped
@Named("preregister")
public class PreregisterController {

	/**
	 * Holds current block choice timetable
	 * 
	 * @author enerccio
	 *
	 */
	public static class RBlockChoice {
		private Long choice;
		private int count;

		public Long getChoice() {
			return choice;
		}

		public void setChoice(Long choice) {
			this.choice = choice;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}

	@SessionScoped
	@Named("activePreregister")
	public static class ActivePreregister implements Serializable {
		private static final long serialVersionUID = -7909077009774084563L;

		private Course currentlyEdited;
		private List<RBlockChoice> choices = new ArrayList<RBlockChoice>();
		private boolean fail;

		public void clear() {
			currentlyEdited = null;
			fail = false;
			choices.clear();
		}

		public Course getCurrentlyEdited() {
			return currentlyEdited;
		}

		public void setCurrentlyEdited(Course currentlyEdited) {
			this.currentlyEdited = currentlyEdited;
		}

		public List<RBlockChoice> getChoices() {
			return choices;
		}

		public void setChoices(List<RBlockChoice> choices) {
			this.choices = choices;
		}

		public boolean isFail() {
			return fail;
		}

		public void setFail(boolean fail) {
			this.fail = fail;
		}
	}

	@Inject
	private ActivePreregister ap;
	@Inject
	private ActiveSession as;
	@Inject
	private MinistagRepository repository;

	/**
	 * Clears preregister cache and displays preregister.xhtml
	 * 
	 * @return
	 */
	public String showIndex() {
		ap.clear();
		return "preregister.xhtml";
	}

	/**
	 * Returns the list of courses without ones that are signed up by student
	 * 
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
	 * 
	 * @param c
	 * @return
	 */
	public String updateCourseRegistration(Course c) {
		ap.setCurrentlyEdited(c);
		ap.getChoices().clear();
		for (RequiredBlock rb : c.getBlocks()) {
			RBlockChoice rbc = new RBlockChoice();
			rbc.setCount(rb.getTimetableChoices().size());
			for (Timetable t : rb.getTimetableChoices()) {
				if (t.getStudents().contains(as.getCurrentUser())) {
					rbc.setChoice(t.getId());
					break;
				}
			}
			ap.getChoices().add(rbc);
		}
		return "";
	}

	/**
	 * Checks whether ids are equals based on choice
	 * 
	 * @param testId
	 * @param selId
	 * @return
	 */
	public boolean valid(Long testId, Long selId) {
		if (selId == null)
			return false;
		return selId.equals(testId);
	}

	/**
	 * Removes the student from all timetables for current course
	 * 
	 * @return
	 */
	public String unsub() {
		for (RequiredBlock rb : ap.getCurrentlyEdited().getBlocks()) {
			rb = repository.find(rb.getId(), RequiredBlock.class);
			for (Timetable t : rb.getTimetableChoices()) {
				t.getStudents().remove(as.getCurrentUser());
				repository.save(t);
			}
		}

		ap.clear();
		return "";
	}

	public String submit() {
		List<RBlockChoice> choices = ap.getChoices();
		for (RBlockChoice rbc : choices) {
			if (rbc.getCount() > 0 && rbc.getChoice() == null) {
				ap.setFail(true);
				return "";
			}
		}

		for (RequiredBlock rb : ap.getCurrentlyEdited().getBlocks()) {
			rb = repository.find(rb.getId(), RequiredBlock.class);
			for (Timetable t : rb.getTimetableChoices()) {
				t.getStudents().remove(as.getCurrentUser());
				repository.save(t);
			}
		}

		for (RBlockChoice rbc : choices) {
			Timetable t = repository.find(rbc.getChoice(), Timetable.class);
			t.getStudents().add(as.getCurrentUser());
			repository.save(t);
		}
		ap.clear();
		return "";
	}
}
