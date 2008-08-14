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
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.BindingException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.riena.core.marker.IMarker;
import org.eclipse.riena.ui.core.marker.MandatoryMarker;
import org.eclipse.riena.ui.ridgets.ISingleChoiceRidget;
import org.eclipse.riena.ui.ridgets.databinding.IUnboundPropertyObservable;
import org.eclipse.riena.ui.ridgets.databinding.UnboundPropertyWritableList;
import org.eclipse.riena.ui.ridgets.util.beans.ListBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Ridget for a {@link ChoiceComposite} widget with single selection.
 */
public class SingleChoiceRidget extends AbstractMarkableRidget implements ISingleChoiceRidget {

	/** A single selected bean Object. */
	private final WritableList optionsObservable;
	private final WritableValue selectionObservable;

	private Binding optionsBinding;
	private Binding selectionBinding;
	private String[] optionLabels;

	public SingleChoiceRidget() {
		optionsObservable = new WritableList();
		selectionObservable = new WritableValue();
		selectionObservable.addChangeListener(new IChangeListener() {
			public void handleChange(ChangeEvent event) {
				updateMarkers();
			}
		});
	}

	@Override
	protected void bindUIControl() {
		if (optionsBinding != null) {
			createChildren(getUIControl());
		}
	}

	@Override
	protected void checkUIControl(Object uiControl) {
		AbstractSWTRidget.assertType(uiControl, ChoiceComposite.class);
		if (uiControl != null) {
			ChoiceComposite composite = (ChoiceComposite) uiControl;
			Assert.isTrue(!composite.isMultipleSelection(), "expected single selection ChoiceComposite"); //$NON-NLS-1$
		}
	}

	@Override
	protected void unbindUIControl() {
		Composite control = getUIControl();
		disposeChildren(control);
	}

	// public methods
	// ///////////////

	@Override
	public ChoiceComposite getUIControl() {
		return (ChoiceComposite) super.getUIControl();
	}

	public void bindToModel(IObservableList optionValues, IObservableValue selectionValue) {
		Assert.isNotNull(optionValues, "optionValues"); //$NON-NLS-1$
		Assert.isNotNull(selectionValue, "selectionValue"); //$NON-NLS-1$
		bindToModel(optionValues, null, selectionValue);
	}

	public void bindToModel(Object listBean, String listPropertyName, Object selectionBean, String selectionPropertyName) {
		Assert.isNotNull(listBean, "listBean"); //$NON-NLS-1$
		Assert.isNotNull(listPropertyName, "listPropertyName"); //$NON-NLS-1$
		Assert.isNotNull(selectionBean, "selectionBean"); //$NON-NLS-1$
		Assert.isNotNull(selectionPropertyName, "selectionPropertyName"); //$NON-NLS-1$
		IObservableList optionValues = new UnboundPropertyWritableList(listBean, listPropertyName);
		IObservableValue selectionValue = PojoObservables.observeValue(selectionBean, selectionPropertyName);
		bindToModel(optionValues, null, selectionValue);
	}

	public void bindToModel(List<? extends Object> optionValues, List<String> optionLabels, Object selectionBean,
			String selectionPropertyName) {
		Assert.isNotNull(optionValues, "optionValues"); //$NON-NLS-1$
		Assert.isNotNull(selectionBean, "selectionBean"); //$NON-NLS-1$
		Assert.isNotNull(selectionPropertyName, "selectionPropertyName"); //$NON-NLS-1$
		IObservableList list = new UnboundPropertyWritableList(new ListBean(optionValues), ListBean.PROPERTY_VALUES);
		IObservableValue selection = PojoObservables.observeValue(selectionBean, selectionPropertyName);
		bindToModel(list, optionLabels, selection);
	}

	@Override
	public void updateFromModel() {
		assertIsBoundToModel();
		super.updateFromModel();
		if (optionsBinding.getModel() instanceof IUnboundPropertyObservable) {
			((IUnboundPropertyObservable) optionsBinding.getModel()).updateFromBean();
		}
		optionsBinding.updateModelToTarget();
		Object oldSelection = selectionObservable.getValue();
		selectionBinding.updateModelToTarget();
		ChoiceComposite control = getUIControl();
		disposeChildren(control);
		createChildren(control);
		firePropertyChange(PROPERTY_SELECTION, oldSelection, selectionObservable.getValue());
	}

