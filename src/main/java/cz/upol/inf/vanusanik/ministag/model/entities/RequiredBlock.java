package cz.upol.inf.vanusanik.ministag.model.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "class_block")
public class RequiredBlock extends BasicEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="class_id")
	private Course taughtClass;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="block")
	private List<Timetable> timetableChoices = new ArrayList<Timetable>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Course getTaughtClass() {
		return taughtClass;
	}

	public void setTaughtClass(Course taughtClass) {
		this.taughtClass = taughtClass;
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
		return "RequiredBlock";
	}
	
	
	
}
