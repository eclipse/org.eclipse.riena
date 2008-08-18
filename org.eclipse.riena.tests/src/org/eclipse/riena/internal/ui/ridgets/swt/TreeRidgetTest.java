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

import org.eclipse.riena.tests.FTActionListener;
import org.eclipse.riena.tests.TreeUtils;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.ITreeRidget;
import org.eclipse.riena.ui.ridgets.swt.uibinding.DefaultSwtControlRidgetMapper;
import org.eclipse.riena.ui.ridgets.tree2.ITreeNode;
import org.eclipse.riena.ui.ridgets.tree2.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Tests for the {@link TreeRidget}.
 * <p>
 * These tests use a TreeNode that wraps a String and focus on methods from the
 * {@link ITreeRidget}.
 * 
 * @see TreeRidgetTest2
 */
public class TreeRidgetTest extends AbstractSWTRidgetTest {

	private static final String ROOT_NODE_USER_OBJECT = "TestRootNode";
	private static final String ROOT_CHILD1_NODE_USER_OBJECT = "TestRootChild1Node";
	private static final String ROOT_CHILD2_NODE_USER_OBJECT = "TestRootChild2Node";
	private static final String ROOT_CHILD1_CHILD1_NODE_USER_OBJECT = "TestRootChild1Child1Node";
	private static final String ROOT_CHILD1_CHILD2_NODE_USER_OBJECT = "TestRootChild1Child2Node";
	private static final String ROOT_CHILD1_CHILD2_CHILD_NODE_USER_OBJECT = "TestRootChild1Child2ChildNode";

