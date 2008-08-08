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
package org.eclipse.riena.ui.ridgets.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 */
public class MaxLength implements IValidator {

	private int maxLength;

	public MaxLength(int length) {
		this.maxLength = length;
	}

	/**
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	public IStatus validate(Object value) {

		if (value == null || maxLength < 0) {
			return ValidationRuleStatus.ok();
		}

		if (value instanceof String) {
			String string = (String) value;
			int length = string.length();

			if (length > maxLength) {
				return ValidationRuleStatus.error(true, "'" + string + "' must not be longer than " + maxLength //$NON-NLS-1$ //$NON-NLS-2$
						+ " characters.", this); //$NON-NLS-1$
			}
			return ValidationRuleStatus.ok();
		} else {
			throw new ValidationFailure("MaxLength can only validate objects of type String."); //$NON-NLS-1$
		}
	}

}