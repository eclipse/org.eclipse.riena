/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.core.util;

import java.util.Map;

import junit.framework.TestCase;

/**
 * Test stuff from {@code PropertiesUtils}.
 */
public class PropertiesUtilsTest extends TestCase {

	public void testAsMapNullString() {
		Map<String, String> map = PropertiesUtils.asMap(null);
		assertEquals(0, map.size());
	}

	public void testAsMapEmptyString() {
		Map<String, String> map = PropertiesUtils.asMap("");
		assertEquals(0, map.size());
	}

	public void testAsMap() {
		Map<String, String> map = PropertiesUtils.asMap("a=1,b=2");
		assertEquals("1", map.get("a"));
		assertEquals("2", map.get("b"));
	}

	public void testAsMapWithExpectationFullfill() {
		Map<String, String> map = PropertiesUtils.asMap("a=1,b=2", "a", "b");
		assertEquals("1", map.get("a"));
		assertEquals("2", map.get("b"));
	}

	public void testAsMapWithExpectationFails() {
		try {
			PropertiesUtils.asMap("a=1,b=2", "a", "c");
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	public void testAsMapIgnoreWhitespace() {
		Map<String, String> map = PropertiesUtils.asMap(" a= 1 ,  b = 2   ");
		assertEquals("1", map.get("a"));
		assertEquals("2", map.get("b"));
	}

	public void testAsMapFailMissingEquals() {
		try {
			PropertiesUtils.asMap(" a: 1 ,  b = 2   ");
			fail();
		} catch (IllegalArgumentException e) {
			// ok
		}
	}
}
