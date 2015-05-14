package de.renepickhardt.imessages.wikiparser.dataTypes;

import java.util.ArrayList;

public class Page extends AbstractWikiElement {

	protected ArrayList<Revision> revisions;
	/**
	 * The namespace 4 is reserved for Wikipedia itself.
	 */
	protected int namespace;

	/**
	 * Initialises the {@code revisions} list so that elements can be added to it.
	 */
	public Page() {
		this.revisions = new ArrayList<>();
	}

	public ArrayList<Revision> getRevisions() {
		return revisions;
	}

	public void setRevisions(ArrayList<Revision> revisions) {
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

	public int getNamespace() {
		return namespace;
	}

	public void setNamespace(int namespace) {
		this.namespace = namespace;
	}
}
