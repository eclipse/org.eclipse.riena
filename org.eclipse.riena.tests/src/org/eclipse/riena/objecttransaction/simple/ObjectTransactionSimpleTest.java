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
package org.eclipse.riena.objecttransaction.simple;

import org.eclipse.riena.internal.objecttransaction.impl.ObjectTransactionImpl;
import org.eclipse.riena.objecttransaction.IObjectTransaction;
import org.eclipse.riena.objecttransaction.IObjectTransactionExtract;
import org.eclipse.riena.objecttransaction.ObjectTransactionFactoryAccessor;
import org.eclipse.riena.objecttransaction.simple.value.Addresse;
import org.eclipse.riena.objecttransaction.simple.value.Kunde;
import org.eclipse.riena.objecttransaction.simple.value.Vertrag;
import org.eclipse.riena.objecttransaction.state.State;
import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.NonUITestCase;

/**
 * TODO Fehlender Klassen-Kommentar
 * 
 * @author Christian Campo
 */
@NonUITestCase
public class ObjectTransactionSimpleTest extends RienaTestCase {

	public void setUp() throws Exception {
		super.setUp();
		// this.setTraceOn(false);
		// trainModules("META-INF/hivetestmodule.xml");
		// replay();
	}

	public void tearDown() throws Exception {
		// verify();
		super.tearDown();
	}

	/**
	 * 
	 */
	public void testSimpleAllNew() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		assertTrue("nachname ist nicht Miller", kunde.getNachname().equals("Miller"));
		assertTrue("vorname ist nicht john", kunde.getVorname().equals("john"));
		assertTrue("kundenr ist nicht 4711", kunde.getKundennr().equals("4711"));

		Addresse addresse = new Addresse(true);
		kunde.setAddresse(addresse);

		assertTrue("address ist nicht gesetzt", kunde.getAddresse() != null);

