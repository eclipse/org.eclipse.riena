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
package org.eclipse.riena.navigation.ui.swt.viewprovider;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.riena.tests.collect.NonGatherableTestCase;

/**
 *
 */
@NonGatherableTestCase("This is not a �TestCase�!")
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.riena.navigation.ui.swt.viewprovider");
		// $JUnit-BEGIN$
		suite.addTestSuite(SwtViewIdTest.class);
		suite.addTestSuite(SwtViewProviderTest.class);
		// $JUnit-END$
		return suite;
	}

}
