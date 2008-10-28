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
package org.eclipse.riena.sample.app.client.helloworld.controllers;

import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.sample.app.common.model.Customer;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.ITextRidget;

/**
 * Controller of the sub module that displays the details of a customer.
 */
public class CustomerDetailsSubModuleController extends SubModuleController {

	private ITextRidget numberFacade;
	private ITextRidget nameFacade;
	private ITextRidget firstnameFacade;
	private ITextRidget birthplaceFacade;
	private IActionRidget offersFacade;
	private IActionRidget saveFacade;

	public CustomerDetailsSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);
	}

	@Override
	public void afterBind() {
		super.afterBind();
		intializeControlBindings();
	}

	private void intializeControlBindings() {

		Customer customer = getCustomer();

		numberFacade.bindToModel(customer, "customerNumber"); //$NON-NLS-1$
		numberFacade.updateFromModel();

		nameFacade.bindToModel(customer, "lastName"); //$NON-NLS-1$
		nameFacade.updateFromModel();

		firstnameFacade.bindToModel(customer, "firstName"); //$NON-NLS-1$
		firstnameFacade.updateFromModel();

		birthplaceFacade.bindToModel(customer.getBirth(), "birthPlace"); //$NON-NLS-1$
		birthplaceFacade.updateFromModel();

		IActionListener callback = new SaveCallback();
		saveFacade.addListener(callback);

		callback = new OffersCallback();
		offersFacade.addListener(callback);

	}

	private Customer getCustomer() {
		return (Customer) getNavigationNode().getContext();
	}

	public ITextRidget getNumberFacade() {
		return numberFacade;
	}

	public void setNumberFacade(ITextRidget numberFacade) {
		this.numberFacade = numberFacade;
	}

	public ITextRidget getNameFacade() {
		return nameFacade;
	}

	public void setNameFacade(ITextRidget nameFacade) {
		this.nameFacade = nameFacade;
	}

	public ITextRidget getFirstnameFacade() {
		return firstnameFacade;
	}

	public void setFirstnameFacade(ITextRidget firstnameFacade) {
		this.firstnameFacade = firstnameFacade;
	}

	public ITextRidget getBirthplaceFacade() {
		return birthplaceFacade;
	}

	public void setBirthplaceFacade(ITextRidget birthplaceFacade) {
		this.birthplaceFacade = birthplaceFacade;
	}

	public IActionRidget getOffersFacade() {
		return offersFacade;
	}

	public void setOffersFacade(IActionRidget offersFacade) {
		this.offersFacade = offersFacade;
	}

	public IActionRidget getSaveFacade() {
		return saveFacade;
	}

	public void setSaveFacade(IActionRidget saveFacade) {
		this.saveFacade = saveFacade;
	}

	private static class SaveCallback implements IActionListener {

		public void callback() {
			System.out.println("Save is not implemented"); //$NON-NLS-1$
		}

	}

	private static class OffersCallback implements IActionListener {

		public void callback() {
			System.out.println("Offers is not implemented"); //$NON-NLS-1$
		}

	}

}
