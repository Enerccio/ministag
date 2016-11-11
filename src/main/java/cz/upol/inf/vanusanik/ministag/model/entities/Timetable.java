package cz.upol.inf.vanusanik.ministag.model.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "timetable")
public class Timetable extends BasicEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	private TimetableType type;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="class_id")
	private RequiredBlock block;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "timetable_teacher", nullable=false)
	private User teacher;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name="enrolled_students",
		    joinColumns=@JoinColumn(name="time_id", referencedColumnName="ID"),
		    inverseJoinColumns=@JoinColumn(name="user_id", referencedColumnName="ID"))
	private List<User> students = new ArrayList<User>();
	
	/* Unix time differences!*/
	private int timeFrom;
	
	/* Unix time differences!*/
	private int timeTo;
	
	private ScheduleType scheduleType;
	
	@Temporal(TemporalType.DATE)
	private Calendar startWeek;
	
	@Temporal(TemporalType.DATE)
	private Calendar endWeek;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TimetableType getType() {
		return type;
	}

	public void setType(TimetableType type) {
		this.type = type;
	}

	public RequiredBlock getBlock() {
		return block;
	}

	public void setBlock(RequiredBlock block) {
		this.block = block;
	}

	public User getTeacher() {
		return teacher;
	}

	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}

	public List<User> getStudents() {
		return students;
	}

	public void setStudents(List<User> students) {
		this.students = students;
	}

	public int getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(int timeFrom) {
		this.timeFrom = timeFrom;
	}

	public int getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(int timeTo) {
		this.timeTo = timeTo;
	}

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}

	public Calendar getStartWeek() {
		return startWeek;
	}

	public void setStartWeek(Calendar startWeek) {
		this.startWeek = startWeek;
	}

	public Calendar getEndWeek() {
		return endWeek;
	}

	public void setEndWeek(Calendar endWeek) {
		this.endWeek = endWeek;
	}

	@Override
	public String getPrimaryKey() {
		return "id";
	}

	@Override
	public Object getPrimaryKeyValue() {
		return getId();
	}

	@Override
	public String getMappedName() {
		return "Timetable";
	}
	
	@Override
	public String displayShort() {
		return "";
	}
}
