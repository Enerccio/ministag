package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Handles preregister.xhtml
 * @author enerccio
 *
 */
@ApplicationScoped
@Named("preregister")
public class PreregisterController {

	@SessionScoped
	@Named("activePreregister")
	public static class ActivePreregister implements Serializable {
		private static final long serialVersionUID = -7909077009774084563L;

		public void clear() {
			
		}		
	}
	
	@Inject private ActivePreregister ap;
	
	public String showIndex() {
		ap.clear();
		return "preregister.xhtml";
	}
}
