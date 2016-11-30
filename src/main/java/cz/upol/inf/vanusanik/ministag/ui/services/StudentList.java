package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.RequiredBlock;
import cz.upol.inf.vanusanik.ministag.model.entities.Timetable;
import cz.upol.inf.vanusanik.ministag.model.entities.User;

/**
 * Handles the student list ajax requests.
 * @author enerccio
 *
 */
@ApplicationScoped
@Named("studentList")
public class StudentList {

	/**
	 * Holds the list of students to be displayed in message box
	 * @author enerccio
	 *
	 */
	@SessionScoped 
	@Named("activeStudentList")
	public static class ActiveStudentList implements Serializable {
		private static final long serialVersionUID = -5056712862414319772L;
		private boolean display = false;
		private boolean links = false;
		private Set<User> students = new HashSet<User>();
		
		public boolean isDisplay() {
			return display;
		}
		
		public void setDisplay(boolean display) {
			this.display = display;
		}
		
		public Set<User> getStudents() {
			return students;
		}
		
		public Set<User> getStudentsOnce() {
			this.display = false;
			return students;
		}
		
		public void setStudents(Set<User> students) {
			this.students = students;
		}
		
		public boolean isLinks() {
			return links;
		}
		
		public void setLinks(boolean links) {
			this.links = links;
		}
	}
	
	@Inject private ActiveStudentList studentList;
	
	/**
	 * Shows the ajax request
	 * @param sourceData
	 * @param links
	 * @return
	 */
	public String show(Object sourceData, boolean links) {
		if (sourceData instanceof RequiredBlock) {
			RequiredBlock rb = (RequiredBlock)sourceData;
			studentList.getStudents().clear();
			for (Timetable t : rb.getTimetableChoices()) {
				studentList.getStudents().addAll(t.getStudents());
			}
			studentList.setDisplay(true);
			studentList.setLinks(true);
			return null;
		}
		studentList.getStudents().clear();
		studentList.setDisplay(false);
		return null;
	}
}
