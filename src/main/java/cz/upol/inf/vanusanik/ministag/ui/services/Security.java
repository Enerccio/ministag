package cz.upol.inf.vanusanik.ministag.ui.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;
import cz.upol.inf.vanusanik.ministag.ui.services.Security.AddEditUserRequest.CurrentlyEditedUser;
import cz.upol.inf.vanusanik.ministag.ui.tools.Action;
import cz.upol.inf.vanusanik.ministag.ui.tools.Utils;

@Named("security")
@ApplicationScoped
/**
 * Handles security of the application. Resolves logins, logouts, new users, change passwords,
 * logged-in check and roles check.
 */
public class Security {
	
	@PostConstruct
	/**
	 * Performs initialization post @Inject. 
	 * Creates admin user, if there is none
	 */
	public void init() {
		User u = repository.find("admin", User.class);
		if (u == null) {
			try {
				u = new User();
				u.setSalt(UUID.randomUUID().toString());
				u.setLogin("admin");
				u.setRole(Roles.ADMIN);
				u.setPassword(Utils.asHex(Utils.hash("#admin", u.getSalt())));
				repository.save(u);
			} catch (Exception e) {
				
			}
		}
	}

	@Named("loginTarget")
	@RequestScoped
	/**
	 * Login target. Handles what uri should login return to on success
	 */
	public static class LoginTarget {
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	@Named("login")
	@RequestScoped
	/**
	 * Request handler for login
	 */
	public static class LoginRequest {
		
		@Inject
		private Security security;
		
		private String login, password;
		private boolean loginFailure, loginNeeded;

		public boolean isLoginFailure() {
			return loginFailure;
		}

		public void setLoginFailure(boolean loginFailure) {
			this.loginFailure = loginFailure;
		}

		public boolean isLoginNeeded() {
			return loginNeeded;
		}

		public void setLoginNeeded(boolean loginNeeded) {
			this.loginNeeded = loginNeeded;
		}

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
		/**
		 * Performs the login. Validating is done via LoginGuard.
		 * @param target page to return to
		 * @return redirect
		 */
		public String doLogin(String target) {
			return security.doLogin(target, this);
		}
	}
	
	@Named("logout")
	@RequestScoped
	/**
	 * Request handler for logout
	 */
	public static class LogoutRequest {
		
		@Inject
		private ActiveSession session;
		
		public String logout() {
			session.setCurrentUser(null);
			return "index.xhtml";
		}
		
	}
	
	@Named("addEditUser")
	@RequestScoped
	/**
	 * Request handler for user management
	 */
	public static class AddEditUserRequest {	
		
		@Named("currentlyEditedUser")
		@SessionScoped
		public static class CurrentlyEditedUser implements Serializable {
			
			private static final long serialVersionUID = 5504155210761645432L;
			private String currentUser;

			public String getCurrentUser() {
				return currentUser;
			}

			public void setCurrentUser(String currentUser) {
				this.currentUser = currentUser;
			}
			
		}
		
		@Inject private MinistagRepository repository;
		@Inject private ActiveSession as;
		@Inject private CurrentlyEditedUser ceu;
		
		private User user;
		private String editUser;
		private String password;
		private String password2;
		private Roles userRole;
		
		@PostConstruct
		public void init() {
			if (ceu.getCurrentUser() != null && !"".equals(ceu.getCurrentUser())) {
				this.editUser = ceu.getCurrentUser();
				this.user = repository.find(ceu.getCurrentUser(), User.class);
				this.userRole = user.getRole();
			}
		}
		
		public List<Roles> getRoles() {
			return new ArrayList<Roles>(Arrays.asList(Roles.values()));
		}
		
		public String submit() {
			if (ceu.getCurrentUser() != null) {
				User u = repository.find(ceu.getCurrentUser(), User.class);
				
				if (password.length() != 0) {
					u.setSalt(UUID.randomUUID().toString());
					u.setPassword(Utils.asHex(Utils.hash(password, u.getSalt())));
				}
				if (!cannotEditRole()) {
					u.setRole(userRole);
				}
				
				repository.save(u);
			} else {
				User u = new User();
				
				u.setLogin(editUser);
				u.setSalt(UUID.randomUUID().toString());
				u.setPassword(Utils.asHex(Utils.hash(password, u.getSalt())));
				u.setRole(userRole);
				
				repository.save(u);
			}
			return Utils.appendRedirect("admUsers.xhtml");
		}
		
