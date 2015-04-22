package de.renepickhardt.imessages.wikiparser.dataTypes;

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
		return "ts: " + timestamp + "\tuser: " + user + "\ttitle: " + title + "\tcomment:" + comment;
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
			String titleSubstring = title.substring(9);
			InetAddressValidator ipAddressValidator = InetAddressValidator.getInstance();
			if (ipAddressValidator.isValid(titleSubstring)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
