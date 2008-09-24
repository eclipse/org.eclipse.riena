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
package org.eclipse.riena.example.client.navigation.model;

import org.eclipse.riena.example.client.application.ExampleIcons;
import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.NavigationArgument;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.model.SubApplicationNode;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtViewProvider;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtViewProviderAccessor;

public class NavigationSubApplicationNodeBuilder extends NavigationNodeBuilder {

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeBuilder#buildNode(org.eclipse.riena.navigation.NavigationNodeId,
	 *      org.eclipse.riena.navigation.NavigationArgument)
	 */
	public INavigationNode<?> buildNode(NavigationNodeId navigationNodeId, NavigationArgument navigationArgument) {
		SwtViewProvider presentation = SwtViewProviderAccessor.getViewProvider();

		ISubApplicationNode subApplication = new SubApplicationNode(navigationNodeId, "Navigation"); //$NON-NLS-1$
		subApplication.setIcon(createIconPath(ExampleIcons.ICON_APPLICATION));
		presentation.present(subApplication, "subapplication.1"); //$NON-NLS-1$
		subApplication.setSelected(true);

		IModuleGroupNode moduleGroup = new ModuleGroupNode(null);
		moduleGroup.setLabel("ModuleGroup 1.1"); //$NON-NLS-1$
		subApplication.addChild(moduleGroup);
		IModuleNode module = new ModuleNode(null, "Module 1.1.1"); //$NON-NLS-1$
		module.setIcon(createIconPath(ExampleIcons.ICON_APPLICATION));
		moduleGroup.addChild(module);
		ISubModuleNode subModule = new SubModuleNode(
				new NavigationNodeId("org.eclipse.riena.example.customerDetail"), "SubModule 1.1.1.1"); //$NON-NLS-1$ //$NON-NLS-2$
		subModule.setIcon(createIconPath(ExampleIcons.ICON_FILE));
		module.addChild(subModule);

		ISubModuleNode subModule2 = new SubModuleNode(
				new NavigationNodeId("org.eclipse.riena.example.customerDetail"), "SubModule 1.1.1.1"); //$NON-NLS-1$ //$NON-NLS-2$
		subModule.addChild(subModule2);

		subModule = new SubModuleNode(
				new NavigationNodeId("org.eclipse.riena.example.customerDetail"), "SubModule 1.1.1.2"); //$NON-NLS-1$ //$NON-NLS-2$
		module.addChild(subModule);
		module = new ModuleNode(null, "Module 1.1.2 (closeable)"); //$NON-NLS-1$
		module.setIcon(createIconPath(ExampleIcons.ICON_HOMEFOLDER));
		moduleGroup.addChild(module);
		subModule = new SubModuleNode(
				new NavigationNodeId("org.eclipse.riena.example.customerDetail"), "SubModule 1.1.2.1"); //$NON-NLS-1$ //$NON-NLS-2$
		module.addChild(subModule);
		/* NEW */
		subModule = new SubModuleNode(new NavigationNodeId("org.eclipse.riena.example.navigation"), "Navigation"); //$NON-NLS-1$ //$NON-NLS-2$
		module.addChild(subModule);

		moduleGroup = new ModuleGroupNode(null);
		moduleGroup.setLabel("ModuleGroup 1.2"); //$NON-NLS-1$
		moduleGroup.setPresentWithSingleModule(false);
		subApplication.addChild(moduleGroup);
		module = new ModuleNode(null, "Module 1.2.1 (not closeable)"); //$NON-NLS-1$
		module.setCloseable(false);
		module.setIcon(createIconPath(ExampleIcons.ICON_RED_LED));
		moduleGroup.addChild(module);
		subModule = new SubModuleNode(
				new NavigationNodeId("org.eclipse.riena.example.customerDetail"), "SubModule 1.2.1.1"); //$NON-NLS-1$ //$NON-NLS-2$
		module.addChild(subModule);
		subModule = new SubModuleNode(
				new NavigationNodeId("org.eclipse.riena.example.customerDetail"), "SubModule 1.2.1.2"); //$NON-NLS-1$ //$NON-NLS-2$
		module.addChild(subModule);

		return subApplication;
	}

}
