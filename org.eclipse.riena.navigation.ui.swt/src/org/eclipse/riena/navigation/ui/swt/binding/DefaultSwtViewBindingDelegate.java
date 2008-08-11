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
package org.eclipse.riena.navigation.ui.swt.binding;

import org.eclipse.riena.navigation.ui.views.AbstractViewBindingDelegate;
import org.eclipse.riena.ui.ridgets.uibinding.IControlRidgetMapper;
import org.eclipse.riena.ui.swt.utils.SWTBindingPropertyLocator;
import org.eclipse.swt.widgets.Widget;

/**
 *
 */
public class DefaultSwtViewBindingDelegate extends AbstractViewBindingDelegate {

	public DefaultSwtViewBindingDelegate() {
		this(new DefaultSwtControlRidgetMapper());
	}

	@SuppressWarnings("unchecked")
	public DefaultSwtViewBindingDelegate(IControlRidgetMapper mapper) {
		super(new SWTBindingPropertyLocator(), mapper);
	}

	@Override
	public void addUIControl(Object uiControl, String bindingId) {
		super.addUIControl(uiControl, bindingId);
		if (uiControl instanceof Widget) {
			Widget widget = (Widget) uiControl;
			widget.setData(SWTBindingPropertyLocator.BINDING_PROPERTY, bindingId);
		}
	}

}
