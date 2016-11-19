package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
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

	@Named("departmentList")
	@RequestScoped
	public static class DepartmentList {

		@Inject
		private DepartmentController controller;

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

	@Named("editDepartment")
	@RequestScoped
	public static class EditDepartment {

		@Inject
		private ActiveDepartment ad;
		@Inject
		private MinistagRepository repository;
		@Inject
		private DepartmentController controller;

		private String abbr;
		private String desc;
		private String name;
		private List<String> garants;
		private List<String> teachers;

		@PostConstruct
		public void init() {
			if (ad.getActiveDepartment() != null) {
				Department d = repository.find(ad.getActiveDepartment(), Department.class);
				abbr = d.getShortName();
				desc = d.getDescription();
				name = d.getName();
				garants = new ArrayList<String>();
				teachers = new ArrayList<String>();

				for (User u : d.getGarants()) {
					garants.add(u.getLogin());
				}

				for (User u : d.getTeachers()) {
					teachers.add(u.getLogin());
				}
			}
		}

		public List<User> getAllGarants() {
			return repository.getUsersByRole(Roles.GARANT);
		}

		public List<User> getAllTeachers() {
			List<User> ul = new ArrayList<User>(repository.getUsersByRole(Roles.GARANT));
			ul.addAll(repository.getUsersByRole(Roles.TEACHER));
			return ul;
		}

		public ActiveDepartment getAd() {
			return ad;
		}

		public void setAd(ActiveDepartment ad) {
			this.ad = ad;
		}

		public String getAbbr() {
			return abbr;
		}

		public void setAbbr(String abbr) {
			this.abbr = abbr;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getGarants() {
			return garants;
		}

		public void setGarants(List<String> garants) {
			this.garants = garants;
		}

		public List<String> getTeachers() {
			return teachers;
		}

		public void setTeachers(List<String> teachers) {
			this.teachers = teachers;
		}

		public String submit() {
			return controller.editDepartment(this);
		}
	}

	@Inject
	private MinistagRepository repository;
	@Inject
	private ActiveDepartment activeDepartment;

	public List<Department> getDepartments() {
		return repository.getDepartments();
	}

	public String editDepartment(EditDepartment editDepartment) {
		Department d = null;
		if (activeDepartment.getActiveDepartment() != null) {
			d = repository.find(activeDepartment.getActiveDepartment(), Department.class);
		} else {
			d = new Department();
		}

		d.setShortName(editDepartment.getAbbr());
		d.setName(editDepartment.getName());
		d.setDescription(editDepartment.getDesc());
		d.getGarants().clear();
		for (String g : editDepartment.getGarants()) {
			User u = repository.find(g, User.class);
			d.getGarants().add(u);
		}
		d.getTeachers().clear();
		for (String g : editDepartment.getTeachers()) {
			User u = repository.find(g, User.class);
			d.getTeachers().add(u);
		}

		repository.save(d);

		return Utils.appendRedirect("admDept.xhtml");
	}

	public void removeDepartments(String dept) {
		repository.remove(repository.find(dept, Department.class));
	}

	public String editDepartment(String dept) {
		activeDepartment.setActiveDepartment(dept);
		return Utils.appendRedirect("admEditDept.xhtml");
	}

}
