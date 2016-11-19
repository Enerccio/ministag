package cz.upol.inf.vanusanik.ministag.schedule;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;

import cz.upol.inf.vanusanik.ministag.model.entities.Timetable;

@SessionScoped
public class ScheduleRequest implements Serializable {
	private static final long serialVersionUID = 4588865678998938852L;
	
	private void readObject(java.io.ObjectInputStream in)
		    throws IOException, ClassNotFoundException {
	    in.defaultReadObject();
	    drawList = new ArrayList<Timetable>();
	}

	private transient List<Timetable> drawList = new ArrayList<Timetable>();
	private boolean colored = true;
	private String title = "";

	public List<Timetable> getDrawList() {
		return drawList;
	}
	public void setDrawList(List<Timetable> drawList) {
		this.drawList = drawList;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
