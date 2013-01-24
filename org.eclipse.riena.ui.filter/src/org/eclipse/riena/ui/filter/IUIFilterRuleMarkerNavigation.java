/*******************************************************************************
 * Copyright (c) 2007, 2013 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.filter;

/**
 * This filter rule adds a marker to a navigation node.
 */
public interface IUIFilterRuleMarkerNavigation extends IUIFilterRuleMarker {

	/**
	 * Sets the ID of the navigation node.
	 * 
	 * @param nodeId
	 *            ID of navigation node
	 */
	void setNode(String nodeId);

}
