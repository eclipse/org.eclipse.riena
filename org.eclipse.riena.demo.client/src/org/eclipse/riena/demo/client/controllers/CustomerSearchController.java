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
package org.eclipse.riena.demo.client.controllers;

import java.util.Date;

import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.demo.common.Customer;
import org.eclipse.riena.demo.common.ICustomerService;
import org.eclipse.riena.internal.demo.client.Activator;
import org.eclipse.riena.navigation.NavigationArgument;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.ILabelRidget;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.ITextRidget;
import org.eclipse.riena.ui.ridgets.swt.DateColumnFormatter;

/**
 * customer search view controller
 */
public class CustomerSearchController extends SubModuleController {

	private ICustomerService customerDemoService;

	public void bind(ICustomerService customerDemoService) {
		this.customerDemoService = customerDemoService;
	}

	public void unbind(ICustomerService customerDemoService) {
		this.customerDemoService = null;
	}

	private SearchBean customerSearchBean = new SearchBean();
	private SearchResult result = new SearchResult();

	@Override
	public void configureRidgets() {
		Inject.service(ICustomerService.class).into(this).andStart(
				Activator.getDefault().getBundle().getBundleContext());

		ITextRidget suchName = (ITextRidget) getRidget("searchLastName");
		suchName.bindToModel(customerSearchBean, "lastName");
		suchName.setMandatory(true);

		((ILabelRidget) getRidget("hits")).bindToModel(result, "hits");

		final ITableRidget kunden = ((ITableRidget) getRidget("result"));
		String[] columnNames = { "lastname", "firstname", "birthdate", "street", "city" };
		String[] propertyNames = { "lastName", "firstName", "birthDate", "address.street", "address.city" };

		kunden.bindToModel(result, "customers", Customer.class, propertyNames, columnNames);

		kunden.setColumnFormatter(2, new DateColumnFormatter("dd.mm.yyyy") {
			@Override
			protected Date getDate(Object element) {
				return ((Customer) element).getBirthDate();
			}
		});

		((IActionRidget) getRidget("search")).addListener(new IActionListener() {
			public void callback() {
				result.setCustomers(null);
				getRidget("result").updateFromModel();

				result.setCustomers(customerDemoService.search(null));

				getRidget("result").updateFromModel();
				getRidget("hits").updateFromModel();
			}
		});

		((IActionRidget) getRidget("new")).addListener(new IActionListener() {
			public void callback() {
				getNavigationNode().navigate(new NavigationNodeId("riena.demo.client.CustomerRecord"));

			}
		});

		((IActionRidget) getRidget("open")).addListener(new IActionListener() {
			public void callback() {
				int selectionIndex = kunden.getSelectionIndex();
				if (selectionIndex >= 0) {
					getNavigationNode().navigate(
							new NavigationNodeId("riena.demo.client.CustomerRecord", String.valueOf(selectionIndex)),
							new NavigationArgument(result.getCustomers().get(selectionIndex)));
				}
			}
		});
	}
}
