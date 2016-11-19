package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.BasicEntity;
import cz.upol.inf.vanusanik.ministag.model.entities.Course;
import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;

@ApplicationScoped
@Named("index")
public class Index {

	/**
	 * Search index root
	 * 
	 * Selector for which root is displayed at index page
	 * 
	 * @author pvan
	 */
	public static enum DisplayRootDataSelector {
		ROOT_DEPT, ROOT_TEACHERS, ROOT_COURSES
	}

	@SessionScoped
	@Named("indexSearch")
	public static class ActiveIndexSearch implements Serializable {
		private static final long serialVersionUID = -6197987957315181285L;

		private LinkedList<BasicEntity> currentSearchStack = new LinkedList<BasicEntity>();
		private DisplayRootDataSelector root;

		public LinkedList<BasicEntity> getCurrentSearchStack() {
			return currentSearchStack;
		}

		public void setCurrentSearchStack(LinkedList<BasicEntity> currentSearchStack) {
			this.currentSearchStack = currentSearchStack;
		}

		public DisplayRootDataSelector getRoot() {
			return root;
		}

		public void setRoot(DisplayRootDataSelector root) {
			this.root = root;
		}

	}

	@Inject
	private ActiveIndexSearch activeSearch;
	@Inject
	private MinistagRepository repository;

	public String clearCache() {
		activeSearch.setRoot(null);
		activeSearch.getCurrentSearchStack().clear();
		return "index.xhtml";
	}

	public String showAllDepartments() {
		activeSearch.getCurrentSearchStack().clear();
		activeSearch.setRoot(DisplayRootDataSelector.ROOT_DEPT);
		return "";
	}

	public String showAllTeachers() {
		activeSearch.getCurrentSearchStack().clear();
		activeSearch.setRoot(DisplayRootDataSelector.ROOT_TEACHERS);
		return "";
	}

	public String showAllCourses() {
		activeSearch.getCurrentSearchStack().clear();
		activeSearch.setRoot(DisplayRootDataSelector.ROOT_COURSES);
		return "";
	}

	public List<Department> getAllDepartments() {
		return repository.getDepartments();
	}

	public List<User> getAllTeachers() {
		List<User> u = new ArrayList<User>();
		u.addAll(repository.getUsersByRole(Roles.GARANT));
		u.addAll(repository.getUsersByRole(Roles.TEACHER));
		Collections.sort(u, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.displayShort().compareTo(o2.displayShort());
			}

		});
		return u;
	}
	
	public List<Course> getAllCourses() {
		List<Course> clist = repository.getAllCourses();
		Collections.sort(clist, new Comparator<Course>() {

			@Override
			public int compare(Course o1, Course o2) {
				int diff = o1.getDept().displayShort().compareTo(o2.getDept().displayShort());
				if (diff == 0)
					return o1.displayShort().compareTo(o2.displayShort());
				return diff;
			}

		});
		return clist;
	}

	public String select(BasicEntity selector) {
		LinkedList<BasicEntity> bl = activeSearch.getCurrentSearchStack();
		if (activeSearch.getCurrentSearchStack().contains(selector)) {
			LinkedList<BasicEntity> newList = new LinkedList<BasicEntity>(bl.subList(0, bl.indexOf(selector) + 1));
			activeSearch.setCurrentSearchStack(newList);
		} else {
			bl.add(selector);
		}
		return "";
	}

	public List<Course> taughtCourses(User u) {
		return repository.getCoursesForUser(u);
	}
}
