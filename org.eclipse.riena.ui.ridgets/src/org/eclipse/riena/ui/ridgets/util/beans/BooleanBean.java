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
package org.eclipse.riena.ui.ridgets.util.beans;

/**
 * Boolean bean provides a boolean value for simple adapter UI-Binding
 */
public class BooleanBean extends AbstractBean {

	private boolean value;

	/**
	 * Creates a boolean bean with default value <code>false</code>
	 */
	public BooleanBean() {
		value = false;
	}

	/**
	 * Creates a boolean bean with the given value;
	 * 
	 * @param value
	 *            The value.
	 */
	public BooleanBean(boolean value) {
		this.value = value;
	}

	/**
	 * Creates a boolean bean with the given value;
	 * 
	 * @param value
	 *            The value.
	 */
	public BooleanBean(Boolean value) {
		this.value = value;
	}

	/**
	 * @return Returns the value.
	 */
	public boolean isValue() {
		return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(boolean value) {
		if (this.value != value) {
			boolean old = this.value;
			this.value = value;
			firePropertyChanged("value", old, this.value); //$NON-NLS-1$
		}
	}

}