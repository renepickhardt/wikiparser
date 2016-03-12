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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author mruster
 */
public abstract class AbstractCSVPrintableElement {

	public final String MISSING_ENTRY_STRING = "!MISSING ENTRY";
	public final String INACCESSIBLE_ENTRY_STRING = "!INACCESSIBLE ENTRY";
	protected int attributesAmount = -1;

	/**
	 * @see #toStringList()
	 * @return the output of {@code toStringList()} transformed into an array.
	 */
	public String[] toStringArray() {
		ArrayList<String> l = this.toStringList();
		String[] a = new String[l.size()];
		return l.toArray(a);
	}

	/**
	 * Iterates over all attributes of this class and returns this Object's values
	 * in an array. If values are missing, they are set to "!MISSING ENTRY".
	 * Likewise, inaccessible values are marked as "!INACCESSIBLE ENTRY". {
	 * <p>
	 * {@code AbstractCSVPrintableElement}s are not returned as such. Instead,
	 * their values are extracted and returned.
	 * <p>
	 * @see #getAllAttributes()
	 * @return an array of {@code String}s containing all attributes of this
	 * Object excluding {@code attributesAmount}.
	 */
	public ArrayList<String> toStringList() {
		List<Field> attributes = this.getAllAttributes();
		ArrayList<String> l = new ArrayList<>();
		for (Field attribute : attributes) {
			try {
				try {
					try {
						AbstractCSVPrintableElement wikiObject = (AbstractCSVPrintableElement) attribute.get(this);
						l.addAll(wikiObject.toStringList());
					} catch (ClassCastException e) {
						Object currentValue = attribute.get(this);
						l.add(currentValue.toString());
					}
				} catch (NullPointerException e2) {
					l.add(this.MISSING_ENTRY_STRING);
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				l.add(this.INACCESSIBLE_ENTRY_STRING);
			}
		}
		return l;
	}

	/**
	 * <p>
	 * Sets {@code attributesAmount} by invoking {@code getAllAttributes()} if
	 * attribute is not initialised yet.
	 * <p>
	 * @return the value of {@code attributesAmount}.
	 */
	public int getAttributesAmount() {
		if (this.attributesAmount < 0) {
			this.getAllAttributes();
		}
		return this.attributesAmount;
	}

	/**
	 * <p>
	 * Retrieves all attributes including inherited ones.
	 * <p>
	 * The returned list <b>will be</b> sorted.
	 * <p>
	 * @see #sort(java.util.ArrayList)
	 * <p>
	 * Final fields are ignored just as {@code attributesAmount} and a potential
	 * {@code $assertionsDisabled} attribute.
	 * <p>
	 * @return all attributes of this object.
	 */
	protected ArrayList<Field> getAllAttributes() {
		ArrayList<Field> attributes = new ArrayList<>();
		for (Class<?> c = this.getClass(); c != null; c = c.getSuperclass()) {
			Field[] declaredFields = c.getDeclaredFields();
			for (Field field : declaredFields) {
				if (!"$assertionsDisabled".equals(field.getName())
								&& !"attributesAmount".equals(field.getName())
								&& !Modifier.isFinal(field.getModifiers())) {
					attributes.add(field);
				}
			}
		}
		this.attributesAmount = attributes.size();
		return this.sort(attributes);
	}

	/**
	 *
	 * @param l list with {@code Field}s that have the {@code name} attribute set.
	 * <p>
	 * @return {@code l} alphabetically sorted by its entries {@code name}.
	 */
	protected ArrayList<Field> sort(ArrayList<Field> l) {
		TreeMap<String, Field> map = new TreeMap<>();
		for (Field field : l) {
			map.put(field.getName(), field);
		}
		return new ArrayList<>(map.values());
	}
}
