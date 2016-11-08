package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.ui.tools.Utils;

@Named("departmentController")
@ApplicationScoped
public class DepartmentController {
	
	@Named("activeDepartment")
	@SessionScoped
	public static class ActiveDepartment implements Serializable {
		private static final long serialVersionUID = -1751329860707450138L;
		
		private String activeDepartment;

		public String getActiveDepartment() {
			return activeDepartment;
		}

		public void setActiveDepartment(String activeDepartment) {
			this.activeDepartment = activeDepartment;
		}		
		
	}
	
	@Named("activeCourse")
	@SessionScoped
	public static class ActiveCourse implements Serializable {
		private static final long serialVersionUID = -1751329860707450138L;
		
	}
	
	@Named("departmentList")
	@RequestScoped
	public static class DepartmentList {
	
		@Inject private DepartmentController controller;
		
		public List<Department> getDepartments() {
			return controller.getDepartments();
		}
		
		public String edit(String dept) {
			if ("".equals(dept))
				dept = null;
			return controller.editDepartment(dept);
		}
		
		public String remove(String dept) {
			controller.removeDepartments(dept);
			return Utils.appendRedirect("admDept.xhtml");
		}
	}
	
	@Inject private MinistagRepository repository;
	@Inject private ActiveDepartment activeDepartment;
	
	public List<Department> getDepartments() {
		return repository.getDepartments();
	}

	public void removeDepartments(String dept) {
		repository.remove(repository.find(dept, Department.class));
	}

	public String editDepartment(String dept) {
		activeDepartment.setActiveDepartment(dept);
		return Utils.appendRedirect("admEditDept.xhtml");
	}
	
}
