package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import cz.upol.inf.vanusanik.ministag.model.entities.TimetableType;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.ui.services.SecurityController.ActiveSession;
import cz.upol.inf.vanusanik.ministag.ui.tools.Utils;

/**
 * GarantController
 * @author enerccio
 *
 */
@ApplicationScoped
@Named("garant")
public class GarantController {

	@Inject
	private ActiveSession as;
	@Inject
	private MinistagRepository repository;
	@Inject
	private ChosenDepartment ad;
	@Inject
	private ChosenCourse ac;
	@Inject
	private ChosenBlock ab;
	@Inject
	private GarantEditBlock eb;

	/**
	 * Holds currenly chosen department for session
	 * @author enerccio
	 *
	 */
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

	/**
	 * Holds currently chosen course for session
	 * @author enerccio
	 *
	 */
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

	/**
	 * Holds currently chosen block for session
	 * @author enerccio
	 *
	 */
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

	/**
	 * Handles showing department list
	 * @author enerccio
	 *
	 */
	@RequestScoped
	@Named("garantDeptList")
	public static class GarantDepartmentList {

		@Inject
		private GarantController controller;

		public List<Department> getDepartments() {
			return controller.listDepartments();
		}

	}

	/**
	 * Handles editing the course
	 * @author enerccio
	 *
	 */
	@RequestScoped
	@Named("garantCourseEdit")
	public static class GarantEditCourse {
		@Inject
		private GarantController controller;
		@Inject
		private ChosenCourse ac;
		@Inject
		private ChosenDepartment ad;
		@Inject
		private MinistagRepository repository;

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

	/**
	 * Handles editing block
	 * @author enerccio
	 *
	 */
	@SessionScoped
	@Named("garantEditBlock")
	public static class GarantEditBlock implements Serializable {
		private static final long serialVersionUID = -8455586560856921293L;

		@Inject
		private ChosenCourse ac;
		@Inject
		private ChosenDepartment ad;
		@Inject
		private ChosenBlock ab;
		@Inject
		private MinistagRepository repository;
		@Inject
		private ActiveSession as;

		private String blockId = "";
		private String courseName = "";
		private List<Timetable> timetables = new ArrayList<Timetable>();

		private List<Timetable> timetables2delete = new ArrayList<Timetable>();

		private String teacher;
		private TimetableType type = TimetableType.LECTURE;
		private Date classFrom = Utils.calendarData(8, 0);
		private Date classTo = Utils.calendarData(9, 45);
		private int day = 0;

		@PostConstruct
		public void init() {
			teacher = as.getCurrentUser().getLogin();
			courseName = ad.getCurrentDepartment().getShortName() + "/" + ac.getCurrentCourse().getShortName();
			timetables.clear();
			timetables2delete.clear();
			if (ab.getCurrentBlock() != null) {
				timetables.clear();
				timetables.addAll(ab.getCurrentBlock().getTimetableChoices());
				blockId = ab.getCurrentBlock().getBlockDisplayName();
			}
		}

		public String submit() {
			RequiredBlock rb;
			if (ab.getCurrentBlock() == null) {
				rb = new RequiredBlock();
			} else {
				rb = ab.getCurrentBlock();
			}
			rb.setBlockDisplayName(getBlockId());
			rb.setTaughtClass(ac.getCurrentCourse());
			rb = repository.save(rb);

			ac.getCurrentCourse().getBlocks().add(rb);
			ac.setCurrentCourse(repository.save(ac.getCurrentCourse()));

			for (Timetable td : timetables2delete) {
				if (td.getBlock() != null) {
					rb.getTimetableChoices().remove(td);
					repository.remove(td);
				}
			}

			for (Timetable td : timetables) {
				if (td.getBlock() == null) {
					td.setBlock(rb);
					td = repository.save(td);
					rb.getTimetableChoices().add(td);
				} else {
					repository.save(td);
				}
			}

			ab.setCurrentBlock(repository.save(rb));
			init();
			return Utils.appendRedirect("gmyCourses.xhtml");
		}

		/**
		 * Adds single timetable entry. This is not saved until submit.
		 * @return
		 */
		public String add() {
			Timetable t = new Timetable();
			t.setClassFrom(getClassFrom());
			t.setClassTo(getClassTo());
			t.setTeacher(repository.find(getTeacher(), User.class));
			t.setType(getType());
			t.setDay(getDay());
			timetables.add(t);

			type = TimetableType.LECTURE;
			teacher = null;
			classFrom = Utils.calendarData(8, 0);
			classTo = Utils.calendarData(9, 45);
			return "";
		}

		public String modify(Timetable t) {
			return "";
		}

		public String delete(Timetable t) {
			timetables2delete.add(t);
			timetables.remove(t);
			return "";
		}

		public String getBlockId() {
			return blockId;
		}

		public void setBlockId(String blockId) {
			this.blockId = blockId;
		}

		public String getCourseName() {
			return courseName;
		}

		public void setCourseName(String courseName) {
			this.courseName = courseName;
		}

		public List<Timetable> getTimetables() {
			return timetables;
		}

		public void setTimetables(List<Timetable> timetables) {
			this.timetables = timetables;
		}

		public String getTeacher() {
			return teacher;
		}

		public void setTeacher(String teacher) {
			this.teacher = teacher;
		}

		public TimetableType getType() {
			return type;
		}

		public void setType(TimetableType type) {
			this.type = type;
		}

		public Date getClassFrom() {
			return classFrom;
		}

		public void setClassFrom(Date classFrom) {
			this.classFrom = classFrom;
		}

		public Date getClassTo() {
			return classTo;
		}

		public void setClassTo(Date classTo) {
			this.classTo = classTo;
		}

		public List<TimetableType> getTimetableTypes() {
			return new ArrayList<TimetableType>(Arrays.asList(TimetableType.values()));
		}

		public List<User> getAllTeachers() {
			return ad.getCurrentDepartment().getTeachers();
		}

		public int getDay() {
			return day;
		}

		public void setDay(int day) {
			this.day = day;
		}

		public ChosenBlock getAb() {
			return ab;
		}

		public void setAb(ChosenBlock ab) {
			this.ab = ab;
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
		eb.init();
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
