package cz.upol.inf.vanusanik.ministag.model.entities;

import java.util.ArrayList;
import java.util.Date;
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
	
	private Date classFrom;
	private Date classTo;
	private int day;
	
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

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
}
