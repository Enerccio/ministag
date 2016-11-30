package cz.upol.inf.vanusanik.ministag.ui.components;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

/**
 * InputTime component
 * @author enerccio
 *
 */
@FacesComponent("inputTime")
public class InputTime extends UIInput implements NamingContainer {

	private UIInput hour;
	private UIInput minute;

	/**
	 * As required by <cc:interface>.
	 */
	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

	/**
	 * Set initial hour and minute based on model.
	 */
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		Calendar calendar = Calendar.getInstance();
		Date date = (Date) getValue();

		if (date != null) {
			calendar.setTime(date);
		}

		hour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
		minute.setValue(calendar.get(Calendar.MINUTE));
		super.encodeBegin(context);
	}

	/**
	 * Returns the submitted value in HH-mm format.
	 */
	@Override
	public Object getSubmittedValue() {
		return hour.getSubmittedValue() + "-" + minute.getSubmittedValue();
	}

	/**
	 * Converts the submitted value to concrete {@link Date} instance.
	 */
	@Override
	protected Object getConvertedValue(FacesContext context, Object submittedValue) {
		try {
			return new SimpleDateFormat("HH-mm").parse((String) submittedValue);
		} catch (ParseException e) {
			throw new ConverterException(e);
		}
	}

	public UIInput getHour() {
		return hour;
	}

	public void setHour(UIInput hour) {
		this.hour = hour;
	}

	public UIInput getMinute() {
		return minute;
	}

	public void setMinute(UIInput minute) {
		this.minute = minute;
	}

}