	public Object getSelection() {
		return selectionObservable.getValue();
	}

	@Deprecated
	public IObservableValue getSelectionObservable() {
		return selectionObservable;
	}

	public void setSelection(Object candidate) {
		assertIsBoundToModel();
		if (candidate != null && !optionsObservable.contains(candidate)) {
			throw new BindingException("candidate not in option list: " + candidate); //$NON-NLS-1$
		}
		Object oldSelection = selectionObservable.getValue();
		selectionObservable.setValue(candidate);
		updateChildren(getUIControl());
		firePropertyChange(PROPERTY_SELECTION, oldSelection, candidate);
	}

	public IObservableList getObservableList() {
		return optionsObservable;
	}

	@Override
	public final boolean isDisableMandatoryMarker() {
		return selectionObservable != null && selectionObservable.getValue() != null;
	}

	// helping methods
	// ////////////////

	private void assertIsBoundToModel() {
		if (optionsBinding == null || selectionBinding == null) {
			throw new BindingException("ridget not bound to model"); //$NON-NLS-1$
		}
	}

	private void bindToModel(IObservableList optionValues, List<String> optionLabels, IObservableValue selectionValue) {
		if (optionLabels != null) {
			String msg = "Mismatch between number of optionValues and optionLabels"; //$NON-NLS-1$
			Assert.isLegal(optionValues.size() == optionLabels.size(), msg);
		}

		unbindUIControl();

		// clear observables as they may be bound to another model
		// must dispose old bindings first to avoid updating the old model
		if (optionsBinding != null) {
			optionsBinding.dispose();
			optionsBinding = null;
			optionsObservable.clear();
		}
		if (selectionBinding != null) {
			selectionBinding.dispose();
			selectionBinding = null;
			selectionObservable.setValue(null);
		}

		// set up new binding
		DataBindingContext dbc = new DataBindingContext();
		optionsBinding = dbc.bindList(optionsObservable, optionValues, new UpdateListStrategy(
				UpdateListStrategy.POLICY_UPDATE), new UpdateListStrategy(UpdateListStrategy.POLICY_ON_REQUEST));
		selectionBinding = dbc.bindValue(selectionObservable, selectionValue, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE), new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST));
		if (optionLabels != null) {
			this.optionLabels = optionLabels.toArray(new String[optionLabels.size()]);
		} else {
			this.optionLabels = null;
		}

		bindUIControl();
	}

	private void createChildren(Composite control) {
		if (control != null && !control.isDisposed()) {
			Object[] values = optionsObservable.toArray();
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				String caption = optionLabels != null ? optionLabels[i] : String.valueOf(value);

				Button button = new Button(control, SWT.RADIO);
				button.setText(caption);
				button.setForeground(control.getForeground());
				button.setBackground(control.getBackground());
				button.setData(value);
				if (selectionObservable.getValue() == value) {
					button.setSelection(true);
				}
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button button = (Button) e.widget;
						Object data = button.getData();
						if (button.getSelection()) {
							SingleChoiceRidget.this.setSelection(data);
						}
					}
				});
			}
			control.layout(true);
		}
	}

	private void disposeChildren(Composite control) {
		if (control != null && !control.isDisposed()) {
			for (Control child : control.getChildren()) {
				child.dispose();
			}
		}
	}

	private void updateChildren(Composite control) {
		if (control != null && !control.isDisposed()) {
			Object selection = selectionObservable.getValue();
			for (Control child : control.getChildren()) {
				Button button = (Button) child;
				boolean isSelected = (selection != null) && (child.getData() == selection);
				button.setSelection(isSelected);
			}
		}
	}

	private void updateMarkers() {
		boolean isMandatoryDisabled = isDisableMandatoryMarker();
		for (IMarker marker : getMarkersOfType(MandatoryMarker.class)) {
			MandatoryMarker mMarker = (MandatoryMarker) marker;
			mMarker.setDisabled(isMandatoryDisabled);
		}
	}

}
