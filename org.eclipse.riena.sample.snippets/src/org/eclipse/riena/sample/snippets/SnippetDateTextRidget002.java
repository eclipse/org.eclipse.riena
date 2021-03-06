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
package org.eclipse.riena.sample.snippets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.beans.common.DateBean;
import org.eclipse.riena.ui.ridgets.IDateTextRidget;
import org.eclipse.riena.ui.ridgets.ITextRidget;
import org.eclipse.riena.ui.ridgets.swt.SwtRidgetFactory;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;

/**
 * Shows how to use a {@link IDateTextRidget} with a 'dd.MM.yyyy' pattern, bound
 * against a Date value.
 */
public final class SnippetDateTextRidget002 {

	public static void main(final String[] args) {
		final Display display = Display.getDefault();
		try {
			final Shell shell = new Shell();
			shell.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.SUB_MODULE_BACKGROUND));
			GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).spacing(20, 10).applyTo(shell);

			UIControlsFactory.createLabel(shell, "Date (dd.MM.yyyy):"); //$NON-NLS-1$
			final Text txtInput = UIControlsFactory.createTextDate(shell);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(txtInput);

			UIControlsFactory.createLabel(shell, "Output (Date):"); //$NON-NLS-1$
			final Text txtOutput = UIControlsFactory.createText(shell);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(txtOutput);

			final IDateTextRidget rInput = (IDateTextRidget) SwtRidgetFactory.createRidget(txtInput);
			rInput.setFormat(IDateTextRidget.FORMAT_DDMMYYYY);
			rInput.setDirectWriting(true);

			final ITextRidget rOutput = (ITextRidget) SwtRidgetFactory.createRidget(txtOutput);
			rOutput.setOutputOnly(true);

			final DateBean bean = new DateBean(new Date());
			rInput.bindToModel(bean, DateBean.DATE_PROPERTY);
			rInput.updateFromModel();
			rOutput.bindToModel(bean, DateBean.DATE_PROPERTY);
			rOutput.updateFromModel();
			rInput.addPropertyChangeListener(ITextRidget.PROPERTY_TEXT, new PropertyChangeListener() {
				public void propertyChange(final PropertyChangeEvent evt) {
					rOutput.updateFromModel();
				}
			});

			shell.setSize(400, 200);
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} finally {
			display.dispose();
		}
	}

}
