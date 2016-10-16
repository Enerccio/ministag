package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.User;

@SessionScoped
@Named("currentSession")
public class ActiveSession implements Serializable {
	private static final long serialVersionUID = -3755950572410738849L;
	private User currentUser;

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
}
