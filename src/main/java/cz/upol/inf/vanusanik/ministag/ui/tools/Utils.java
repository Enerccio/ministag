package cz.upol.inf.vanusanik.ministag.ui.tools;

import java.security.MessageDigest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Utils {

	/**
	 * Returns byte array as hex string
	 * @param hash
	 * @return
	 */
	public static String asHex(byte[] hash) {
		return Hex.encodeHexString(hash);
	}

	/**
	 * Decodes hex string as byte array
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
	 * Appends ?faces-redirect=true or &faces-redirect=true to redirect, based on the uri
	 * @param url
	 * @return
	 */
	public static String appendRedirect(String url) {
		if (url == null || url.equals("") || url.contains("faces-redirect=true"))
			return url;
		return url.contains("?") ? url + "&faces-redirect=true" : url + "?faces-redirect=true";
	}

	public static String redirect(String message, String uri) {
		return "redirect.xhtml?redirectTo=" + uri + "&msg="+message;
	}

}
