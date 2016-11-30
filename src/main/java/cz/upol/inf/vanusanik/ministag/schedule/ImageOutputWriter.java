package cz.upol.inf.vanusanik.ministag.schedule;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Draws specific buffered image into the response.
 * Image encoding is dependent of the requested encoding
 * @author enerccio
 *
 */
@ApplicationScoped
public class ImageOutputWriter {

	public static final String ENCODING_PNG = "png";
	public static final String ENCODING_JPG = "jpg";
	public static final String ENCODING_GIF = "gif";

	/**
	 * Sends the buffered image to the response upstream
	 * @param i
	 * @param encoding
	 * @param resp
	 * @throws Exception
	 */
	public void generate(BufferedImage i, String encoding, HttpServletResponse resp) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		if (encoding.equals(ENCODING_PNG)) {
			ImageIO.write(i, "png", os);
			resp.setContentType("image/png");
		} else if (encoding.equals(ENCODING_JPG)) {
			ImageIO.write(i, "jpg", os);
			resp.setContentType("image/jpg");
		} else if (encoding.equals(ENCODING_GIF)) {
			ImageIO.write(i, "gif", os);
			resp.setContentType("image/gif");
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		int rqs = os.size();
		resp.setContentLength(rqs);
		IOUtils.copy(new ByteArrayInputStream(os.toByteArray()), resp.getOutputStream());
	}

}