		addresse.setOrt("Frankfurt");
		addresse.setPlz("60000");
		addresse.setStrasse("Münchnerstr.");
		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));

		showStatus("testSimpleAllNew", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleExistingKundeAllNew() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		assertTrue("nachname ist nicht Miller", kunde.getNachname().equals("Miller"));
		assertTrue("vorname ist nicht john", kunde.getVorname().equals("john"));
		assertTrue("kundenr ist nicht 4711", kunde.getKundennr().equals("4711"));
		objectTransaction.setCleanModus(false);

		Addresse addresse = new Addresse(true);
		kunde.setAddresse(addresse);

		assertTrue("address ist nicht gesetzt", kunde.getAddresse() != null);

		addresse.setOrt("Frankfurt");
		addresse.setPlz("60000");
		addresse.setStrasse("Münchnerstr.");
		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));

		showStatus("testSimpleExistingKundeAllNew", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleAllClean() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		assertTrue("nachname ist nicht Miller", kunde.getNachname().equals("Miller"));
		assertTrue("vorname ist nicht john", kunde.getVorname().equals("john"));
		assertTrue("kundenr ist nicht 4711", kunde.getKundennr().equals("4711"));

		Addresse addresse = new Addresse(true);
		kunde.setAddresse(addresse);
		assertTrue("address ist nicht gesetzt", kunde.getAddresse() != null);

		addresse.setOrt("Frankfurt");
		addresse.setPlz("60000");
		addresse.setStrasse("Münchnerstr.");

		// test within clean modus
		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));

		objectTransaction.setCleanModus(false);

		// test after clean modus was left
		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));

		showStatus("testSimpleAllClean", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleExistingKundeAllNewChangingAddresse() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Addresse addresse = new Addresse(true);
		kunde.setAddresse(addresse);

		addresse.setOrt("Frankfurt");
		addresse.setPlz("60000");
		addresse.setStrasse("Münchnerstr.");
		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));

		objectTransaction.registerAsDeleted(kunde.getAddresse());
		addresse = new Addresse(true);

		addresse.setOrt("München");
		addresse.setPlz("80000");
		addresse.setStrasse("Leopoldstrasse");

		assertTrue("ort in addresse ist nicht München", addresse.getOrt().equals("München"));
		assertTrue("plz in addresse ist nicht 80000", addresse.getPlz().equals("80000"));
		assertTrue("strasse ist nicht Leopoldstrasse", addresse.getStrasse().equals("Leopoldstrasse"));

		kunde.setAddresse(addresse);

		assertTrue("address ist nicht gesetzt", kunde.getAddresse() != null);

		showStatus("testSimpleExistingKundeAllNewChangingAddresse", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithNewKundeAndSetAndGet() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		assertTrue("kundenr=4711", kunde.getKundennr().equals("4711"));
		assertTrue("vorname=john", kunde.getVorname().equals("john"));
		assertTrue("nachname=miller", kunde.getNachname().equals("Miller"));

		showStatus("testSimpleWithNewKundeAndSetAndGet", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithNewKundeAndSubTransaction() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		assertTrue("kundenr=4711", kunde.getKundennr().equals("4711"));
		assertTrue("vorname=john", kunde.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde.getNachname().equals("Miller"));

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		kunde.setVorname("jane");
		kunde.setNachname("Stewart");
		assertTrue("vorname=jane", kunde.getVorname().equals("jane"));
		assertTrue("nachname=Stewart", kunde.getNachname().equals("Stewart"));

		showStatus("testSimpleWithNewKundeAndSubTransaction objectTransaction", objectTransaction);
		showStatus("testSimpleWithNewKundeAndSubTransaction subObjectTransaction", subObjectTransaction);

	}

	/**
	 * 
	 */
	public void testSimpleWithNewKundeAndSubTransactionAndCommit() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		assertTrue("kundenr=4711", kunde.getKundennr().equals("4711"));
		assertTrue("vorname=john", kunde.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde.getNachname().equals("Miller"));

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		subObjectTransaction.toString();
		kunde.setVorname("jane");
		kunde.setNachname("Stewart");
		assertTrue("vorname=jane", kunde.getVorname().equals("jane"));
		assertTrue("nachname=Stewart", kunde.getNachname().equals("Stewart"));

		subObjectTransaction.commit();

		assertTrue("vorname=jane", kunde.getVorname().equals("jane"));
		assertTrue("nachname=Stewart", kunde.getNachname().equals("Stewart"));

		showStatus("testSimpleWithNewKundeAndSubTransactionAndCommit subObjectTransaction", subObjectTransaction);
		showStatus("testSimpleWithNewKundeAndSubTransactionAndCommit objectTransaction", objectTransaction);

	}

	/**
	 * 
	 */
	public void testSimpleWithNewKundeAndSubTransactionWithNewAddress() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		assertTrue("kundenr=4711", kunde.getKundennr().equals("4711"));
		assertTrue("vorname=john", kunde.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde.getNachname().equals("Miller"));

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		subObjectTransaction.toString();
		kunde.setVorname("jane");
		kunde.setNachname("Stewart");

		assertTrue("vorname=jane", kunde.getVorname().equals("jane"));
		assertTrue("nachname=Stewart", kunde.getNachname().equals("Stewart"));
		Addresse addresse = new Addresse(true);
		kunde.setAddresse(addresse);

		addresse.setOrt("Frankfurt");
		addresse.setPlz("60000");
		addresse.setStrasse("Münchnerstr.");

		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));

		showStatus("testSimpleWithNewKundeAndSubTransactionWithNewAddress objectTransaction", objectTransaction);
		showStatus("testSimpleWithNewKundeAndSubTransactionWithNewAddress subObjectTransaction", subObjectTransaction);

	}

	/**
	 * 
	 */
	public void testSimpleWithNewKundeAndSubTransactionWithChangedAddress() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Addresse addresse = new Addresse(true);
		kunde.setAddresse(addresse);

		addresse.setOrt("Frankfurt");
		addresse.setPlz("60000");
		addresse.setStrasse("Münchnerstr.");
		assertTrue("kundenr=4711", kunde.getKundennr().equals("4711"));
		assertTrue("vorname=john", kunde.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde.getNachname().equals("Miller"));

		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		subObjectTransaction.toString();
		kunde.setVorname("jane");
		kunde.setNachname("Stewart");

		assertTrue("vorname=jane", kunde.getVorname().equals("jane"));
		assertTrue("nachname=Stewart", kunde.getNachname().equals("Stewart"));

		addresse = new Addresse(true);
		subObjectTransaction.registerAsDeleted(kunde.getAddresse());

		addresse.setOrt("München");
		addresse.setPlz("80000");
		addresse.setStrasse("Leopoldstrasse");
		kunde.setAddresse(addresse);

		assertTrue("ort in addresse ist nicht München", addresse.getOrt().equals("München"));
		assertTrue("plz in addresse ist nicht 80000", addresse.getPlz().equals("80000"));
		assertTrue("strasse ist nicht Leopoldstrasse", addresse.getStrasse().equals("Leopoldstrasse"));

		showStatus("testSimpleWithNewKundeAndSubTransactionWithChangedAddress objectTransaction", objectTransaction);
		showStatus("testSimpleWithNewKundeAndSubTransactionWithChangedAddress subObjectTransaction",
				subObjectTransaction);

	}

	/**
	 * 
	 */
	public void testSimpleWithNewKundeAndSubTransactionWithChangedAddressAndCommit() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Addresse addresse = new Addresse(true);
		kunde.setAddresse(addresse);

		addresse.setOrt("Frankfurt");
		addresse.setPlz("60000");
		addresse.setStrasse("Münchnerstr.");
		assertTrue("kundenr=4711", kunde.getKundennr().equals("4711"));
		assertTrue("vorname=john", kunde.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde.getNachname().equals("Miller"));

		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		subObjectTransaction.toString();
		kunde.setVorname("jane");
		kunde.setNachname("Stewart");

		assertTrue("vorname=jane", kunde.getVorname().equals("jane"));
		assertTrue("nachname=Stewart", kunde.getNachname().equals("Stewart"));

		addresse = new Addresse(true);
		subObjectTransaction.registerAsDeleted(kunde.getAddresse());

		addresse.setOrt("München");
		addresse.setPlz("80000");
		addresse.setStrasse("Leopoldstrasse");
		kunde.setAddresse(addresse);
		assertTrue("ort in addresse ist nicht München", addresse.getOrt().equals("München"));
		assertTrue("plz in addresse ist nicht 80000", addresse.getPlz().equals("80000"));
		assertTrue("strasse ist nicht Leopoldstrasse", addresse.getStrasse().equals("Leopoldstrasse"));

		subObjectTransaction.commit();

		assertTrue("vorname=jane", kunde.getVorname().equals("jane"));
		assertTrue("nachname=Stewart", kunde.getNachname().equals("Stewart"));

		assertTrue("ort in addresse ist nicht München", addresse.getOrt().equals("München"));
		assertTrue("plz in addresse ist nicht 80000", addresse.getPlz().equals("80000"));
		assertTrue("strasse ist nicht Leopoldstrasse", addresse.getStrasse().equals("Leopoldstrasse"));

		showStatus("testSimpleWithNewKundeAndSubTransactionWithChangedAddressAndCommit objectTransaction",
				objectTransaction);

	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndNewVertraege() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0816") == null);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);

		showStatus("testSimpleWithExistingKundeUndNewVertraege objectTransaction", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndNewAndRemoveVertraege1() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		kunde.removeVertrag(v1);
		objectTransaction.registerAsDeleted(v1);

		showStatus("testSimpleWithExistingKundeUndNewAndRemoveVertraege1 objectTransaction", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndNewAndRemoveVertraege2() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		kunde.removeVertrag("0815");

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		objectTransaction.registerAsDeleted(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		showStatus("testSimpleWithExistingKundeUndNewAndRemoveVertraege2 objectTransaction", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndNewAndRemoveVertraege3() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);

		kunde.removeVertrag("0815");

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag 0815 gefunden", kunde.getVertrag("0815") == null);

		objectTransaction.registerAsDeleted(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		showStatus("testSimpleWithExistingKundeUndNewAndRemoveVertraege2 objectTransaction", objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndNewVertraegeInSubTransaction() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0816") == null);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);

		showStatus("testSimpleWithExistingKundeUndNewVertraegeInSubTransaction objectTransaction", objectTransaction);
		showStatus("testSimpleWithExistingKundeUndNewVertraegeInSubTransaction subObjectTransaction",
				subObjectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndNewVertraegeInSubTransactionAndCommit() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0816") == null);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);

		subObjectTransaction.commit();

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("Es gibt nicht 2 Verträge sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 2);

		showStatus("testSimpleWithExistingKundeUndNewVertraegeInSubTransactionAndCommit objectTransaction",
				objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndRemoveVertraegeInSubTransaction() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		kunde.removeVertrag(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		subObjectTransaction.registerAsDeleted(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);
		assertTrue("Es gibt nicht 1 Vertrag sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 1);

		showStatus("testSimpleWithExistingKundeUndRemoveVertraegeInSubTransaction objectTransaction", objectTransaction);
		showStatus("testSimpleWithExistingKundeUndRemoveVertraegeInSubTransaction subObjectTransaction",
				subObjectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndRemoveVertraegeInSubTransactionAndCommit() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		kunde.removeVertrag(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		subObjectTransaction.registerAsDeleted(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		subObjectTransaction.commit();

		showStatus("testSimpleWithExistingKundeUndRemoveVertraegeInSubTransactionAndCommit objectTransaction",
				objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndRemoveVertraegeInSubTransactionAndCommitWithList() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		assertTrue("Es gibt nicht 2 Verträge sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 2);

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		kunde.removeVertrag(v1);
		assertTrue("Es gibt nicht 1 Vertrag sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 1);

		subObjectTransaction.registerAsDeleted(v1);

		subObjectTransaction.commit();
		assertTrue("Es gibt nicht 1 Vertrag sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 1);

		showStatus("testSimpleWithExistingKundeUndRemoveVertraegeInSubTransactionAndCommitWithList objectTransaction",
				objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndRemoveVertraegeInSubTransactionAndCommitRoot() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0815") == v1);
		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		kunde.removeVertrag(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		subObjectTransaction.registerAsDeleted(v1);

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		subObjectTransaction.commit();

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		objectTransaction.commitToObjects();

		assertTrue("falscher oder keine Vertrag beim Kunden gefunden", kunde.getVertrag("0816") == v2);
		assertTrue("nicht existierender Vertrag gefunden", kunde.getVertrag("0815") == null);

		showStatus("testSimpleWithExistingKundeUndRemoveVertraegeInSubTransactionAndCommit objectTransaction",
				objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndRemoveVertraegeInSubTransactionAndCommitRootWithList() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		assertTrue("Es gibt nicht 2 Verträge sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 2);

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);
		kunde.removeVertrag(v1);
		assertTrue("Es gibt nicht 1 Vertrag sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 1);

		subObjectTransaction.registerAsDeleted(v1);

		subObjectTransaction.commit();
		assertTrue("Es gibt nicht 1 Vertrag sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 1);
		objectTransaction.commitToObjects();
		assertTrue("Es gibt nicht 1 Vertrag sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 1);

		showStatus("testSimpleWithExistingKundeUndRemoveVertraegeInSubTransactionAndCommitWithList objectTransaction",
				objectTransaction);
	}

	/**
	 * 
	 */
	public void testSimpleWithExistingKundeUndAddVertragInSubTransactionAndCommit() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		IObjectTransaction subObjectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createSubObjectTransaction(objectTransaction);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		assertTrue("Es gibt nicht 2 Verträge sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 2);

		subObjectTransaction.commit();
		assertTrue("Es gibt nicht 2 Vertrag sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 2);
		objectTransaction.commitToObjects();
		assertTrue("Es gibt nicht 2 Vertrag sondern :" + kunde.listVertrag().length, kunde.listVertrag().length == 2);

		showStatus("testSimpleWithExistingKundeUndAddVertragInSubTransactionAndCommit objectTransaction",
				objectTransaction);
	}

	/**
	 * 
	 */
	public void testImport() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		// simulating a Kunde from the database
		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		assertTrue("vorname=john", kunde.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde.getNachname().equals("Miller"));

		// making changes to the object
		objectTransaction.setCleanModus(false);
		kunde.setVorname("jane");
		kunde.setNachname("Stewart");

		// extracting recorded changes
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		// create new objecttransaction
		IObjectTransaction objectTransaction2 = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();
		// simulate of reloading the same data from the database as before
		objectTransaction2.setCleanModus(true);
		Kunde kunde2 = new Kunde("4711");
		kunde2.setVorname("john");
		kunde2.setNachname("Miller");

		assertTrue("vorname=john", kunde2.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde2.getNachname().equals("Miller"));

		// apply the changes from the other objecttransaction's extract
		objectTransaction2.setCleanModus(false);
		objectTransaction2.importExtract(extract);
		// ---> properties have changed
		assertTrue("vorname=john", kunde2.getVorname().equals("jane"));
		assertTrue("nachname=Miller", kunde2.getNachname().equals("Stewart"));
	}

	/**
	 * 
	 */
	public void testImport2() {
		IObjectTransaction objectTransaction = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();

		// simulating a Kunde from the database
		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		assertTrue("vorname=john", kunde.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde.getNachname().equals("Miller"));

		Vertrag v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde.addVertrag(v1);

		Vertrag v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde.addVertrag(v2);

		// making changes to the object
		objectTransaction.setCleanModus(false);
		kunde.setVorname("jane");
		kunde.setNachname("Stewart");

		kunde.removeVertrag("0815");

		// extracting recorded changes
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		// create new objecttransaction
		IObjectTransaction objectTransaction2 = ObjectTransactionFactoryAccessor.fetchObjectTransactionFactory()
				.createObjectTransaction();
		// simulate of reloading the same data from the database as before
		objectTransaction2.setCleanModus(true);
		Kunde kunde2 = new Kunde("4711");
		kunde2.setVorname("john");
		kunde2.setNachname("Miller");

		assertTrue("vorname=john", kunde2.getVorname().equals("john"));
		assertTrue("nachname=Miller", kunde2.getNachname().equals("Miller"));

		v1 = new Vertrag("0815");
		v1.setVertragsBeschreibung("mein erster Vertrag");
		kunde2.addVertrag(v1);

		v2 = new Vertrag("0816");
		v2.setVertragsBeschreibung("noch ein Vertrag");
		kunde2.addVertrag(v2);

		// apply the changes from the other objecttransaction extract
		objectTransaction2.setCleanModus(false);
		objectTransaction2.importExtract(extract);
		// ---> properties have changed
		assertTrue("vorname=john", kunde2.getVorname().equals("jane"));
		assertTrue("nachname=Miller", kunde2.getNachname().equals("Stewart"));
		assertTrue("vertrag 0815 must return null", kunde2.getVertrag("0815") == null);
		kunde2.removeVertrag("0816");
	}

	/**
	 * 
	 */
	public void testSimpleCheckVersion() {
		ObjectTransactionImpl objectTransaction = (ObjectTransactionImpl) ObjectTransactionFactoryAccessor
				.fetchObjectTransactionFactory().createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		assertTrue("nachname ist nicht Miller", kunde.getNachname().equals("Miller"));
		assertTrue("vorname ist nicht john", kunde.getVorname().equals("john"));
		assertTrue("kundenr ist nicht 4711", kunde.getKundennr().equals("4711"));
		Addresse addresse = new Addresse(true);
		kunde.setAddresse(addresse);
		assertTrue("address ist nicht gesetzt", kunde.getAddresse() != null);
		addresse.setOrt("Frankfurt");
		addresse.setPlz("60000");
		addresse.setStrasse("Münchnerstr.");
		assertTrue("ort in addresse ist nicht Frankfurt", addresse.getOrt().equals("Frankfurt"));
		assertTrue("plz in addresse ist nicht 60000", addresse.getPlz().equals("60000"));
		assertTrue("strasse ist nicht Münchnerstr.", addresse.getStrasse().equals("Münchnerstr."));
		assertTrue("kunde ist nicht CLEAN", objectTransaction.isState(kunde, State.CLEAN));
		assertTrue("addresse ist nicht CLEAN", objectTransaction.isState(addresse, State.CLEAN));
		objectTransaction.setCleanModus(false);

		assertTrue("kunde hat nicht version 1", kunde.getVersion().equals("1"));
		assertTrue("addresse hat nicht version 1", addresse.getVersion().equals("1"));

		addresse.setPlz("70000");
		objectTransaction.setVersionUpdate(addresse, "1.1");
		assertTrue("addresse hat nicht version 1.1", addresse.getVersion().equals("1.1"));
		assertTrue("addresse ist nicht MODIFIED", objectTransaction.isState(addresse, State.MODIFIED));

		showStatus("testSimpleCheckVersion", objectTransaction);
	}

	/**
	 * 
	 */
	public void testUpdateVersion() {
		ObjectTransactionImpl objectTransaction = (ObjectTransactionImpl) ObjectTransactionFactoryAccessor
				.fetchObjectTransactionFactory().createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);
		assertTrue("kunde ist nicht CLEAN", objectTransaction.isState(kunde, State.CLEAN));
		kunde.setVorname("jim");
		assertTrue("kunde ist nicht MODIFIED", objectTransaction.isState(kunde, State.MODIFIED));
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		// simulate remote system with new objecttransaction
		ObjectTransactionImpl objectTransaction2 = (ObjectTransactionImpl) ObjectTransactionFactoryAccessor
				.fetchObjectTransactionFactory().createObjectTransaction();
		objectTransaction2.setCleanModus(true);
		Kunde kunde2 = new Kunde("4711");
		kunde2.setVorname("john");
		kunde2.setNachname("Miller");
		assertTrue("kunde2 ist nicht CLEAN", objectTransaction2.isState(kunde2, State.CLEAN));
		objectTransaction2.setCleanModus(false);
		objectTransaction2.importExtract(extract);
		assertTrue("kunde2 ist nicht MODIFIED", objectTransaction2.isState(kunde2, State.MODIFIED));
		objectTransaction2.commitToObjects();
		assertTrue("kunde2 ist nicht CLEAN", objectTransaction2.isState(kunde2, State.CLEAN));
		objectTransaction2.setVersionUpdate(kunde2, "2");
		assertTrue("kunde2 ist nicht MODIFIED", objectTransaction2.isState(kunde2, State.MODIFIED));
		extract = objectTransaction2.exportExtract();

		// back on my localsystem
		objectTransaction.commitToObjects();
		assertTrue("kunde ist nicht CLEAN", objectTransaction.isState(kunde, State.CLEAN));
		objectTransaction.importExtract(extract);
		assertTrue("kunde ist nicht MODIFIED", objectTransaction.isState(kunde, State.MODIFIED));

		showStatus("testUpdateVersion", objectTransaction);
	}

	private void showStatus(String testName, IObjectTransaction objectTransaction) {
		System.out.println("testname >>>>>" + testName + "<<<<<");
		System.out.println(objectTransaction);
	}
}