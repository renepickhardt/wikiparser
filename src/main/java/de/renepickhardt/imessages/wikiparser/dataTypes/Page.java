package de.renepickhardt.imessages.wikiparser.dataTypes;

import java.util.ArrayList;
import org.apache.commons.validator.routines.InetAddressValidator;

public class Page extends AbstractWikiElement {

	private String action;
	private String id;
	private ArrayList<Revision> revisions;

	/**
	 * Initialises the {@code revisions} list so that elements can be added to it.
	 */
	public Page() {
		this.revisions = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ArrayList<Revision> getRevision() {
		return revisions;
	}

	public void setRevision(ArrayList<Revision> revisions) {
		this.revisions = revisions;
	}

	/**
	 *
	 * @param revision revision to add to the list of {@code revisions}.
	 * <p>
	 * @return {@code true} if the addition completed successfully.
	 */
	public boolean addRevision(Revision revision) {
		return this.revisions.add(revision);
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
