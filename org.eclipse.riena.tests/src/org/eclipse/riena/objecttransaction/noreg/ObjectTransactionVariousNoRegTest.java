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
package org.eclipse.riena.objecttransaction.noreg;

import org.eclipse.riena.objecttransaction.IObjectTransaction;
import org.eclipse.riena.objecttransaction.ObjectTransactionFactoryAccessor;
import org.eclipse.riena.objecttransaction.noreg.value.Kunde;
import org.eclipse.riena.tests.RienaTestCase;

/**
 * TODO Fehlender Klassen-Kommentar
 * 
 * @author Christian Campo
 */
public class ObjectTransactionVariousNoRegTest extends RienaTestCase {

	public void setUp() throws Exception {
		super.setUp();
		// this.setTraceOn(true);
		// trainModules("META-INF/hivetestmodule.xml");
		// replay();
	}

	public void tearDown() throws Exception {
		// verify();
		super.tearDown();
	}

	/**
	 * @throws Exception
	 */
	public void testPreRegisteredWithNoObjectId() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		assertTrue("nachname ist nicht Miller", kunde.getNachname().equals("Miller"));
		assertTrue("vorname ist nicht john", kunde.getVorname().equals("john"));
		assertTrue("kundenr ist nicht 4711", kunde.getKundennr().equals("4711"));

		// tests that you can register object with ObjectID null and that you
		// can switch the
		// cleanmodus to false
		kunde.setObjectId(null);
		objectTransaction.register(kunde);
		objectTransaction.setCleanModus(false);
		objectTransaction.importExtract(objectTransaction.exportExtract());
	}

}