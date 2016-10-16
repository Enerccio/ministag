package cz.upol.inf.vanusanik.ministag.ui.tools;

/**
 * Uri action
 *
 */
public interface Action {
	
	/**
	 * @return uri to go to. If null, it will be the loginTarget.uri
	 */
	public String run();

}
