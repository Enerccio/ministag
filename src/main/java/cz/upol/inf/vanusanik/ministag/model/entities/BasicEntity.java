package cz.upol.inf.vanusanik.ministag.model.entities;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
/**
 * Basic root entity.
 * @author enerccio
 *
 */
public abstract class BasicEntity {

	/**
	 * Returns name of the primary key field
	 * @return
	 */
	public abstract String getPrimaryKey();

	/**
	 * Returns the value of the primary key of this entity
	 * @return
	 */
	public abstract Object getPrimaryKeyValue();

	/**
	 * Returns mapping name
	 * @return
	 */
	public abstract String getMappedName();

	/**
	 * Returns human-readable short display name
	 * @return
	 */
	public abstract String displayShort();

}
