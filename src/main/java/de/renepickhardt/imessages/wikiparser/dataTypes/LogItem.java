package de.renepickhardt.imessages.wikiparser.dataTypes;

import org.apache.commons.validator.routines.InetAddressValidator;

public class LogItem extends AbstractWikiContentElement {

	protected String action;
	protected String timestamp;
	protected String title;

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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * In a block log, the block is issued on the user page of the affected user.
	 * Thus, the title of this entry identifies the (anonymous) user name of the
	 * blocked user. If it is an IP address, this method will return {@code true}.
	 * Likewise, if the user was identified to be a whole network, it will also
	 * return {@code true}.
	 *
	 * @see
	 * org.apache.commons.validator.routines.InetAddressValidator#isValid(java.lang.String)
	 * @return {@code true} iff the blocked user name is an IPv4 or IPv6 address.
	 */
	public boolean wasBlockedUserAnonymous() {
		try {
			/**
			 * Truncates "Benutzer:" from the title
			 */
			String titleSubstringDE = title.substring(9);
			/**
			 * Truncates "User:" from the title
			 */
			String titleSubstringEN = title.substring(5);

			// Sometimes, a whole subnet was blocked (e.g. 200.67.239.0/24)
			titleSubstringDE = titleSubstringDE.split("/")[0];
			titleSubstringEN = titleSubstringEN.split("/")[0];

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
