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
package org.eclipse.riena.navigation.ui.controllers;

import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.listener.ApplicationNodeListener;
import org.eclipse.riena.ui.ridgets.IStatuslineRidget;
import org.eclipse.riena.ui.ridgets.IStatuslineUIProcessRidget;
import org.eclipse.riena.ui.ridgets.IWindowRidget;
import org.eclipse.riena.ui.ridgets.listener.IWindowRidgetListener;

/**
 * The controller for the application
 */
public class ApplicationController extends NavigationNodeController<IApplicationNode> {

	private IWindowRidget applicationWindow;
	private IWindowRidgetListener windowRidgetListener;
	private IStatuslineRidget statuslineRidget;
	private boolean menuBarVisible;

	public ApplicationController(IApplicationNode applicationNode) {
		super(applicationNode);
		applicationNode.addListener(new ApplicationNodeListener() {

			@Override
			public void labelChanged(IApplicationNode source) {
				updateLabel();
			}

			@Override
			public void iconChanged(IApplicationNode source) {
				updateIcon();
			}
		});
	}

	boolean done = false;

	private NodeEventDelegation contextUpdater = new NodeEventDelegation();

	public IStatuslineRidget getStatuslineRidget() {
		if (!done) {
			if (statuslineRidget.getStatuslineUIProcessRidget() != null) {
				((IStatuslineUIProcessRidget) statuslineRidget.getStatuslineUIProcessRidget())
						.setContextLocator(contextUpdater);
			}
			done = true;
		}
		return statuslineRidget;
	}

	/**
	 * @param statuslineRidget
	 *            the statuslineRidget to set
	 */
	public void setStatuslineRidget(IStatuslineRidget statuslineRidget) {
		this.statuslineRidget = statuslineRidget;
	}

	public void setApplicationWindow(IWindowRidget pWindowRidget) {
		if (windowRidgetListener == null) {
			windowRidgetListener = new FrameListener();
		}
		applicationWindow = pWindowRidget;
		if (applicationWindow != null && windowRidgetListener != null) {
			applicationWindow.addWindowRidgetListener(windowRidgetListener);
		}
	}

	public void setVisible(boolean pVisible) {
		if (applicationWindow != null) {
			applicationWindow.setVisible(pVisible);
		}
	}

	private static class FrameListener implements IWindowRidgetListener {
		public void closed() {
		}

		public void activated() {
		}
	}

	/**
	 * @return the applicationWindow
	 */
	public IWindowRidget getApplicationWindow() {
		return applicationWindow;
	}

	/**
	 * @see org.eclipse.riena.ui.internal.ridgets.IRidgetContainer#configureRidgets()
	 */
	public void configureRidgets() {
		// nothing to do
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.controllers.NavigationNodeController#afterBind()
	 */
	@Override
	public void afterBind() {
		super.afterBind();
		updateLabel();
		updateIcon();
	}

	private void updateLabel() {
		applicationWindow.setTitle(getNavigationNode().getLabel());
	}

	private void updateIcon() {
		updateIcon(applicationWindow);
	}

	public void setMenubarVisible(boolean visible) {
		this.menuBarVisible = visible;
	}

	public boolean isMenubarVisible() {
		return menuBarVisible;
	}

}
