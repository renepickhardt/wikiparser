package de.renepickhardt.imessages.wikiparser.dataTypes;

import java.lang.reflect.Field;
import org.apache.commons.validator.routines.InetAddressValidator;

public class LogItem {

	private String timestamp;
	private String user;
	private String title;
	private String comment;
	private String action;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "ts: " + timestamp + "\tuser: " + user + "\ttitle: " + title + "\tcomment:" + comment + "\taction:" + action;
	}

	/**
	 * Iterates over all attributes of this class and returns this Object's values
	 * in an array. If values are missing, they are set to "!MISSING ENTRY".
	 * Likewise, inaccessible values are marked as "!INACCESSIBLE ENTRY".
	 * <p>
	 * @return a array of {@code String}s containing all attributes of this
	 *         Object.
	 */
	public String[] toStringArray() {
		Field[] attributes = getClass().getDeclaredFields();
		String[] a = new String[attributes.length];
		for (int i = 0; i < a.length; i++) {
			try {
				Object currentValue = attributes[i].get(this);
				try {
					a[i] = currentValue.toString();
				} catch (NullPointerException e) {
					a[i] = "!MISSING ENTRY";
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				a[i] = "!INACCESSIBLE ENTRY";
			}
		}
		return a;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @see
	 * org.apache.commons.validator.routines.InetAddressValidator#isValid(java.lang.String)
	 * @return {@code true} iff the title is an IPv4 or IPv6 address.
	 */
	public boolean isTitleAnIpAddress() {
		try {
			/**
			 * Truncates "Benutzer:" from the title
			 */
			String titleSubstringDE = title.substring(9);
			/**
			 * Truncates "User:" from the title
			 */
			String titleSubstringEN = title.substring(5);
			InetAddressValidator ipAddressValidator = InetAddressValidator.getInstance();
			if (ipAddressValidator.isValid(titleSubstringDE)
					|| ipAddressValidator.isValid(titleSubstringEN)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
