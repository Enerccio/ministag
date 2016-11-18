package cz.upol.inf.vanusanik.ministag.schedule;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import cz.upol.inf.vanusanik.ministag.model.service.MinistagRepository;

@ApplicationScoped
public class ScheduleGenerator {
	
	@Inject private ScheduleRequest rq;
	@Inject private MinistagRepository r;

	public BufferedImage generate(int width, int height) throws Exception {
		CoordinateTranslator ct = new CoordinateTranslator(width, height);
		BufferedImage i = new BufferedImage(width, height, rq.isColored() ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_USHORT_GRAY);
		
		doGenerate((Graphics2D)i.getGraphics(), ct);
		
		return i;
	}

	private void doGenerate(Graphics2D graphics, CoordinateTranslator ct) throws Exception {		
		// add border
		ct.push(0.05, 0.05, 0.9, 0.9);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(ct.encodeX(0), ct.encodeY(0), ct.encodeX(1)-ct.encodeX(0), ct.encodeY(1)-ct.encodeY(0));
		
		if (rq.getTitle() != null && !"".equals(rq.getTitle())) {
			// will push ct
			addTitle(graphics, ct);
		}
	}

	private void addTitle(Graphics2D graphics, CoordinateTranslator ct) throws Exception {
		graphics.setColor(Color.BLACK);
		ct.push(0, 0, 1, 0.1);
		
		Font f = new Font("Helvetica", Font.PLAIN, 12);
		graphics.setFont(f);
		FontMetrics fm = graphics.getFontMetrics();
		int th = fm.getHeight();
		int tw = fm.charsWidth(rq.getTitle().toCharArray(), 0, rq.getTitle().toCharArray().length);
		
		double rw = ct.relativeX(tw);
		double rh = ct.relativeY(th);
		float x = ct.encodeX((1.0 - rw)/2);
		float y = ct.encodeY((1.0 - rh)/2);
		
		graphics.drawString(rq.getTitle(), x, y);
		
		ct.pop();
		ct.push(0, 0.1, 1, 0.9);
	}
	
}
