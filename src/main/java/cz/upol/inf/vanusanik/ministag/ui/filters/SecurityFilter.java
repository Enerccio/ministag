package cz.upol.inf.vanusanik.ministag.ui.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
import cz.upol.inf.vanusanik.ministag.ui.services.SecurityController.ActiveSession;

/**
 * Handles the security of the application.
 * Checks whether user is logged in for each section of website and if not,
 * redirects to the login. If incorrect user is logged in, redirects to index.xhtml.
 * 
 * @author enerccio
 *
 */
public class SecurityFilter implements Filter {

	@Inject
	private ActiveSession as;

	private Set<String> pathLog = new HashSet<String>();
	private Set<String> pathAdm = new HashSet<String>();
	private Set<String> pathGar = new HashSet<String>();
	private Set<String> pathTea = new HashSet<String>();
	private Set<String> pathStu = new HashSet<String>();
	private Set<String> allPaths = new HashSet<String>();

	@Override
	public void init(FilterConfig arg0) throws ServletException {

		pathAdm.add("/admApp.xhtml");
		pathAdm.add("/admDept.xhtml");
		pathAdm.add("/admEditDept.xhtml");
		pathAdm.add("/admUser.xhtml");
		pathAdm.add("/admUsers.xhtml");

		pathGar.add("/gmyDept.xhtml");
		pathGar.add("/gmyCourses.xhtml");
		pathGar.add("/gmyCourse.xhtml");
		pathGar.add("/gBlocks.xhtml");
		
		pathStu.add("/preregister.xhtml");
		pathStu.add("/schedule.xhtml");
		
		pathTea.add("/schedule.xhtml");

		allPaths.addAll(pathAdm);
		allPaths.addAll(pathGar);
		allPaths.addAll(pathTea);
		allPaths.addAll(pathStu);
		allPaths.addAll(pathLog);
	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) arg0;
		HttpServletResponse httpResponse = (HttpServletResponse) arg1;

		String uri = httpRequest.getRequestURI();

		String[] uriParts = uri.split("[#?]");
		String path = uriParts[0];
		path = path.replaceAll(Pattern.quote(httpRequest.getContextPath()), "");

		User u = as.getCurrentUser();

		for (String p : allPaths) {
			if (path.startsWith(p)) {
				if (u == null) {
					redirectLogin(httpRequest, httpResponse, path);
					return;
				}

				Roles r = u.getRole();
				boolean check = false;

				switch (r) {
				case ADMIN:
					check = pathAdm.contains(p);
					break;
				case GARANT:
					check = pathGar.contains(p);
					break;
				case STUDENT:
					check = pathStu.contains(p);
					break;
				case TEACHER:
					check = pathTea.contains(p);
					break;
				default:
					break;
				}

				if (!check) {
					redirectMain(httpRequest, httpResponse);
					return;
				}
			}
		}

		arg2.doFilter(arg0, arg1);
	}

	/**
	 * Redirects to the login.xhtml?backurl=[requested page]
	 * @param httpRequest
	 * @param httpResponse
	 * @param page 
	 * @throws IOException
	 */
	private void redirectLogin(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String page)
			throws IOException {
		httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml?backurl=" + page);
	}
	
	/**
	 * Redirects to the index.xhtml
	 * @param httpRequest
	 * @param httpResponse
	 * @throws IOException
	 */
	private void redirectMain(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws IOException {
		httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.xhtml");
	}

}
