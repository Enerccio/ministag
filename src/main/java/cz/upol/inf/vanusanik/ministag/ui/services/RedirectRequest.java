package cz.upol.inf.vanusanik.ministag.ui.services;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.ui.tools.Utils;

@Named("redirect")
@RequestScoped
public class RedirectRequest {

	private String uriFragment;
	
	private String message;

	public String getUriFragment() {
		return uriFragment;
	}

	public void setUriFragment(String uriFragment) {
		this.uriFragment = uriFragment;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String redirect() {
		return Utils.appendRedirect(uriFragment);
	}
}
