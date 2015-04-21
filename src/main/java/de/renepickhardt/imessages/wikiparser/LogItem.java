package de.renepickhardt.imessages.wikiparser;

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
		// TODO Auto-generated method stub
		return "ts: " + timestamp + "\tuser: " + user + "\ttitle: " + title + "\tcomment:" + comment;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public static final String IPV4_NETWORK_REGEX = "\\A(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(/([1-9]|[1-2]d|3[0-2]))\\z";
	public static final String IPV4_REGEX = "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
	public static final String IPV6_REGEX = "\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z";

	boolean isTitleAnIpAddress() {
		try {
			/**
			 * Truncates "Benutzer:" from the title
			 */
			String titleSubstring = title.substring(9);
			if (titleSubstring.matches(IPV4_REGEX)
					|| titleSubstring.matches(IPV4_NETWORK_REGEX)
					|| titleSubstring.matches(IPV6_REGEX)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

}
