package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.BasicEntity;
import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;

@ApplicationScoped
@Named("index")
public class Index {
	
	/**
	 * Search index root
	 * 
	 * Selector for which root is displayed at index page
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
	
	@Inject private ActiveIndexSearch activeSearch; 
	@Inject private MinistagRepository repository;
	
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
	
	public String select(BasicEntity selector) {
		if (activeSearch.getCurrentSearchStack().contains(selector)) {
			activeSearch.setCurrentSearchStack(new LinkedList<BasicEntity>(activeSearch.getCurrentSearchStack().subList(0, 
					activeSearch.getCurrentSearchStack().indexOf(selector)))); 
		} else {
			activeSearch.getCurrentSearchStack().add(selector);
		}
		return "";
	}
}