		/**
		 * Checks whether role is editable.
		 * 
		 * Role is not editable when user is editing himself (to not remove ADMIN tag)
		 * @return
		 */
		public boolean cannotEditRole() {
			if (ceu.getCurrentUser() == null)
				return false;
			return as.getCurrentUser().getLogin().equals(ceu.getCurrentUser());
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getPassword2() {
			return password2;
		}

		public void setPassword2(String password2) {
			this.password2 = password2;
		}

		public String getEditUser() {
			return editUser;
		}

		public void setEditUser(String editUser) {
			this.editUser = editUser;
		}

		public Roles getUserRole() {
			return userRole;
		}

		public void setUserRole(Roles userRole) {
			this.userRole = userRole;
		}
	}
	
	@RequestScoped
	@Named(value = "userList")
	public static class UserList {
		
		@Inject private MinistagRepository repository;
		@Inject private ActiveSession as;
		@Inject private CurrentlyEditedUser ceu;
		
		public List<User> getUsers() {
			return repository.getUsers();
		}

		public String edit(String id) {
			if (id.equals(""))
				id = null;
			ceu.setCurrentUser(id);
			return Utils.appendRedirect("admUser.xhtml");
		}
		
		public String remove(String id) {
			if (as.getCurrentUser().getLogin().equals(id))
				return "";
			repository.remove(repository.find(id, User.class));
			return "";
		}
	}
	
	@Inject
	private MinistagRepository repository;
	
	@Inject
	private ActiveSession session;
	
	@Inject
	private LoginRequest lr;
	
	@Inject
	private LoginTarget loginTarget;
	
	private String defaultRedirect = "login.xhtml?faces-redirect=true&includeViewParams=true";
	
	private String loginRedirect = "login.xhtml?faces-redirect=true&includeViewParams=true";

	/**
	 * Checks whether provided username or password is valid user or not
	 * @param login
	 * @param target
	 * @param loginRequest 
	 * @return redirect
	 */
	public String doLogin(String target, LoginRequest loginRequest) {
		User u = repository.find(loginRequest.getLogin(), User.class);
		if (u == null) {
			loginRequest.setLoginFailure(true);
			return loginRedirect + "&backurl=" + loginTarget.getUrl();
		}
		if (!Arrays.equals(Utils.toHex(u.getPassword()), Utils.hash(loginRequest.getPassword(), u.getSalt()))) {
			loginRequest.setLoginFailure(true);
			return loginRedirect + "&backurl=" + loginTarget.getUrl();
		}
		session.setCurrentUser(u);	
		return Utils.appendRedirect(loginTarget.getUrl());
	}
	
	/**
	 * Checks whether current user has any of roles or not
	 * @param roles (empty if you only need logged in check)
	 * @return false if no one is logged in or role is mismatched
	 */
	public boolean hasRole(String role) {
		Roles r = Roles.valueOf(role);
		if (session.getCurrentUser() == null)
			return false;
		return session.getCurrentUser().getRole() == r;
	}

	/**
	 * Checks if user is logged in before running the action. 
	 * 
	 * Action is run if current session contains logged-in user. Otherwise returns defaultRedirect.
	 * @param action to be run if user is logged in
	 * @return redirect page
	 */
	public String runPrivileged(Action action) {
		return runPrivileged(action, null);
	}
	
	/**
	 * Same as runPrivileged, but also checks for the role.
	 * @param action to be run if user is logged in and has required role
	 * @param role role to check against
	 * @return redirect page
	 */
	public String runPrivileged(Action action, Roles role) {
		if (session.getCurrentUser() == null) {
			lr.setLoginNeeded(true);
			return defaultRedirect + "&backurl=" + loginTarget.getUrl();
		}
		if (session.getCurrentUser().getRole() != role) {
			lr.setLoginNeeded(true);
			return defaultRedirect + "&backurl=" + loginTarget.getUrl();
		}
		String uri = action.run();
		if (uri == null) {
			return Utils.appendRedirect(loginTarget.getUrl());
		} else {
			return Utils.appendRedirect(uri);
		}
	}

	public String getDefaultRedirect() {
		return defaultRedirect;
	}

	public void setDefaultRedirect(String defaultRedirect) {
		this.defaultRedirect = defaultRedirect;
	}

	public String getLoginRedirect() {
		return loginRedirect;
	}

	public void setLoginRedirect(String loginRedirect) {
		this.loginRedirect = loginRedirect;
	}
}