	private ITreeNode rootNode;
	private ITreeNode rootChild1Node;
	private ITreeNode rootChild2Node;
	private ITreeNode rootChild1Child2Node;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getRidget().bindToModel(initializeTreeModel(), ITreeNode.class, ITreeNode.PROPERTY_CHILDREN,
				ITreeNode.PROPERTY_PARENT, ITreeNode.PROPERTY_VALUE);
	}

	@Override
	protected Control createUIControl(Composite parent) {
		return new Tree(parent, SWT.MULTI);
	}

	@Override
	protected IRidget createRidget() {
		return new TreeRidget();
	}

	@Override
	protected Tree getUIControl() {
		return (Tree) super.getUIControl();
	}

	@Override
	protected ITreeRidget getRidget() {
		return (ITreeRidget) super.getRidget();
	}

	// testing methods
	// ////////////////

	public void testRidgetMapping() {
		DefaultSwtControlRidgetMapper mapper = new DefaultSwtControlRidgetMapper();
		assertSame(TreeRidget.class, mapper.getRidgetClass(getUIControl()));
	}

	public void testGetUIControl() throws Exception {
		Tree control = getUIControl();
		assertEquals(control, getRidget().getUIControl());
	}

	public void testBindToModel() {
		assertEquals(ROOT_NODE_USER_OBJECT, getUIControlItem(0).getText(0));
		assertEquals(ROOT_CHILD1_NODE_USER_OBJECT, getUIControlItem(1).getText(0));
		assertEquals(ROOT_CHILD1_CHILD1_NODE_USER_OBJECT, getUIControlItem(2).getText(0));
		assertEquals(ROOT_CHILD2_NODE_USER_OBJECT, getUIControlItem(3).getText(0));
	}

	public void testBindToModelNull() {
		ITreeRidget ridget = getRidget();
		ITreeNode[] roots = { rootNode };

		try {
			ridget.bindToModel(null, ITreeNode.class, ITreeNode.PROPERTY_CHILDREN, ITreeNode.PROPERTY_PARENT,
					ITreeNode.PROPERTY_VALUE);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		try {
			ridget.bindToModel(roots, null, ITreeNode.PROPERTY_CHILDREN, ITreeNode.PROPERTY_PARENT,
					ITreeNode.PROPERTY_VALUE);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		try {
			ridget.bindToModel(roots, ITreeNode.class, null, ITreeNode.PROPERTY_PARENT, ITreeNode.PROPERTY_VALUE);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		try {
			ridget.bindToModel(roots, ITreeNode.class, ITreeNode.PROPERTY_CHILDREN, null, ITreeNode.PROPERTY_VALUE);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		try {
			ridget.bindToModel(roots, ITreeNode.class, ITreeNode.PROPERTY_CHILDREN, ITreeNode.PROPERTY_PARENT, null);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}
	}

	public void testUpdateTreeFromModel() {
		ITreeRidget ridget = getRidget();
		Tree control = getUIControl();

		ridget.expandTree();

		assertEquals(1, control.getItemCount());
		TreeItem treeRoot = control.getItems()[0];
		assertEquals(ROOT_NODE_USER_OBJECT, treeRoot.getText());
		assertEquals(2, treeRoot.getItemCount());

		TreeItem treeRootChild1 = treeRoot.getItem(0);
		assertEquals(ROOT_CHILD1_NODE_USER_OBJECT, treeRootChild1.getText());
		TreeItem treeRootChild2 = treeRoot.getItem(1);
		assertEquals(ROOT_CHILD2_NODE_USER_OBJECT, treeRootChild2.getText());
		assertEquals(2, treeRootChild1.getItemCount());
		assertEquals(0, treeRootChild2.getItemCount());

		TreeItem treeRootChild1Child1 = treeRootChild1.getItem(0);
		assertEquals(ROOT_CHILD1_CHILD1_NODE_USER_OBJECT, treeRootChild1Child1.getText());
		TreeItem treeRootChild1Child2 = treeRootChild1.getItem(1);
		assertEquals(ROOT_CHILD1_CHILD2_NODE_USER_OBJECT, treeRootChild1Child2.getText());
		assertEquals(0, treeRootChild1Child1.getItemCount());
		assertEquals(1, treeRootChild1Child2.getItemCount());

		TreeItem treeRootChild1Child2Child = treeRootChild1Child2.getItem(0);
		assertEquals(ROOT_CHILD1_CHILD2_CHILD_NODE_USER_OBJECT, treeRootChild1Child2Child.getText());
		assertEquals(0, treeRootChild1Child2Child.getItemCount());
	}

	public void testUpdateOnModelChanges() {
		Tree control = getUIControl();

		assertEquals(3, TreeUtils.getItemCount(control));

		removeFromParent(rootChild2Node);

		assertEquals(2, TreeUtils.getItemCount(control));

		new TreeNode(rootNode, "TestNewNode1");
		new TreeNode(rootNode, "TestNewNode2");

		assertEquals(4, TreeUtils.getItemCount(control));
	}

	public void testExpandAndCollapseTree() {
		ITreeRidget ridget = getRidget();
		Tree control = getUIControl();

		assertEquals(3, TreeUtils.getItemCount(control));

		ridget.expandTree();

		assertEquals(6, TreeUtils.getItemCount(control));

		ridget.collapseTree();

		assertEquals(1, TreeUtils.getItemCount(control));

		// check that the tree was collapsed recursively...

		ridget.expand(rootNode);

		assertEquals(3, TreeUtils.getItemCount(control));

		ridget.expandTree();

		assertEquals(6, TreeUtils.getItemCount(control));

		// ...to check that it collapses to its default state when a new model
		// is updated...
		getRidget().bindToModel(initializeTreeModel(), ITreeNode.class, ITreeNode.PROPERTY_CHILDREN,
				ITreeNode.PROPERTY_PARENT, ITreeNode.PROPERTY_VALUE);

		assertEquals(3, TreeUtils.getItemCount(control));

		ridget.expandTree();

		assertEquals(6, TreeUtils.getItemCount(control));

		// ...update from model should preserver expansion state...
		ridget.updateFromModel();

		assertEquals(6, TreeUtils.getItemCount(control));
	}

	/**
	 * The expandTree and collapseTree methods do not affect the control when
	 * unbound, but are applied to the control after rebinding.
	 */
	public void testExpandAndCollapseTreeWhenUnbound() {
		ITreeRidget ridget = getRidget();
		Tree control = getUIControl();

		assertEquals(3, TreeUtils.getItemCount(control));

		ridget.setUIControl(null);
		ridget.expandTree();
		ridget.setUIControl(control);

		assertEquals(6, TreeUtils.getItemCount(control));

		ridget.setUIControl(null);
		ridget.collapseTree();
		ridget.setUIControl(control);

		assertEquals(1, TreeUtils.getItemCount(control));
	}

	public void testExpandAndCollapseSingleNodes() {
		ITreeRidget ridget = getRidget();
		Tree control = getUIControl();

		ridget.expandTree();

		assertEquals(6, TreeUtils.getItemCount(control));

		ridget.collapse(rootChild1Child2Node);

		assertEquals(5, TreeUtils.getItemCount(control));

		ridget.collapse(rootChild1Node);

		assertEquals(3, TreeUtils.getItemCount(control));

		ridget.expand(rootChild1Node);

		assertEquals(5, TreeUtils.getItemCount(control));

		ridget.expand(rootChild1Child2Node);

		assertEquals(6, TreeUtils.getItemCount(control));
	}

	/**
	 * The expand and collapse methods do not affect the control when unbound,
	 * but are applied to the control after rebinding.
	 */
	public void testExpandAndCollapseSingleNodesWhenUnbound() {
		ITreeRidget ridget = getRidget();
		Tree control = getUIControl();

		assertEquals(3, TreeUtils.getItemCount(control));

		ridget.setUIControl(null);
		ridget.expand(rootChild1Node);
		ridget.setUIControl(control);

		assertEquals(5, TreeUtils.getItemCount(control));

		ridget.setUIControl(null);
		ridget.collapse(rootNode);
		ridget.setUIControl(control);

		assertEquals(1, TreeUtils.getItemCount(control));
	}

	public void testFullExpansionStatusIsPreserved() {
		ITreeRidget ridget = getRidget();
		Tree control = getUIControl();

		ridget.expandTree();

		assertEquals(6, TreeUtils.getItemCount(control));

		ridget.setUIControl(null);
		ridget.setUIControl(control);

		assertEquals(6, TreeUtils.getItemCount(control));

		ridget.collapseTree();

		assertEquals(1, TreeUtils.getItemCount(control));

		ridget.setUIControl(null);
		ridget.setUIControl(control);

		assertEquals(1, TreeUtils.getItemCount(control));
	}

	public void testPartialExpansionStatusIsPreserved() {
		ITreeRidget ridget = getRidget();
		Tree control = getUIControl();

		assertEquals(3, TreeUtils.getItemCount(control));

		ridget.expand(rootChild1Node);

		assertEquals(5, TreeUtils.getItemCount(control));

		ridget.setUIControl(null);
		ridget.setUIControl(control);

		assertEquals(5, TreeUtils.getItemCount(control));

		ridget.collapse(rootNode);

		assertEquals(1, TreeUtils.getItemCount(control));

		ridget.setUIControl(null);
		ridget.setUIControl(control);

		assertEquals(1, TreeUtils.getItemCount(control));
	}

	public void testAddDoubleClickListener() {
		ITreeRidget ridget = getRidget();
		Tree control = getUIControl();

		try {
			ridget.addDoubleClickListener(null);
			fail();
		} catch (RuntimeException npe) {
			// expected
		}

		FTActionListener listener1 = new FTActionListener();
		ridget.addDoubleClickListener(listener1);

		FTActionListener listener2 = new FTActionListener();
		ridget.addDoubleClickListener(listener2);
		ridget.addDoubleClickListener(listener2);

		Event doubleClick = new Event();
		doubleClick.widget = control;
		doubleClick.type = SWT.MouseDoubleClick;
		control.notifyListeners(SWT.MouseDoubleClick, doubleClick);

		assertEquals(1, listener1.getCount());
		assertEquals(2, listener2.getCount());

		ridget.removeDoubleClickListener(listener1);

		control.notifyListeners(SWT.MouseDoubleClick, doubleClick);

		assertEquals(1, listener1.getCount());
	}

	// helping methods
	// ////////////////

	private ITreeNode[] initializeTreeModel() {
		rootNode = new TreeNode(ROOT_NODE_USER_OBJECT);
		rootChild1Node = new TreeNode(rootNode, ROOT_CHILD1_NODE_USER_OBJECT);
		rootChild2Node = new TreeNode(rootNode, ROOT_CHILD2_NODE_USER_OBJECT);
		new TreeNode(rootChild1Node, ROOT_CHILD1_CHILD1_NODE_USER_OBJECT);
		rootChild1Child2Node = new TreeNode(rootChild1Node, ROOT_CHILD1_CHILD2_NODE_USER_OBJECT);
		new TreeNode(rootChild1Child2Node, ROOT_CHILD1_CHILD2_CHILD_NODE_USER_OBJECT);
		return new ITreeNode[] { rootNode };
	}

	/** Removes the give node from its parent (if it has one). */
	private void removeFromParent(ITreeNode node) {
		ITreeNode parent = node.getParent();
		if (parent != null) {
			List<ITreeNode> children = parent.getChildren();
			children.remove(node);
			parent.setChildren(children);
		}
	}

	/**
	 * Return the TreeItem corresponding to the following mock "index" scheme:
	 * 0: item for node1, 1: item for node2, 2: item for node3, 3: item for
	 * node4.
	 * <p>
	 * This method will fully expand the tree to ensure all tree items are
	 * created.
	 */
	private final TreeItem getUIControlItem(int index) {
		getRidget().expandTree();
		Tree control = getUIControl();
		switch (index) {
		case 0:
			return control.getItem(0);
		case 1:
			return control.getItem(0).getItem(0);
		case 2:
			return control.getItem(0).getItem(0).getItem(0);
		case 3:
			return control.getItem(0).getItem(1);
		}
		throw new IndexOutOfBoundsException("index= " + index);
	}

}
