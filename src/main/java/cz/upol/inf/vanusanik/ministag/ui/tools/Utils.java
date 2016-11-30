package cz.upol.inf.vanusanik.ministag.ui.tools;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import cz.upol.inf.vanusanik.ministag.model.entities.EndingType;
import cz.upol.inf.vanusanik.ministag.model.entities.TimetableType;

/**
 * Utility functions. Both instance and out-of-instance variants.
 * Instance variants are used in jsp
 * @author enerccio
 *
 */
@ApplicationScoped
@Named("utils")
public class Utils {

	/**
	 * Returns short name for {@link EndingType}
	 * @param e
	 * @return
	 */
	public String endingType2Display(EndingType e) {
		switch (e) {
		case COLLOQUIUM:
			return "K";
		case CREDIT:
			return "Z";
		default:
			break;
		}
		return "";
	}

	/**
	 * Returns the longer name for {@link EndingType}
	 * @param e
	 * @return
	 */
	public String endingType2DisplayLonger(EndingType e) {
		switch (e) {
		case COLLOQUIUM:
			return "Kollokvium";
		case CREDIT:
			return "Zápočet";
		default:
			break;
		}
		return "";
	}

	/**
	 * Returns type of timetable for course
	 * @param t
	 * @return
	 */
	public String timetableType2Display(TimetableType t) {
		switch (t) {
		case LECTURE:
			return "Přednáška";
		case SEMINAR:
			return "Seminář";
		case SPECIAL:
			return "Cvičení";
		default:
			break;

		}
		return "";
	}

	/**
	 * Returns text form for day ID
	 * @param day
	 * @return
	 */
	public String day2display(int day) {
		return day2text(day);
	}

	/**
	 * Returns all days in order
	 * @return
	 */
	public List<Integer> getAllDays() {
		return Arrays.asList(new Integer[] { 0, 1, 2, 3, 4 });
	}

	/**
	 * Returns byte array as hex string
	 * 
	 * @param hash
	 * @return
	 */
	public static String asHex(byte[] hash) {
		return Hex.encodeHexString(hash);
	}

	/**
	 * Decodes hex string as byte array
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] toHex(String hex) {
		try {
			return Hex.decodeHex(hex.toCharArray());
		} catch (DecoderException e) {
			// TODO Logger
			return null;
		}
	}

	/**
	 * Performs salted hash check
	 * 
	 * @param password
	 * @return
	 */
	public static byte[] hash(String password, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes("utf-8"));
			return md.digest(password.getBytes("utf-8"));
		} catch (Exception e) {
			// TODO logger
			return null;
		}
	}

	/**
	 * Appends ?faces-redirect=true or &amp;faces-redirect=true to redirect, based
	 * on the uri
	 * 
	 * @param url
	 * @return
	 */
	public static String appendRedirect(String url) {
		if (url == null || url.equals("") || url.contains("faces-redirect=true"))
			return url;
		return url.contains("?") ? url + "&faces-redirect=true" : url + "?faces-redirect=true";
	}

	/**
	 * Handles the redirect request (with message)
	 * @param message
	 * @param uri
	 * @return
	 */
	public static String redirect(String message, String uri) {
		return "redirect.xhtml?redirectTo=" + uri + "&msg=" + message;
	}

	/**
	 * Returns calendar data for specific hour and minute
	 * @param h
	 * @param m
	 * @return
	 */
	public static Date calendarData(int h, int m) {
		Calendar c = Calendar.getInstance();
		c.set(0, 0, 0, h, m, 0);
		return c.getTime();
	}

	/**
	 * Forms basic uri for schedule request
	 * @param w
	 * @param h
	 * @param encType
	 * @return
	 */
	public static String showSchedule(int w, int h, String encType) {
		return "schedule/draw?width=" + w + "&height=" + h + "&encoding=" + encType;
	}

	/**
	 * Transforms day to string representation
	 * @param day
	 * @return
	 */
	public static String day2text(int day) {
		switch (day) {
		case 0:
			return "Pondělí";
		case 1:
			return "Uterý";
		case 2:
			return "Středa";
		case 3:
			return "Čtvrtek";
		case 4:
			return "Pátek";
		}
		return "";
	}
}
