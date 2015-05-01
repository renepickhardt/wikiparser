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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 *
 * @author mruster
 */
public abstract class AbstractWikiObject {

	protected int recursiveAttributesAmount = -1;

	/**
	 * Iterates over all attributes of this class and returns this Object's values
	 * in an array. If values are missing, they are set to "!MISSING ENTRY".
	 * Likewise, inaccessible values are marked as "!INACCESSIBLE ENTRY".
	 * <p>
	 * @see #getAllAttributes()
	 * @return an array of {@code String}s containing all attributes of this
	 *         Object excluding {@code recursiveAttributesAmount}.
	 */
	public String[] toStringArray() {
		List<Field> attributes = this.getAllAttributes();
		String[] a = new String[this.getRecursiveAttributesAmount()];
		int i = 0;
		for (Field attribute : attributes) {
			try {
				Object currentValue = attribute.get(this);
				try {
					a[i++] = currentValue.toString();
				} catch (NullPointerException e) {
					a[i++] = "!MISSING ENTRY";
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				a[i++] = "!INACCESSIBLE ENTRY";
			}
		}
		return a;
	}

	/**
	 * <p>
	 * Sets {@code recursiveAttributesAmount} by invoking
	 * {@code getAllAttributes()} if attribute is not initialised yet.
	 * <p>
	 * @return the value of {@code recursiveAttributesAmount}.
	 */
	public int getRecursiveAttributesAmount() {
		if (this.recursiveAttributesAmount < 0) {
			this.recursiveAttributesAmount = this.getAllAttributes().size();
		}
		return this.recursiveAttributesAmount;
	}

	/**
	 * <p>
	 * Retrieve attributes of this class and the attributes of its attributes that
	 * are {@code AbstractWikiElement} instances. Hence, if an attribute is of
	 * type {@code AbstractWikiElement} itself, all its attributes will be added.
	 * The {@code AbstractWikiElement} then is not added.
	 * <p>
	 * Assume we have an {@code AbstractWikiElement} (short AWE) as follows:
	 * AWE1/AWE2/AWE3/[attr1, attr2]. Then the count will be 2 although the
	 * nesting hierarchy has three AWEs.
	 * <p>
	 * The {@code recursiveAttributesAmount} attribute is not counted neither is a
	 * possible {@code $assertionsDisabled"} attribute.
	 * <p>
	 * <b>Inherited attributes are ignored.</b>
	 * <p>
	 * @param c a {@code Class} of which not-inherited attributes will be
	 *          extracted.
	 * <p>
	 * @return the not-inherited attributes of this Object and all of his
	 *         sub-{@code AbstractWikiElement}s' attributes without the respective
	 *         {@code recursiveAttributesAmount} attribute itself.
	 */
	protected ArrayList<Field> getAWEAttributesRecursively(Class c) {
		ArrayList<Field> childrenAttributes = new ArrayList<>();
		Field[] attributesArray = c.getDeclaredFields();
		List<Field> attributes = Arrays.asList(attributesArray);

		for (Field attribute : attributes) { // add only AbstractWikiObject's attributes but not itself:
			try {
				AbstractWikiObject wikiObject = (AbstractWikiObject) attribute.get(this);
				childrenAttributes.addAll(wikiObject.getAWEAttributesRecursively(wikiObject.getClass()));
			} catch (IllegalAccessException | ClassCastException | NullPointerException e) {
				if (!"$assertionsDisabled".equals(attribute.getName())
						&& !"recursiveAttributesAmount".equals(attribute.getName())) {
					childrenAttributes.add(attribute);
				}
			}
		}
		return childrenAttributes;
	}

	/**
	 * <p>
	 * Retrieves all attributes including inherited ones. Also extracts
	 * {@code AbstractWikiElement}s. For details,
	 * <p>
	 * @see #getAWEAttributesRecursively(java.lang.Class)
	 * @return all attributes of this object with {@code AbstractWikiObject}
	 *         attributes extracted.
	 */
	protected ArrayList<Field> getAllAttributes() {
		ArrayList<Field> attributes = new ArrayList<>();
		for (Class<?> c = this.getClass(); c != null; c = c.getSuperclass()) {
			attributes.addAll(this.getAWEAttributesRecursively(c));
		}
		return attributes;
	}
}
