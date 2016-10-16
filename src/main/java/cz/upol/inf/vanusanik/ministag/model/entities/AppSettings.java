package cz.upol.inf.vanusanik.ministag.model.entities;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sys_settings")
public class AppSettings extends BasicEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private Calendar startOfYear;
	
	private int numWeeks;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Calendar getStartOfYear() {
		return startOfYear;
	}
	
	public void setStartOfYear(Calendar startOfYear) {
		this.startOfYear = startOfYear;
	}
	
	public int getNumWeeks() {
		return numWeeks;
	}
	
	public void setNumWeeks(int numWeeks) {
		this.numWeeks = numWeeks;
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
		return "AppSettings";
	}
	
}
