package cz.upol.inf.vanusanik.ministag.model.entities;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BasicEntity {
	
	public abstract String getPrimaryKey();
	public abstract Object getPrimaryKeyValue();
	public abstract String getMappedName();

}
