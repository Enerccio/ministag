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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "course")
public class Course extends BasicEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	private String name;
	
	@Column(length = 10)
	private String shortName;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "class_garant", nullable=false)
	private User garant;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="taughtClass")
	private List<RequiredBlock> blocks = new ArrayList<RequiredBlock>();
	
	@Column(columnDefinition = "LONGTEXT")
	private String description;
	
	@Column(columnDefinition = "LONGTEXT")
	private String syllabus;
	
	private int creditCount;
	
	private boolean hasExam;
	
	private EndingType endingType;
	
	@Override
	public String displayShort() {
		return shortName;
	}

	public List<RequiredBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<RequiredBlock> blocks) {
		this.blocks = blocks;
	}

	public int getCreditCount() {
		return creditCount;
	}

	public void setCreditCount(int creditCount) {
		this.creditCount = creditCount;
	}

	public boolean isHasExam() {
		return hasExam;
	}

	public void setHasExam(boolean hasExam) {
		this.hasExam = hasExam;
	}

	public EndingType getEndingType() {
		return endingType;
	}

	public void setEndingType(EndingType endingType) {
		this.endingType = endingType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
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
		return "TaughClass";
	}
	
	
}
