/*******************************************************************************
 * Copyright (c) 2007, 2014 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.sample.app.client.mail;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Display;

import org.eclipse.riena.ui.swt.RienaMessageDialog;

public class MessagePopupHandler extends AbstractHandler {

	public Object execute(final ExecutionEvent event) {
		RienaMessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Open", "Open Message Dialog!"); //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	}
}
