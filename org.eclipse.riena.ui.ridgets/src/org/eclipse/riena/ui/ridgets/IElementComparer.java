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
package org.eclipse.riena.ui.ridgets;

/**
 * This interface is used to compare elements for equality. This allows the client to specify different equality criteria than the <code>equals</code>
 * implementation of the elements themselves.
 * 
 * @see IChoiceRidget#setComparer()
 * @since 5.0
 */
public interface IElementComparer {

	/**
	 * Compares two elements for equality
	 * 
	 * @param element1
	 *            the first element
	 * @param element2
	 *            the second element
	 * @return whether a is equal to b
	 */
	boolean equals(Object element1, Object element2);

}
