package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Course;
import cz.upol.inf.vanusanik.ministag.model.entities.Department;
import cz.upol.inf.vanusanik.ministag.model.entities.EndingType;
import cz.upol.inf.vanusanik.ministag.model.entities.RequiredBlock;
import cz.upol.inf.vanusanik.ministag.model.entities.Timetable;
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
	@Inject private ChosenCourse ac;
	@Inject private ChosenBlock ab;
	
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
	
	@SessionScoped
	@Named("garantChosenCourse")
	public static class ChosenCourse implements Serializable {
		private static final long serialVersionUID = 8978334012055233298L;
		
		private Course currentCourse;

		public Course getCurrentCourse() {
			return currentCourse;
		}

		public void setCurrentCourse(Course currentCourse) {
			this.currentCourse = currentCourse;
		}
	}
	
	@SessionScoped
	@Named("garantChosenBlock")
	public static class ChosenBlock implements Serializable {
		private static final long serialVersionUID = 7978334012055233298L;
		
		private RequiredBlock currentBlock;

		public RequiredBlock getCurrentBlock() {
			return currentBlock;
		}

		public void setCurrentBlock(RequiredBlock currentBlock) {
			this.currentBlock = currentBlock;
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
	
	@RequestScoped
	@Named("garantCourseEdit")
	public static class GarantEditCourse {
		@Inject private GarantController controller;
		@Inject private ChosenCourse ac;
		@Inject private ChosenDepartment ad;
		@Inject private MinistagRepository repository;
		
		private String abbr = "";
		private String name = "";
		private User garant;
		private String description = "";
		private String syllabus = "";
		private int creditCount = 0;
		private EndingType endingType = EndingType.CREDIT;
		private boolean exam = false;
		
		@PostConstruct
		public void init() {
			controller.initEditCourse(this);
		}
		
		public String getAbbr() {
			return abbr;
		}
		public void setAbbr(String abbr) {
			this.abbr = abbr;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public User getGarant() {
			return garant;
		}
		public void setGarant(User garant) {
			this.garant = garant;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getSyllabus() {
			return syllabus;
		}
		public void setSyllabus(String syllabus) {
			this.syllabus = syllabus;
		}
		public int getCreditCount() {
			return creditCount;
		}
		public void setCreditCount(int creditCount) {
			this.creditCount = creditCount;
		}
		public EndingType getEndingType() {
			return endingType;
		}
		public void setEndingType(EndingType endingType) {
			this.endingType = endingType;
		}
		public boolean isExam() {
			return exam;
		}
		public void setExam(boolean exam) {
			this.exam = exam;
		}	
		
		public List<EndingType> getEndings() {
			return new ArrayList<EndingType>(Arrays.asList(EndingType.values()));
		}
		
		public String submit() {
			Course c;
			boolean newCourse = false;
			if (ac.getCurrentCourse() != null) {
				c = repository.find(ac.getCurrentCourse().getId(), Course.class);
			} else {
				c = new Course();
				c.setDept(ad.getCurrentDepartment());
				newCourse = true;
			}
			
			c.setShortName(getAbbr());
			c.setCreditCount(getCreditCount());
			c.setDescription(getDescription());
			c.setEndingType(getEndingType());
			c.setGarant(getGarant());
			c.setHasExam(isExam());
			c.setName(getName());
			c.setSyllabus(getSyllabus());
			
			ac.setCurrentCourse(repository.save(c));
			
			if (newCourse) {
				Department d = repository.find(ad.getCurrentDepartment().getShortName(), Department.class);
				d.getCourses().add(ac.getCurrentCourse());
				
				ad.setCurrentDepartment(repository.save(d));
			}
			return Utils.appendRedirect("gmyCourses.xhtml");
		}
	}

	public List<Department> listDepartments() {
		User garant = as.getCurrentUser();
		return repository.getDepartmentByGarant(garant);
	}
	
	public void initEditCourse(GarantEditCourse ec) {
		if (ac.getCurrentCourse() != null) {
			ec.setAbbr(ac.getCurrentCourse().getShortName());
			ec.setCreditCount(ac.getCurrentCourse().getCreditCount());
			ec.setDescription(ac.getCurrentCourse().getDescription());
			ec.setEndingType(ac.getCurrentCourse().getEndingType());
			ec.setExam(ac.getCurrentCourse().isHasExam());
			ec.setName(ac.getCurrentCourse().getName());
			ec.setSyllabus(ac.getCurrentCourse().getSyllabus());
		}
		ec.setGarant(as.getCurrentUser());
	}

	public String editCoursesIn(Department d) {
		ad.setCurrentDepartment(d);
		return Utils.appendRedirect("gmyCourses.xhtml");
	}
	
	public String editCourse(Course c) {
		ac.setCurrentCourse(c);
		return Utils.appendRedirect("gmyCourse.xhtml");
	}
	
	public String editBlock(Course c, RequiredBlock b) {
		ac.setCurrentCourse(c);
		ab.setCurrentBlock(b);
		return Utils.appendRedirect("gTimetable.xhtml");
	}
	
	public String delete(Course c) {
		c.getDept().getCourses().remove(c);
		repository.save(c.getDept());
		for (RequiredBlock b : c.getBlocks()) {
			for (Timetable t : b.getTimetableChoices()) {
				repository.remove(t);
			}
			repository.remove(b);
		}
		repository.remove(c);
		return "";
	}
}
