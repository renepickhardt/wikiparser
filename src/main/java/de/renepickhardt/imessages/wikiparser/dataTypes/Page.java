package de.renepickhardt.imessages.wikiparser.dataTypes;

import org.apache.commons.validator.routines.InetAddressValidator;

public class Page extends AbstractWikiElement {

	private String action;

	@Override
	public String toString() {
		return "ts: " + timestamp + "\tuser: " + contributor + "\ttitle: " + title + "\tcomment:" + comment + "\taction:" + action;
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
