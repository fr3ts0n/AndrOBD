/*
 * (C) Copyright 2015 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 */

package com.fr3ts0n.pvs.gui;

import com.fr3ts0n.pvs.ProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;

import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * @author se82wi
 *         Tree node from a process variable
 */
public class PvTreeNode extends DefaultMutableTreeNode
	implements PvChangeListener
{
	/**
	 * ProcessVar tree node Renderer
	 */
	private static final long serialVersionUID = 8478304706986978587L;

	public PvTreeNode()
	{
		super();
	}

	public PvTreeNode(Object userObject)
	{
		super(userObject);
		setUserObject(userObject);
	}

	/*
	   * (non-Javadoc)
	   *
	   * @see
	   * javax.swing.tree.DefaultMutableTreeNode#setUserObject(java.lang.Object)
	   */
	@SuppressWarnings("unchecked")
	@Override
	public void setUserObject(Object userObject)
	{
		super.setUserObject(userObject);
		if (userObject instanceof ProcessVar)
		{
			Object currVal;
			ProcessVar pv = (ProcessVar) userObject;
			// loop through attributes
			Iterator<Object> it = pv.values().iterator();
			while (it.hasNext())
			{
				currVal = it.next();
				// if there is a child PV, then add it as a tree node
				if (currVal instanceof ProcessVar)
					add(new PvTreeNode(currVal));
			}
			pv.addPvChangeListener(this, PvChangeEvent.PV_ADDED
				| PvChangeEvent.PV_DELETED
				| PvChangeEvent.PV_CLEARED
				| PvChangeEvent.PV_ELIMINATED);
		}
	}

	/**
	 * get process variable from tree node
	 *
	 * @param path tree path to find object for
	 * @return process variable, or null if no process variable found
	 */
	public static ProcessVar getPvFromTreePath(TreePath path)
	{
		PvTreeNode selNode = (PvTreeNode) path.getLastPathComponent();
		if (selNode == null)
			return null;

		Object pvObj = selNode.getUserObject();
		if (!(pvObj instanceof ProcessVar))
			return null;

		return (ProcessVar) pvObj;
	}

	/**
	 * Find child node containing matching user Object
	 *
	 * @param userObject user object to match
	 * @return matching child node, or
	 * <pre>null</pre> if nothing has been found
	 */
	private PvTreeNode findChild(Object userObject)
	{
		PvTreeNode currNode;
		// search through children for matching user object
		@SuppressWarnings("unchecked")
		Iterator<PvTreeNode> it = children.iterator();
		while (it.hasNext())
		{
			currNode = it.next();
			if (userObject == currNode.userObject)
				return currNode;
		}
		// haven't found it -> return NULL
		return null;
	}

	/**
	 * Handler for PvChangeEvents
	 */
	@Override
	public synchronized void pvChanged(PvChangeEvent pvchangeevent)
	{
		Object currVal = pvchangeevent.getValue();
		if (!(currVal instanceof ProcessVar))
			return;

		switch (pvchangeevent.getType())
		{
			case PvChangeEvent.PV_ADDED:
				add(new PvTreeNode(currVal));
				break;

			case PvChangeEvent.PV_CLEARED:
				removeAllChildren();
				break;

			case PvChangeEvent.PV_DELETED:
				PvTreeNode pvNode = findChild(currVal);
				if (pvNode != null)
					pvNode.removeFromParent();
		}
	}
}
