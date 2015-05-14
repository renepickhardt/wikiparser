/*
 * Copyright (C) 2015 mruster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.renepickhardt.imessages.wikiparser.dataTypes;

/**
 *
 * @author mruster
 */
public class AbstractWikiContentElement extends AbstractWikiElement {

	protected User contributor;
	protected String comment;

	/**
	 * <p>
	 * This constructor ensures that there is always an existing contributor
	 * {@code User} object.
	 * <p>
	 */
	public AbstractWikiContentElement() {
		this.contributor = new User();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public User getUser() {
		return contributor;
	}

	public void setUser(User user) {
		this.contributor = user;
	}

	public void setUserName(String name) {
		this.contributor.setName(name);
	}

	public void setUserId(String id) {
		this.contributor.setId(id);
	}
}
