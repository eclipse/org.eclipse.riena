/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.filter.extension;

import org.eclipse.riena.core.injector.extension.ExtensionInterface;

/**
 * Mappers for all rules.
 * <p>
 * <b>Note:</b> The "org.eclipse.riena.filter.rulemapper" is @deprecated.
 */
@ExtensionInterface(id = "org.eclipse.riena.filter.rulemapper,ruleMapper")
public interface IRuleMapperExtension {

	IRuleMarkerRidgetMapper getRidgetHiddenMarker();

	IRuleMarkerRidgetMapper getRidgetDisabledMarker();

	IRuleMarkerRidgetMapper getRidgetOutputMarker();

	IRuleMarkerRidgetMapper getRidgetMandatoryMarker();

	IRuleMarkerRidgetMapper getMenuItemHiddenMarker();

	IRuleMarkerRidgetMapper getMenuItemDisabledMarker();

	IRuleMarkerNavigationMapper getNavigationHiddenMarker();

	IRuleMarkerNavigationMapper getNavigationDisabledMarker();

	IRuleValidatorRidgetMapper getRidgetValidator();

}
