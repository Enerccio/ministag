package cz.upol.inf.vanusanik.ministag.schedule;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 5850204032730392319L;

	public static final String URI = "/draw";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String ENCODING = "encoding";

	@Inject
	private ImageOutputWriter outputWriter;
	@Inject
	private ScheduleGenerator outputGenerator;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (req.getPathInfo() == null || !req.getPathInfo().equals(URI)) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
		resp.setDateHeader("Expires", 0);

		String strw = req.getParameter(WIDTH);
		String strh = req.getParameter(HEIGHT);
		String encoding = req.getParameter(ENCODING);

		if (strw == null || strh == null || encoding == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		int w, h;
		try {
			w = Integer.parseInt(strw);
			h = Integer.parseInt(strh);
		} catch (NumberFormatException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (w == 0 || h == 0) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			BufferedImage i = outputGenerator.generate(w, h);
			outputWriter.generate(i, encoding, resp);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

}
