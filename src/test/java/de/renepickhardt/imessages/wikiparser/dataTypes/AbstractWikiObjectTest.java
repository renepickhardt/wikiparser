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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mruster
 */
public class AbstractWikiObjectTest {

	private LogItem instance;

	public AbstractWikiObjectTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		instance = new LogItem();
		instance.setAction("test action");
		instance.setComment("test comment");
		instance.setTimestamp("2014-30-04");
		instance.setTitle("test title");
		instance.setUserId("test user ID");
		instance.setUserName("test user name");
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of toStringArray method, of class AbstractWikiObject.
	 */
	@Test
	public void testToStringArray() {
		System.out.println("toStringArray");
		String[] expResult = null;
		String[] result = instance.toStringArray();
		assertArrayEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("Not implemented yet.");
	}

	/**
	 * Test of getAttributesAmountRecursively method, of class AbstractWikiObject.
	 */
	@Test
	public void testGetAttributesAmountRecursively() {
		System.out.println("getAttributesAmountRecursively");

		int expResult = 6;
		int result = instance.getRecursiveAttributesAmount();

		assertEquals(expResult, result);
	}
}
