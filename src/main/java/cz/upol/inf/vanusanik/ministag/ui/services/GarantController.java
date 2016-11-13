package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.ui.services.SecurityController.ActiveSession;
import cz.upol.inf.vanusanik.ministag.ui.tools.Utils;

@ApplicationScoped
@Named("garant")
public class GarantController {
	
	@Inject private ActiveSession as;
	@Inject private MinistagRepository repository;
	@Inject private ChosenDepartment ad;
	
	@SessionScoped
	@Named("garantChosenDept")
	public static class ChosenDepartment implements Serializable {
		private static final long serialVersionUID = 8978334012055233297L;
		
		private Department currentDepartment;

		public Department getCurrentDepartment() {
			return currentDepartment;
		}

		public void setCurrentDepartment(Department currentDepartment) {
			this.currentDepartment = currentDepartment;
		}
	}
	
	@RequestScoped
	@Named("garantDeptList")
	public static class GarantDepartmentList {
		
		@Inject private GarantController controller;
		
		public List<Department> getDepartments() {
			return controller.listDepartments();
		}
		
	}

	public List<Department> listDepartments() {
		User garant = as.getCurrentUser();
		return repository.getDepartmentByGarant(garant);
	}
	
	public String editCoursesIn(Department d) {
		ad.currentDepartment = d;
		return Utils.appendRedirect("gmyCourses.xhtml");
	}
}
