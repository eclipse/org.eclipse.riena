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
package org.eclipse.riena.ui.ridgets.tree;

import java.io.Serializable;

import org.eclipse.core.runtime.Assert;
import org.eclipse.riena.ui.ridgets.UIBindingFailure;

/**
 * Class <code>ChangeVisibilityTreeModel</code> provides an interface to change
 * the visibility of its nodes.
 */
public class ChangeVisibilityTreeModel extends DefaultTreeModel implements IVisibleTreeModel {

	/**
	 * Creates a tree in which any node can have children.
	 * 
	 * @param root
	 *            - the root of the tree.
	 */
	public ChangeVisibilityTreeModel(ITreeNode root) {

		super(root);
	}

	/**
	 * @see de.compeople.spirit.core.client.uibinding.adapter.tree.IVisibleTreeModel#getVisibleChild(java.lang.Object,
	 *      int)
	 */
	public Object getVisibleChild(Object parent, int index) {

		Assert.isNotNull(parent, "parent is null"); //$NON-NLS-1$
		Assert.isTrue(parent instanceof IVisibleTreeNode, "parent is not an instance of IVisibleTreeNode"); //$NON-NLS-1$

		return ((IVisibleTreeNode) parent).getVisibleChildAt(index);
	}

	/**
	 * @see de.compeople.spirit.core.client.uibinding.adapter.tree.IVisibleTreeModel#getVisibleChildCount(java.lang.Object)
	 */
	public int getVisibleChildCount(Object parent) {

		Assert.isNotNull(parent, "parent is null"); //$NON-NLS-1$
		Assert.isTrue(parent instanceof IVisibleTreeNode, "parent is not an instance of IVisibleTreeNode"); //$NON-NLS-1$

		return ((IVisibleTreeNode) parent).getVisibleChildCount();
	}

	/**
	 * @see de.compeople.spirit.core.client.uibinding.adapter.tree.IVisibleTreeModel#getIndexOfVisibleChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	public int getIndexOfVisibleChild(Object parent, Object child) {

		Assert.isNotNull(parent, "parent is null"); //$NON-NLS-1$
		Assert.isTrue(parent instanceof IVisibleTreeNode, "parent is not an instance of IVisibleTreeNode"); //$NON-NLS-1$
		Assert.isNotNull(child, "child is null"); //$NON-NLS-1$
		Assert.isTrue(child instanceof IVisibleTreeNode, "child is not an instance of IVisibleTreeNode"); //$NON-NLS-1$

		return ((IVisibleTreeNode) parent).getVisibleIndex((IVisibleTreeNode) child);
	}

	/**
	 * @see de.compeople.spirit.core.client.uibinding.adapter.tree.IVisibleTreeModel#isVisibleLeaf(java.lang.Object)
	 */
	public boolean isVisibleLeaf(Object node) {

		Assert.isNotNull(node, "node is null"); //$NON-NLS-1$
		Assert.isTrue(node instanceof IVisibleTreeNode, "node is not an instance of IVisibleTreeNode"); //$NON-NLS-1$

		return ((IVisibleTreeNode) node).isVisibleLeaf();
	}

	/**
	 * Sets the visibility of the node.
	 * 
	 * @param node
	 * @param visible
	 */
	public void setTreeNodeVisibility(ChangeVisibilityTreeNode node, boolean visible) {

		int[] nodeIndexes = new int[1];
		Serializable[] nodes = new Serializable[1];
		nodes[0] = node;

		if (visible) {
			node.setVisible(visible);
			nodeIndexes[0] = getIndexOfVisibleChild(node.getParent(), node);
			fireTreeNodesInserted(this, node.getParent(), nodeIndexes, nodes);
		} else {
			nodeIndexes[0] = getIndexOfVisibleChild(node.getParent(), node);
			fireTreeNodesRemoved(this, node.getParent(), nodeIndexes, nodes);
			node.setVisible(visible);
		}
	}

	@Override
	protected int getIndex(DefaultTreeNode parent, DefaultTreeNode node) {

		return getIndexOfVisibleChild(parent, node);
	}

	@Override
	protected int getChildCount(DefaultTreeNode newChild, DefaultTreeNode parent) {

		return getVisibleChildCount(parent);
	}

	/**
	 * @see de.compeople.spirit.core.client.uibinding.adapter.tree.DefaultTreeModel#removeNodeFromParent(de.compeople.spirit.core.client.uibinding.adapter.tree.DefaultTreeNode)
	 */
	@Override
	public void removeNodeFromParent(DefaultTreeNode node) {

		DefaultTreeNode parent = (DefaultTreeNode) node.getParent();

		if (parent == null) {
			throw new UIBindingFailure("node does not have a parent."); //$NON-NLS-1$
		} // end if

		int removedNodeVisibleIndex = getIndex(parent, node);
		parent.remove(super.getIndex(parent, node));

		if (!(node instanceof ChangeVisibilityTreeNode) || ((ChangeVisibilityTreeNode) node).isVisible()) {

			int[] childIndex = new int[1];
			Serializable[] removedArray = new Serializable[1];

			childIndex[0] = removedNodeVisibleIndex;
			removedArray[0] = node;

			fireTreeNodesRemoved(this, parent, childIndex, removedArray);
		}
	}
}
