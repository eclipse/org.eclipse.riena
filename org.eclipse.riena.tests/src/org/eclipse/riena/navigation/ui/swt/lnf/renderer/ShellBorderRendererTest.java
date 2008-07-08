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
package org.eclipse.riena.navigation.ui.swt.lnf.renderer;

import junit.framework.TestCase;

import org.eclipse.riena.navigation.ui.swt.lnf.ILnfKeyConstants;
import org.eclipse.riena.navigation.ui.swt.lnf.LnfManager;
import org.eclipse.riena.navigation.ui.swt.lnf.rienadefault.RienaDefaultLnf;

/**
 * Tests of the class <code>ShellBorderRenderer</code>.
 */
public class ShellBorderRendererTest extends TestCase {

	/**
	 * Test of the method <code>getCompelteBorderWidth</code>.
	 */
	public void testGetCompelteBorderWidth() {

		MyLnf lnf = new MyLnf();
		LnfManager.setLnf(lnf);
		lnf.initialize();
		ShellBorderRenderer renderer = new ShellBorderRenderer();

		lnf.setPadding(20);
		int expected = 20 + renderer.getBorderWidth();
		assertEquals(expected, renderer.getCompleteBorderWidth());

		lnf.removePadding();
		expected = renderer.getBorderWidth();
		assertEquals(expected, renderer.getCompleteBorderWidth());

		lnf.setPadding(1.2);
		expected = renderer.getBorderWidth();
		assertEquals(expected, renderer.getCompleteBorderWidth());

		renderer.dispose();

	}

	/**
	 * Look and Feel where it is possible to change the setting for padding.
	 */
	private class MyLnf extends RienaDefaultLnf {

		public void removePadding() {
			getSettingTable().remove(ILnfKeyConstants.TITLELESS_SHELL_PADDING);
		}

		public void setPadding(Object padding) {
			getSettingTable().put(ILnfKeyConstants.TITLELESS_SHELL_PADDING, padding);
		}

	}

}
