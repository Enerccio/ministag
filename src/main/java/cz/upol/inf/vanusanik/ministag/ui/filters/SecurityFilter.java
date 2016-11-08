package cz.upol.inf.vanusanik.ministag.ui.filters;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.upol.inf.vanusanik.ministag.model.entities.Roles;
import cz.upol.inf.vanusanik.ministag.model.entities.User;
import cz.upol.inf.vanusanik.ministag.ui.services.ActiveSession;

public class SecurityFilter implements Filter {
	
	@Inject	private ActiveSession as;
	
	private Map<String, Roles> paths = new LinkedHashMap<String, Roles>();
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// null means no role needed but still logged in check is required
		
		paths.put("/admApp.xhtml", Roles.ADMIN);
		paths.put("/admDept.xhtml", Roles.ADMIN);
		paths.put("/admEditDept.xhtml", Roles.ADMIN);
		paths.put("/admUser.xhtml", Roles.ADMIN);
		paths.put("/admUsers.xhtml", Roles.ADMIN);
	}
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)arg0;
		HttpServletResponse httpResponse = (HttpServletResponse) arg1;
		
		String uri = httpRequest.getRequestURI();

		String[] uriParts = uri.split("[#?]");
		String path = uriParts[0];
		path = path.replaceAll(Pattern.quote(httpRequest.getContextPath()), "");
		
		User u = as.getCurrentUser();
		
		for (String p : paths.keySet()) {
			if (path.startsWith(p)) {
				if (u == null) {
					redirect(httpRequest, httpResponse, path);
					return;
				}
				Roles rCheck = paths.get(p);
				if (rCheck != null && u.getRole() != null) {
					if (rCheck != u.getRole()) {
						redirect(httpRequest, httpResponse, path);
						return;	
					}
				}
			}
		}
		
		arg2.doFilter(arg0, arg1);
	}

	private void redirect(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String page) 
			throws IOException {
		httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml?backurl=" + page);
	}

}
