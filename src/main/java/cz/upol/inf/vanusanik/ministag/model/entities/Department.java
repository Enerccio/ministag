package cz.upol.inf.vanusanik.ministag.model.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "dept")
public class Department extends BasicEntity {

	@Id
	@Column(length = 10, name = "ID")
	private String shortName;
	
	@Column(columnDefinition="LONGTEXT")
	private String description;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="dept_garants",
	    joinColumns=@JoinColumn(name="dept_id", referencedColumnName="ID"),
	    inverseJoinColumns=@JoinColumn(name="user_id", referencedColumnName="ID"))
	private List<User> garants = new ArrayList<User>();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="dept_teachers",
	    joinColumns=@JoinColumn(name="dept_id", referencedColumnName="ID"),
	    inverseJoinColumns=@JoinColumn(name="user_id", referencedColumnName="ID"))
	private List<User> teachers = new ArrayList<User>();
	
	@Column(length = 64)
	private String name;
	
	@Override
	public String displayShort() {
		return shortName;
	}
	
	@Override
	public String getPrimaryKey() {
		return "shortName";
	}

	@Override
	public Object getPrimaryKeyValue() {
		return getShortName();
	}

	@Override
	public String getMappedName() {
		return "Department";
	}

	public String getShortName() {
		return shortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getGarants() {
		return garants;
	}

	public void setGarants(List<User> garants) {
		this.garants = garants;
	}

	public List<User> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<User> teachers) {
		this.teachers = teachers;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
}
