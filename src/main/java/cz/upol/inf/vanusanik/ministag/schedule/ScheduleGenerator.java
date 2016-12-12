package cz.upol.inf.vanusanik.ministag.schedule;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import cz.upol.inf.vanusanik.ministag.model.entities.Timetable;
import cz.upol.inf.vanusanik.ministag.ui.tools.Utils;

/**
 * Generates image from schedule
 * 
 * @author enerccio
 *
 */
@ApplicationScoped
public class ScheduleGenerator {

	/**
	 * Represents single day in schedule
	 * 
	 * @author enerccio
	 *
	 */
	private static class DayRow {
		/**
		 * Day ID (0=Monday, 1=Tuesday, 2=Wednesday, 3=Thursday, 4=Friday)
		 */
		private int day;
		/**
		 * Ordered list of timetable entries. They shouldn't overlap
		 */
		private List<Timetable> ordered = new ArrayList<Timetable>();
	}

	@Inject
	private ScheduleRequest rq;

	/**
	 * Generates image for specified with and height. Content of image is taken
	 * from {@link ScheduleRequest}
	 * 
	 * @param width
	 * @param height
	 * @return
	 * @throws Exception
	 */
	public BufferedImage generate(int width, int height) throws Exception {
		CoordinateTranslator ct = new CoordinateTranslator(width, height);
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);

		doGenerate((Graphics2D) i.getGraphics(), ct);

		return i;
	}

	/**
	 * Generates schedule image to specified graphics using specified coordinate
	 * translator
	 * 
	 * @param graphics
	 * @param ct
	 * @throws Exception
	 */
	private void doGenerate(Graphics2D graphics, CoordinateTranslator ct) throws Exception {
		// add border
		ct.push(0.025, 0.05, 0.95, 0.9);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(ct.encodeX(0), ct.encodeY(0), ct.encodeX(1) - ct.encodeX(0), ct.encodeY(1) - ct.encodeY(0));

		if (rq.getTitle() != null && !"".equals(rq.getTitle())) {
			// will push ct
			addTitle(graphics, ct);
		}

		List<DayRow> dayRows = fillSchedule();
		draw(graphics, ct, dayRows);
	}

	/**
	 * Draw specific days to specified graphics using coordinate translator
	 * 
	 * @param graphics
	 * @param ct
	 * @param dayRows
	 */
	private void draw(Graphics2D graphics, CoordinateTranslator ct, List<DayRow> dayRows) {
		int cnt = dayRows.size() + 1; // header
		double perLine = 1.0 / cnt;

		// draw lines
		graphics.setColor(Color.BLACK);
		for (int i = 0; i < cnt; i++) {
			graphics.drawLine(ct.encodeX(0), ct.encodeY(perLine * i), ct.encodeX(1.0), ct.encodeY(perLine * i));
			graphics.drawLine(ct.encodeX(0), ct.encodeY(perLine * (i + 1)), ct.encodeX(1.0),
					ct.encodeY(perLine * (i + 1)));
		}

		ct.push(0, 0, 0.15, 1);

		graphics.drawLine(ct.encodeX(1.0), ct.encodeY(0), ct.encodeX(1.0), ct.encodeY(1.0));

		Font f = new Font("Helvetica", Font.PLAIN, 10);
		graphics.setFont(f);

		for (int i = 0; i < cnt; i++) {
			if (i != 0) {
				DayRow dr = dayRows.get(i - 1);
				ct.push(0, perLine * i, 1, perLine);

				graphics.setColor(Color.BLACK);
				writeAnchoredText(Utils.day2text(dr.day), graphics, ct);
				ct.pop();
			}
		}

		ct.pop();
		ct.push(0.15, 0, 0.85, perLine);

		int numBoxes = 16;
		double perBox = 1.0 / numBoxes;

		for (int i = 0; i < numBoxes; i++) {
			ct.push(i * perBox, 0, perBox, 1);

			graphics.setColor(Color.BLACK);
			writeAnchoredText((i + 6) + ":00", graphics, ct);

			ct.pop();
		}

		ct.pop();

		f = new Font("Helvetica", Font.PLAIN, 9);
		graphics.setFont(f);

		for (int i = 0; i < cnt; i++) {
			if (i == 0)
				continue;

			DayRow dr = dayRows.get(i - 1);
			ct.push(0.15, perLine * i, 0.85, perLine);

			for (Timetable t : dr.ordered) {
				Calendar c = Calendar.getInstance();
				c.setTime(t.getClassFrom());
				int startMinutes = c.get(Calendar.MINUTE) + (c.get(Calendar.HOUR_OF_DAY) * 60) - (6 * 60);
				c.setTime(t.getClassTo());
				int endMinutes = c.get(Calendar.MINUTE) + (c.get(Calendar.HOUR_OF_DAY) * 60) - (6 * 60);
				double fractDivider = 1.0 / (60 * numBoxes);

				ct.push(startMinutes * fractDivider, 0, (endMinutes - startMinutes) * fractDivider, 1);

				graphics.drawLine(ct.encodeX(0), ct.encodeY(0), ct.encodeX(0), ct.encodeY(1.0));

				ct.push(0, 0, 1, 0.5);
				writeAnchoredText(t.getBlock().getTaughtClass().getDept().displayShort(), graphics, ct);
				ct.pop();
				ct.push(0, 0.5, 1, 0.5);
				writeAnchoredText(t.getBlock().getTaughtClass().displayShort(), graphics, ct);
				ct.pop();

				graphics.drawLine(ct.encodeX(1.0), ct.encodeY(0), ct.encodeX(1.0), ct.encodeY(1.0));

				ct.pop();
			}

			ct.pop();
		}
	}

	/**
	 * Read the session scoped data bean and uses it to fill schedule as list of
	 * DayRows.
	 * 
	 * @return
	 */
	private List<DayRow> fillSchedule() {
		List<DayRow> drl = new ArrayList<DayRow>();
		for (int i = 0; i < 5; i++) {
			DayRow e = new DayRow();
			e.day = i;
			drl.add(e);
		}

		for (Timetable t : rq.getDrawList()) {
			int day = t.getDay();
			DayRow pdr = null;
			for (DayRow dr : drl) {
				if (dr.day == day) {
					boolean conflicts = false;
					for (Timetable tt : dr.ordered) {
						if ((tt.getClassFrom().before(t.getClassTo())) && (tt.getClassTo().after(t.getClassFrom()))) {
							conflicts = true;
							break;
						}
					}
					if (!conflicts) {
						pdr = dr;
						break;
					}
				}
			}
			if (pdr == null) {
				pdr = new DayRow();
				pdr.day = day;
				drl.add(pdr);
			}
			pdr.ordered.add(t);
		}

		Collections.sort(drl, new Comparator<DayRow>() {

			@Override
			public int compare(DayRow o1, DayRow o2) {
				return Integer.compare(o1.day, o2.day);
			}

		});
		for (DayRow d : drl) {
			Collections.sort(d.ordered, new Comparator<Timetable>() {

				@Override
				public int compare(Timetable o1, Timetable o2) {
					return o1.getClassFrom().compareTo(o2.getClassFrom());
				}
			});
		}
		return drl;
	}

	/**
	 * Adds title to the schedule image. Will push the passed in coordinate
	 * translator
	 * 
	 * @param graphics
	 * @param ct
	 * @throws Exception
	 */
	private void addTitle(Graphics2D graphics, CoordinateTranslator ct) throws Exception {
		graphics.setColor(Color.BLACK);
		ct.push(0, 0, 1, 0.1);

		Font f = new Font("Helvetica", Font.PLAIN, 12);
		graphics.setFont(f);

		writeAnchoredText(rq.getTitle(), graphics, ct);

		ct.pop();
		ct.push(0, 0.1, 1, 0.9);
	}

	/**
	 * Writes anchored text at specified coordinates. Anchored both at x and y.
	 * 
	 * @param text
	 * @param graphics
	 * @param ct
	 */
	private void writeAnchoredText(String text, Graphics2D graphics, CoordinateTranslator ct) {
		FontMetrics fm = graphics.getFontMetrics();
		int th = fm.getHeight();
		int tw = fm.charsWidth(text.toCharArray(), 0, text.toCharArray().length);

		double rw = ct.relativeX(tw);
		double rh = ct.relativeY(th);
		float x = ct.encodeX(0.5 - (rw / 2));
		float y = ct.encodeY(0.5 + (rh / 2));

		graphics.drawString(text, x, y);
	}
}
