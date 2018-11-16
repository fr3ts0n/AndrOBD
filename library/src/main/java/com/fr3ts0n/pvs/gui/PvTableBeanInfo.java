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

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
 * @author erwin
 */
class PvTableBeanInfo extends SimpleBeanInfo
{

	// Bean descriptor//GEN-FIRST:BeanDescriptor
	/*lazy BeanDescriptor*/
	private static BeanDescriptor getBdescriptor()
	{
		
		// Here you can add code for customizing the BeanDescriptor.

		return new BeanDescriptor(PvTable.class, null);
	}//GEN-LAST:BeanDescriptor

	// Property identifiers//GEN-FIRST:Properties
	private static final int PROPERTY_accessibleContext = 0;
	private static final int PROPERTY_actionMap = 1;
	private static final int PROPERTY_alignmentX = 2;
	private static final int PROPERTY_alignmentY = 3;
	private static final int PROPERTY_ancestorListeners = 4;
	private static final int PROPERTY_autoCreateColumnsFromModel = 5;
	private static final int PROPERTY_autoResizeMode = 6;
	private static final int PROPERTY_autoscrolls = 7;
	private static final int PROPERTY_background = 8;
	private static final int PROPERTY_backgroundSet = 9;
	private static final int PROPERTY_border = 10;
	private static final int PROPERTY_bounds = 11;
	private static final int PROPERTY_cellEditor = 12;
	private static final int PROPERTY_cellSelectionEnabled = 13;
	private static final int PROPERTY_colorModel = 14;
	private static final int PROPERTY_columnClass = 15;
	private static final int PROPERTY_columnCount = 16;
	private static final int PROPERTY_columnModel = 17;
	private static final int PROPERTY_columnName = 18;
	private static final int PROPERTY_columnSelectionAllowed = 19;
	private static final int PROPERTY_columnSelectionInterval = 20;
	private static final int PROPERTY_component = 21;
	private static final int PROPERTY_componentCount = 22;
	private static final int PROPERTY_componentListeners = 23;
	private static final int PROPERTY_componentOrientation = 24;
	private static final int PROPERTY_components = 25;
	private static final int PROPERTY_containerListeners = 26;
	private static final int PROPERTY_cursor = 27;
	private static final int PROPERTY_cursorSet = 28;
	private static final int PROPERTY_debugGraphicsOptions = 29;
	private static final int PROPERTY_displayable = 30;
	private static final int PROPERTY_doubleBuffered = 31;
	private static final int PROPERTY_dragEnabled = 32;
	private static final int PROPERTY_dropTarget = 33;
	private static final int PROPERTY_editing = 34;
	private static final int PROPERTY_editingColumn = 35;
	private static final int PROPERTY_editingRow = 36;
	private static final int PROPERTY_editorComponent = 37;
	private static final int PROPERTY_enabled = 38;
	private static final int PROPERTY_focusable = 39;
	private static final int PROPERTY_focusCycleRoot = 40;
	private static final int PROPERTY_focusCycleRootAncestor = 41;
	private static final int PROPERTY_focusListeners = 42;
	private static final int PROPERTY_focusOwner = 43;
	private static final int PROPERTY_focusTraversable = 44;
	private static final int PROPERTY_focusTraversalKeys = 45;
	private static final int PROPERTY_focusTraversalKeysEnabled = 46;
	private static final int PROPERTY_focusTraversalPolicy = 47;
	private static final int PROPERTY_focusTraversalPolicySet = 48;
	private static final int PROPERTY_font = 49;
	private static final int PROPERTY_fontSet = 50;
	private static final int PROPERTY_foreground = 51;
	private static final int PROPERTY_foregroundSet = 52;
	private static final int PROPERTY_graphics = 53;
	private static final int PROPERTY_graphicsConfiguration = 54;
	private static final int PROPERTY_gridColor = 55;
	private static final int PROPERTY_height = 56;
	private static final int PROPERTY_hierarchyBoundsListeners = 57;
	private static final int PROPERTY_hierarchyListeners = 58;
	private static final int PROPERTY_ignoreRepaint = 59;
	private static final int PROPERTY_inputContext = 60;
	private static final int PROPERTY_inputMethodListeners = 61;
	private static final int PROPERTY_inputMethodRequests = 62;
	private static final int PROPERTY_inputVerifier = 63;
	private static final int PROPERTY_insets = 64;
	private static final int PROPERTY_intercellSpacing = 65;
	private static final int PROPERTY_keyListeners = 66;
	private static final int PROPERTY_layout = 67;
	private static final int PROPERTY_lightweight = 68;
	private static final int PROPERTY_locale = 69;
	private static final int PROPERTY_locationOnScreen = 70;
	private static final int PROPERTY_managingFocus = 71;
	private static final int PROPERTY_maximumSize = 72;
	private static final int PROPERTY_maximumSizeSet = 73;
	private static final int PROPERTY_minimumSize = 74;
	private static final int PROPERTY_minimumSizeSet = 75;
	private static final int PROPERTY_model = 76;
	private static final int PROPERTY_mouseListeners = 77;
	private static final int PROPERTY_mouseMotionListeners = 78;
	private static final int PROPERTY_mouseWheelListeners = 79;
	private static final int PROPERTY_name = 80;
	private static final int PROPERTY_nextFocusableComponent = 81;
	private static final int PROPERTY_opaque = 82;
	private static final int PROPERTY_optimizedDrawingEnabled = 83;
	private static final int PROPERTY_paintingTile = 84;
	private static final int PROPERTY_parent = 85;
	private static final int PROPERTY_peer = 86;
	private static final int PROPERTY_preferredScrollableViewportSize = 87;
	private static final int PROPERTY_preferredSize = 88;
	private static final int PROPERTY_preferredSizeSet = 89;
	private static final int PROPERTY_processVar = 90;
	private static final int PROPERTY_propertyChangeListeners = 91;
	private static final int PROPERTY_registeredKeyStrokes = 92;
	private static final int PROPERTY_requestFocusEnabled = 93;
	private static final int PROPERTY_rootPane = 94;
	private static final int PROPERTY_rowCount = 95;
	private static final int PROPERTY_rowMargin = 96;
	private static final int PROPERTY_rowSelectionAllowed = 97;
	private static final int PROPERTY_rowSelectionInterval = 98;
	private static final int PROPERTY_scrollableTracksViewportHeight = 99;
	private static final int PROPERTY_scrollableTracksViewportWidth = 100;
	private static final int PROPERTY_selectedColumn = 101;
	private static final int PROPERTY_selectedColumnCount = 102;
	private static final int PROPERTY_selectedColumns = 103;
	private static final int PROPERTY_selectedRow = 104;
	private static final int PROPERTY_selectedRowCount = 105;
	private static final int PROPERTY_selectedRows = 106;
	private static final int PROPERTY_selectionBackground = 107;
	private static final int PROPERTY_selectionForeground = 108;
	private static final int PROPERTY_selectionMode = 109;
	private static final int PROPERTY_selectionModel = 110;
	private static final int PROPERTY_showGrid = 111;
	private static final int PROPERTY_showHorizontalLines = 112;
	private static final int PROPERTY_showing = 113;
	private static final int PROPERTY_showVerticalLines = 114;
	private static final int PROPERTY_surrendersFocusOnKeystroke = 115;
	private static final int PROPERTY_tableHeader = 116;
	private static final int PROPERTY_toolkit = 117;
	private static final int PROPERTY_toolTipText = 118;
	private static final int PROPERTY_topLevelAncestor = 119;
	private static final int PROPERTY_transferHandler = 120;
	private static final int PROPERTY_treeLock = 121;
	private static final int PROPERTY_UI = 122;
	private static final int PROPERTY_UIClassID = 123;
	private static final int PROPERTY_valid = 124;
	private static final int PROPERTY_validateRoot = 125;
	private static final int PROPERTY_verifyInputWhenFocusTarget = 126;
	private static final int PROPERTY_vetoableChangeListeners = 127;
	private static final int PROPERTY_visible = 128;
	private static final int PROPERTY_visibleRect = 129;
	private static final int PROPERTY_width = 130;
	private static final int PROPERTY_x = 131;
	private static final int PROPERTY_y = 132;

	// Property array
    /*lazy PropertyDescriptor*/
	private static PropertyDescriptor[] getPdescriptor()
	{
		PropertyDescriptor[] properties = new PropertyDescriptor[133];

		try
		{
			properties[PROPERTY_accessibleContext] = new PropertyDescriptor("accessibleContext", PvTable.class, "getAccessibleContext", null);
			properties[PROPERTY_actionMap] = new PropertyDescriptor("actionMap", PvTable.class, "getActionMap", "setActionMap");
			properties[PROPERTY_alignmentX] = new PropertyDescriptor("alignmentX", PvTable.class, "getAlignmentX", "setAlignmentX");
			properties[PROPERTY_alignmentY] = new PropertyDescriptor("alignmentY", PvTable.class, "getAlignmentY", "setAlignmentY");
			properties[PROPERTY_ancestorListeners] = new PropertyDescriptor("ancestorListeners", PvTable.class, "getAncestorListeners", null);
			properties[PROPERTY_autoCreateColumnsFromModel] = new PropertyDescriptor("autoCreateColumnsFromModel", PvTable.class, "getAutoCreateColumnsFromModel", "setAutoCreateColumnsFromModel");
			properties[PROPERTY_autoResizeMode] = new PropertyDescriptor("autoResizeMode", PvTable.class, "getAutoResizeMode", "setAutoResizeMode");
			properties[PROPERTY_autoscrolls] = new PropertyDescriptor("autoscrolls", PvTable.class, "getAutoscrolls", "setAutoscrolls");
			properties[PROPERTY_background] = new PropertyDescriptor("background", PvTable.class, "getBackground", "setBackground");
			properties[PROPERTY_backgroundSet] = new PropertyDescriptor("backgroundSet", PvTable.class, "isBackgroundSet", null);
			properties[PROPERTY_border] = new PropertyDescriptor("border", PvTable.class, "getBorder", "setBorder");
			properties[PROPERTY_bounds] = new PropertyDescriptor("bounds", PvTable.class, "getBounds", "setBounds");
			properties[PROPERTY_cellEditor] = new PropertyDescriptor("cellEditor", PvTable.class, "getCellEditor", "setCellEditor");
			properties[PROPERTY_cellSelectionEnabled] = new PropertyDescriptor("cellSelectionEnabled", PvTable.class, "getCellSelectionEnabled", "setCellSelectionEnabled");
			properties[PROPERTY_colorModel] = new PropertyDescriptor("colorModel", PvTable.class, "getColorModel", null);
			properties[PROPERTY_columnClass] = new IndexedPropertyDescriptor("columnClass", PvTable.class, null, null, "getColumnClass", null);
			properties[PROPERTY_columnCount] = new PropertyDescriptor("columnCount", PvTable.class, "getColumnCount", null);
			properties[PROPERTY_columnModel] = new PropertyDescriptor("columnModel", PvTable.class, "getColumnModel", "setColumnModel");
			properties[PROPERTY_columnName] = new IndexedPropertyDescriptor("columnName", PvTable.class, null, null, "getColumnName", null);
			properties[PROPERTY_columnSelectionAllowed] = new PropertyDescriptor("columnSelectionAllowed", PvTable.class, "getColumnSelectionAllowed", "setColumnSelectionAllowed");
			properties[PROPERTY_columnSelectionInterval] = new IndexedPropertyDescriptor("columnSelectionInterval", PvTable.class, null, null, null, "setColumnSelectionInterval");
			properties[PROPERTY_component] = new IndexedPropertyDescriptor("component", PvTable.class, null, null, "getComponent", null);
			properties[PROPERTY_componentCount] = new PropertyDescriptor("componentCount", PvTable.class, "getComponentCount", null);
			properties[PROPERTY_componentListeners] = new PropertyDescriptor("componentListeners", PvTable.class, "getComponentListeners", null);
			properties[PROPERTY_componentOrientation] = new PropertyDescriptor("componentOrientation", PvTable.class, "getComponentOrientation", "setComponentOrientation");
			properties[PROPERTY_components] = new PropertyDescriptor("components", PvTable.class, "getComponents", null);
			properties[PROPERTY_containerListeners] = new PropertyDescriptor("containerListeners", PvTable.class, "getContainerListeners", null);
			properties[PROPERTY_cursor] = new PropertyDescriptor("cursor", PvTable.class, "getCursor", "setCursor");
			properties[PROPERTY_cursorSet] = new PropertyDescriptor("cursorSet", PvTable.class, "isCursorSet", null);
			properties[PROPERTY_debugGraphicsOptions] = new PropertyDescriptor("debugGraphicsOptions", PvTable.class, "getDebugGraphicsOptions", "setDebugGraphicsOptions");
			properties[PROPERTY_displayable] = new PropertyDescriptor("displayable", PvTable.class, "isDisplayable", null);
			properties[PROPERTY_doubleBuffered] = new PropertyDescriptor("doubleBuffered", PvTable.class, "isDoubleBuffered", "setDoubleBuffered");
			properties[PROPERTY_dragEnabled] = new PropertyDescriptor("dragEnabled", PvTable.class, "getDragEnabled", "setDragEnabled");
			properties[PROPERTY_dropTarget] = new PropertyDescriptor("dropTarget", PvTable.class, "getDropTarget", "setDropTarget");
			properties[PROPERTY_editing] = new PropertyDescriptor("editing", PvTable.class, "isEditing", null);
			properties[PROPERTY_editingColumn] = new PropertyDescriptor("editingColumn", PvTable.class, "getEditingColumn", "setEditingColumn");
			properties[PROPERTY_editingRow] = new PropertyDescriptor("editingRow", PvTable.class, "getEditingRow", "setEditingRow");
			properties[PROPERTY_editorComponent] = new PropertyDescriptor("editorComponent", PvTable.class, "getEditorComponent", null);
			properties[PROPERTY_enabled] = new PropertyDescriptor("enabled", PvTable.class, "isEnabled", "setEnabled");
			properties[PROPERTY_focusable] = new PropertyDescriptor("focusable", PvTable.class, "isFocusable", "setFocusable");
			properties[PROPERTY_focusCycleRoot] = new PropertyDescriptor("focusCycleRoot", PvTable.class, "isFocusCycleRoot", "setFocusCycleRoot");
			properties[PROPERTY_focusCycleRootAncestor] = new PropertyDescriptor("focusCycleRootAncestor", PvTable.class, "getFocusCycleRootAncestor", null);
			properties[PROPERTY_focusListeners] = new PropertyDescriptor("focusListeners", PvTable.class, "getFocusListeners", null);
			properties[PROPERTY_focusOwner] = new PropertyDescriptor("focusOwner", PvTable.class, "isFocusOwner", null);
			properties[PROPERTY_focusTraversable] = new PropertyDescriptor("focusTraversable", PvTable.class, "isFocusTraversable", null);
			properties[PROPERTY_focusTraversalKeys] = new IndexedPropertyDescriptor("focusTraversalKeys", PvTable.class, null, null, "getFocusTraversalKeys", "setFocusTraversalKeys");
			properties[PROPERTY_focusTraversalKeysEnabled] = new PropertyDescriptor("focusTraversalKeysEnabled", PvTable.class, "getFocusTraversalKeysEnabled", "setFocusTraversalKeysEnabled");
			properties[PROPERTY_focusTraversalPolicy] = new PropertyDescriptor("focusTraversalPolicy", PvTable.class, "getFocusTraversalPolicy", "setFocusTraversalPolicy");
			properties[PROPERTY_focusTraversalPolicySet] = new PropertyDescriptor("focusTraversalPolicySet", PvTable.class, "isFocusTraversalPolicySet", null);
			properties[PROPERTY_font] = new PropertyDescriptor("font", PvTable.class, "getFont", "setFont");
			properties[PROPERTY_fontSet] = new PropertyDescriptor("fontSet", PvTable.class, "isFontSet", null);
			properties[PROPERTY_foreground] = new PropertyDescriptor("foreground", PvTable.class, "getForeground", "setForeground");
			properties[PROPERTY_foregroundSet] = new PropertyDescriptor("foregroundSet", PvTable.class, "isForegroundSet", null);
			properties[PROPERTY_graphics] = new PropertyDescriptor("graphics", PvTable.class, "getGraphics", null);
			properties[PROPERTY_graphicsConfiguration] = new PropertyDescriptor("graphicsConfiguration", PvTable.class, "getGraphicsConfiguration", null);
			properties[PROPERTY_gridColor] = new PropertyDescriptor("gridColor", PvTable.class, "getGridColor", "setGridColor");
			properties[PROPERTY_height] = new PropertyDescriptor("height", PvTable.class, "getHeight", null);
			properties[PROPERTY_hierarchyBoundsListeners] = new PropertyDescriptor("hierarchyBoundsListeners", PvTable.class, "getHierarchyBoundsListeners", null);
			properties[PROPERTY_hierarchyListeners] = new PropertyDescriptor("hierarchyListeners", PvTable.class, "getHierarchyListeners", null);
			properties[PROPERTY_ignoreRepaint] = new PropertyDescriptor("ignoreRepaint", PvTable.class, "getIgnoreRepaint", "setIgnoreRepaint");
			properties[PROPERTY_inputContext] = new PropertyDescriptor("inputContext", PvTable.class, "getInputContext", null);
			properties[PROPERTY_inputMethodListeners] = new PropertyDescriptor("inputMethodListeners", PvTable.class, "getInputMethodListeners", null);
			properties[PROPERTY_inputMethodRequests] = new PropertyDescriptor("inputMethodRequests", PvTable.class, "getInputMethodRequests", null);
			properties[PROPERTY_inputVerifier] = new PropertyDescriptor("inputVerifier", PvTable.class, "getInputVerifier", "setInputVerifier");
			properties[PROPERTY_insets] = new PropertyDescriptor("insets", PvTable.class, "getInsets", null);
			properties[PROPERTY_intercellSpacing] = new PropertyDescriptor("intercellSpacing", PvTable.class, "getIntercellSpacing", "setIntercellSpacing");
			properties[PROPERTY_keyListeners] = new PropertyDescriptor("keyListeners", PvTable.class, "getKeyListeners", null);
			properties[PROPERTY_layout] = new PropertyDescriptor("layout", PvTable.class, "getLayout", "setLayout");
			properties[PROPERTY_lightweight] = new PropertyDescriptor("lightweight", PvTable.class, "isLightweight", null);
			properties[PROPERTY_locale] = new PropertyDescriptor("locale", PvTable.class, "getLocale", "setLocale");
			properties[PROPERTY_locationOnScreen] = new PropertyDescriptor("locationOnScreen", PvTable.class, "getLocationOnScreen", null);
			properties[PROPERTY_managingFocus] = new PropertyDescriptor("managingFocus", PvTable.class, "isManagingFocus", null);
			properties[PROPERTY_maximumSize] = new PropertyDescriptor("maximumSize", PvTable.class, "getMaximumSize", "setMaximumSize");
			properties[PROPERTY_maximumSizeSet] = new PropertyDescriptor("maximumSizeSet", PvTable.class, "isMaximumSizeSet", null);
			properties[PROPERTY_minimumSize] = new PropertyDescriptor("minimumSize", PvTable.class, "getMinimumSize", "setMinimumSize");
			properties[PROPERTY_minimumSizeSet] = new PropertyDescriptor("minimumSizeSet", PvTable.class, "isMinimumSizeSet", null);
			properties[PROPERTY_model] = new PropertyDescriptor("model", PvTable.class, "getModel", "setModel");
			properties[PROPERTY_mouseListeners] = new PropertyDescriptor("mouseListeners", PvTable.class, "getMouseListeners", null);
			properties[PROPERTY_mouseMotionListeners] = new PropertyDescriptor("mouseMotionListeners", PvTable.class, "getMouseMotionListeners", null);
			properties[PROPERTY_mouseWheelListeners] = new PropertyDescriptor("mouseWheelListeners", PvTable.class, "getMouseWheelListeners", null);
			properties[PROPERTY_name] = new PropertyDescriptor("name", PvTable.class, "getName", "setName");
			properties[PROPERTY_nextFocusableComponent] = new PropertyDescriptor("nextFocusableComponent", PvTable.class, "getNextFocusableComponent", "setNextFocusableComponent");
			properties[PROPERTY_opaque] = new PropertyDescriptor("opaque", PvTable.class, "isOpaque", "setOpaque");
			properties[PROPERTY_optimizedDrawingEnabled] = new PropertyDescriptor("optimizedDrawingEnabled", PvTable.class, "isOptimizedDrawingEnabled", null);
			properties[PROPERTY_paintingTile] = new PropertyDescriptor("paintingTile", PvTable.class, "isPaintingTile", null);
			properties[PROPERTY_parent] = new PropertyDescriptor("parent", PvTable.class, "getParent", null);
			properties[PROPERTY_peer] = new PropertyDescriptor("peer", PvTable.class, "getPeer", null);
			properties[PROPERTY_preferredScrollableViewportSize] = new PropertyDescriptor("preferredScrollableViewportSize", PvTable.class, "getPreferredScrollableViewportSize", "setPreferredScrollableViewportSize");
			properties[PROPERTY_preferredSize] = new PropertyDescriptor("preferredSize", PvTable.class, "getPreferredSize", "setPreferredSize");
			properties[PROPERTY_preferredSizeSet] = new PropertyDescriptor("preferredSizeSet", PvTable.class, "isPreferredSizeSet", null);
			properties[PROPERTY_processVar] = new PropertyDescriptor("processVar", PvTable.class, null, "setProcessVar");
			properties[PROPERTY_propertyChangeListeners] = new PropertyDescriptor("propertyChangeListeners", PvTable.class, "getPropertyChangeListeners", null);
			properties[PROPERTY_registeredKeyStrokes] = new PropertyDescriptor("registeredKeyStrokes", PvTable.class, "getRegisteredKeyStrokes", null);
			properties[PROPERTY_requestFocusEnabled] = new PropertyDescriptor("requestFocusEnabled", PvTable.class, "isRequestFocusEnabled", "setRequestFocusEnabled");
			properties[PROPERTY_rootPane] = new PropertyDescriptor("rootPane", PvTable.class, "getRootPane", null);
			properties[PROPERTY_rowCount] = new PropertyDescriptor("rowCount", PvTable.class, "getRowCount", null);
			properties[PROPERTY_rowMargin] = new PropertyDescriptor("rowMargin", PvTable.class, "getRowMargin", "setRowMargin");
			properties[PROPERTY_rowSelectionAllowed] = new PropertyDescriptor("rowSelectionAllowed", PvTable.class, "getRowSelectionAllowed", "setRowSelectionAllowed");
			properties[PROPERTY_rowSelectionInterval] = new IndexedPropertyDescriptor("rowSelectionInterval", PvTable.class, null, null, null, "setRowSelectionInterval");
			properties[PROPERTY_scrollableTracksViewportHeight] = new PropertyDescriptor("scrollableTracksViewportHeight", PvTable.class, "getScrollableTracksViewportHeight", null);
			properties[PROPERTY_scrollableTracksViewportWidth] = new PropertyDescriptor("scrollableTracksViewportWidth", PvTable.class, "getScrollableTracksViewportWidth", null);
			properties[PROPERTY_selectedColumn] = new PropertyDescriptor("selectedColumn", PvTable.class, "getSelectedColumn", null);
			properties[PROPERTY_selectedColumnCount] = new PropertyDescriptor("selectedColumnCount", PvTable.class, "getSelectedColumnCount", null);
			properties[PROPERTY_selectedColumns] = new PropertyDescriptor("selectedColumns", PvTable.class, "getSelectedColumns", null);
			properties[PROPERTY_selectedRow] = new PropertyDescriptor("selectedRow", PvTable.class, "getSelectedRow", null);
			properties[PROPERTY_selectedRowCount] = new PropertyDescriptor("selectedRowCount", PvTable.class, "getSelectedRowCount", null);
			properties[PROPERTY_selectedRows] = new PropertyDescriptor("selectedRows", PvTable.class, "getSelectedRows", null);
			properties[PROPERTY_selectionBackground] = new PropertyDescriptor("selectionBackground", PvTable.class, "getSelectionBackground", "setSelectionBackground");
			properties[PROPERTY_selectionForeground] = new PropertyDescriptor("selectionForeground", PvTable.class, "getSelectionForeground", "setSelectionForeground");
			properties[PROPERTY_selectionMode] = new PropertyDescriptor("selectionMode", PvTable.class, null, "setSelectionMode");
			properties[PROPERTY_selectionModel] = new PropertyDescriptor("selectionModel", PvTable.class, "getSelectionModel", "setSelectionModel");
			properties[PROPERTY_showGrid] = new PropertyDescriptor("showGrid", PvTable.class, null, "setShowGrid");
			properties[PROPERTY_showHorizontalLines] = new PropertyDescriptor("showHorizontalLines", PvTable.class, "getShowHorizontalLines", "setShowHorizontalLines");
			properties[PROPERTY_showing] = new PropertyDescriptor("showing", PvTable.class, "isShowing", null);
			properties[PROPERTY_showVerticalLines] = new PropertyDescriptor("showVerticalLines", PvTable.class, "getShowVerticalLines", "setShowVerticalLines");
			properties[PROPERTY_surrendersFocusOnKeystroke] = new PropertyDescriptor("surrendersFocusOnKeystroke", PvTable.class, "getSurrendersFocusOnKeystroke", "setSurrendersFocusOnKeystroke");
			properties[PROPERTY_tableHeader] = new PropertyDescriptor("tableHeader", PvTable.class, "getTableHeader", "setTableHeader");
			properties[PROPERTY_toolkit] = new PropertyDescriptor("toolkit", PvTable.class, "getToolkit", null);
			properties[PROPERTY_toolTipText] = new PropertyDescriptor("toolTipText", PvTable.class, "getToolTipText", "setToolTipText");
			properties[PROPERTY_topLevelAncestor] = new PropertyDescriptor("topLevelAncestor", PvTable.class, "getTopLevelAncestor", null);
			properties[PROPERTY_transferHandler] = new PropertyDescriptor("transferHandler", PvTable.class, "getTransferHandler", "setTransferHandler");
			properties[PROPERTY_treeLock] = new PropertyDescriptor("treeLock", PvTable.class, "getTreeLock", null);
			properties[PROPERTY_UI] = new PropertyDescriptor("UI", PvTable.class, "getUI", "setUI");
			properties[PROPERTY_UIClassID] = new PropertyDescriptor("UIClassID", PvTable.class, "getUIClassID", null);
			properties[PROPERTY_valid] = new PropertyDescriptor("valid", PvTable.class, "isValid", null);
			properties[PROPERTY_validateRoot] = new PropertyDescriptor("validateRoot", PvTable.class, "isValidateRoot", null);
			properties[PROPERTY_verifyInputWhenFocusTarget] = new PropertyDescriptor("verifyInputWhenFocusTarget", PvTable.class, "getVerifyInputWhenFocusTarget", "setVerifyInputWhenFocusTarget");
			properties[PROPERTY_vetoableChangeListeners] = new PropertyDescriptor("vetoableChangeListeners", PvTable.class, "getVetoableChangeListeners", null);
			properties[PROPERTY_visible] = new PropertyDescriptor("visible", PvTable.class, "isVisible", "setVisible");
			properties[PROPERTY_visibleRect] = new PropertyDescriptor("visibleRect", PvTable.class, "getVisibleRect", null);
			properties[PROPERTY_width] = new PropertyDescriptor("width", PvTable.class, "getWidth", null);
			properties[PROPERTY_x] = new PropertyDescriptor("x", PvTable.class, "getX", null);
			properties[PROPERTY_y] = new PropertyDescriptor("y", PvTable.class, "getY", null);
		} catch (IntrospectionException ignored)
		{
		}//GEN-HEADEREND:Properties

		// Here you can add code for customizing the properties array.

		return properties;
	}//GEN-LAST:Properties

	// EventSet identifiers//GEN-FIRST:Events
	private static final int EVENT_ancestorListener = 0;
	private static final int EVENT_componentListener = 1;
	private static final int EVENT_containerListener = 2;
	private static final int EVENT_focusListener = 3;
	private static final int EVENT_hierarchyBoundsListener = 4;
	private static final int EVENT_hierarchyListener = 5;
	private static final int EVENT_inputMethodListener = 6;
	private static final int EVENT_keyListener = 7;
	private static final int EVENT_mouseListener = 8;
	private static final int EVENT_mouseMotionListener = 9;
	private static final int EVENT_mouseWheelListener = 10;
	private static final int EVENT_propertyChangeListener = 11;
	private static final int EVENT_vetoableChangeListener = 12;

	// EventSet array
    /*lazy EventSetDescriptor*/
	private static EventSetDescriptor[] getEdescriptor()
	{
		EventSetDescriptor[] eventSets = new EventSetDescriptor[13];

		try
		{
			eventSets[EVENT_ancestorListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "ancestorListener", javax.swing.event.AncestorListener.class, new String[]{"ancestorAdded", "ancestorMoved", "ancestorRemoved"}, "addAncestorListener", "removeAncestorListener");
			eventSets[EVENT_componentListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "componentListener", java.awt.event.ComponentListener.class, new String[]{"componentHidden", "componentMoved", "componentResized", "componentShown"}, "addComponentListener", "removeComponentListener");
			eventSets[EVENT_containerListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "containerListener", java.awt.event.ContainerListener.class, new String[]{"componentAdded", "componentRemoved"}, "addContainerListener", "removeContainerListener");
			eventSets[EVENT_focusListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "focusListener", java.awt.event.FocusListener.class, new String[]{"focusGained", "focusLost"}, "addFocusListener", "removeFocusListener");
			eventSets[EVENT_hierarchyBoundsListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "hierarchyBoundsListener", java.awt.event.HierarchyBoundsListener.class, new String[]{"ancestorMoved", "ancestorResized"}, "addHierarchyBoundsListener", "removeHierarchyBoundsListener");
			eventSets[EVENT_hierarchyListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "hierarchyListener", java.awt.event.HierarchyListener.class, new String[]{"hierarchyChanged"}, "addHierarchyListener", "removeHierarchyListener");
			eventSets[EVENT_inputMethodListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "inputMethodListener", java.awt.event.InputMethodListener.class, new String[]{"caretPositionChanged", "inputMethodTextChanged"}, "addInputMethodListener", "removeInputMethodListener");
			eventSets[EVENT_keyListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "keyListener", java.awt.event.KeyListener.class, new String[]{"keyPressed", "keyReleased", "keyTyped"}, "addKeyListener", "removeKeyListener");
			eventSets[EVENT_mouseListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "mouseListener", java.awt.event.MouseListener.class, new String[]{"mouseClicked", "mouseEntered", "mouseExited", "mousePressed", "mouseReleased"}, "addMouseListener", "removeMouseListener");
			eventSets[EVENT_mouseMotionListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "mouseMotionListener", java.awt.event.MouseMotionListener.class, new String[]{"mouseDragged", "mouseMoved"}, "addMouseMotionListener", "removeMouseMotionListener");
			eventSets[EVENT_mouseWheelListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "mouseWheelListener", java.awt.event.MouseWheelListener.class, new String[]{"mouseWheelMoved"}, "addMouseWheelListener", "removeMouseWheelListener");
			eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[]{"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener");
			eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor(com.fr3ts0n.pvs.gui.PvTable.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[]{"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener");
		} catch (IntrospectionException ignored)
		{
		}//GEN-HEADEREND:Events

		// Here you can add code for customizing the event sets array.

		return eventSets;
	}//GEN-LAST:Events

	// Method identifiers//GEN-FIRST:Methods
	private static final int METHOD_action0 = 0;
	private static final int METHOD_add1 = 1;
	private static final int METHOD_addColumn2 = 2;
	private static final int METHOD_addColumnSelectionInterval3 = 3;
	private static final int METHOD_addNotify4 = 4;
	private static final int METHOD_addPropertyChangeListener5 = 5;
	private static final int METHOD_addRowSelectionInterval6 = 6;
	private static final int METHOD_applyComponentOrientation7 = 7;
	private static final int METHOD_areFocusTraversalKeysSet8 = 8;
	private static final int METHOD_bounds9 = 9;
	private static final int METHOD_changeSelection10 = 10;
	private static final int METHOD_checkImage11 = 11;
	private static final int METHOD_clearSelection12 = 12;
	private static final int METHOD_columnAdded13 = 13;
	private static final int METHOD_columnAtPoint14 = 14;
	private static final int METHOD_columnMarginChanged15 = 15;
	private static final int METHOD_columnMoved16 = 16;
	private static final int METHOD_columnRemoved17 = 17;
	private static final int METHOD_columnSelectionChanged18 = 18;
	private static final int METHOD_computeVisibleRect19 = 19;
	private static final int METHOD_contains20 = 20;
	private static final int METHOD_convertColumnIndexToModel21 = 21;
	private static final int METHOD_convertColumnIndexToView22 = 22;
	private static final int METHOD_countComponents23 = 23;
	private static final int METHOD_createDefaultColumnsFromModel24 = 24;
	private static final int METHOD_createImage25 = 25;
	private static final int METHOD_createScrollPaneForTable26 = 26;
	private static final int METHOD_createToolTip27 = 27;
	private static final int METHOD_createVolatileImage28 = 28;
	private static final int METHOD_deliverEvent29 = 29;
	private static final int METHOD_disable30 = 30;
	private static final int METHOD_dispatchEvent31 = 31;
	private static final int METHOD_doLayout32 = 32;
	private static final int METHOD_editCellAt33 = 33;
	private static final int METHOD_editingCanceled34 = 34;
	private static final int METHOD_editingStopped35 = 35;
	private static final int METHOD_enable36 = 36;
	private static final int METHOD_enableInputMethods37 = 37;
	private static final int METHOD_findComponentAt38 = 38;
	private static final int METHOD_firePropertyChange39 = 39;
	private static final int METHOD_getActionForKeyStroke40 = 40;
	private static final int METHOD_getBounds41 = 41;
	private static final int METHOD_getCellEditor42 = 42;
	private static final int METHOD_getCellRect43 = 43;
	private static final int METHOD_getCellRenderer44 = 44;
	private static final int METHOD_getClientProperty45 = 45;
	private static final int METHOD_getColumn46 = 46;
	private static final int METHOD_getComponentAt47 = 47;
	private static final int METHOD_getConditionForKeyStroke48 = 48;
	private static final int METHOD_getDefaultEditor49 = 49;
	private static final int METHOD_getDefaultLocale50 = 50;
	private static final int METHOD_getDefaultRenderer51 = 51;
	private static final int METHOD_getFontMetrics52 = 52;
	private static final int METHOD_getInputMap53 = 53;
	private static final int METHOD_getInsets54 = 54;
	private static final int METHOD_getListeners55 = 55;
	private static final int METHOD_getLocation56 = 56;
	private static final int METHOD_getPropertyChangeListeners57 = 57;
	private static final int METHOD_getRowHeight58 = 58;
	private static final int METHOD_getScrollableBlockIncrement59 = 59;
	private static final int METHOD_getScrollableUnitIncrement60 = 60;
	private static final int METHOD_getSize61 = 61;
	private static final int METHOD_getToolTipLocation62 = 62;
	private static final int METHOD_getToolTipText63 = 63;
	private static final int METHOD_getValueAt64 = 64;
	private static final int METHOD_gotFocus65 = 65;
	private static final int METHOD_grabFocus66 = 66;
	private static final int METHOD_handleEvent67 = 67;
	private static final int METHOD_hasFocus68 = 68;
	private static final int METHOD_hide69 = 69;
	private static final int METHOD_imageUpdate70 = 70;
	private static final int METHOD_insets71 = 71;
	private static final int METHOD_inside72 = 72;
	private static final int METHOD_invalidate73 = 73;
	private static final int METHOD_isAncestorOf74 = 74;
	private static final int METHOD_isCellEditable75 = 75;
	private static final int METHOD_isCellSelected76 = 76;
	private static final int METHOD_isColumnSelected77 = 77;
	private static final int METHOD_isFocusCycleRoot78 = 78;
	private static final int METHOD_isLightweightComponent79 = 79;
	private static final int METHOD_isRowSelected80 = 80;
	private static final int METHOD_keyDown81 = 81;
	private static final int METHOD_keyUp82 = 82;
	private static final int METHOD_layout83 = 83;
	private static final int METHOD_list84 = 84;
	private static final int METHOD_locate85 = 85;
	private static final int METHOD_location86 = 86;
	private static final int METHOD_lostFocus87 = 87;
	private static final int METHOD_minimumSize88 = 88;
	private static final int METHOD_mouseDown89 = 89;
	private static final int METHOD_mouseDrag90 = 90;
	private static final int METHOD_mouseEnter91 = 91;
	private static final int METHOD_mouseExit92 = 92;
	private static final int METHOD_mouseMove93 = 93;
	private static final int METHOD_mouseUp94 = 94;
	private static final int METHOD_move95 = 95;
	private static final int METHOD_moveColumn96 = 96;
	private static final int METHOD_nextFocus97 = 97;
	private static final int METHOD_paint98 = 98;
	private static final int METHOD_paintAll99 = 99;
	private static final int METHOD_paintComponents100 = 100;
	private static final int METHOD_paintImmediately101 = 101;
	private static final int METHOD_postEvent102 = 102;
	private static final int METHOD_preferredSize103 = 103;
	private static final int METHOD_prepareEditor104 = 104;
	private static final int METHOD_prepareImage105 = 105;
	private static final int METHOD_prepareRenderer106 = 106;
	private static final int METHOD_print107 = 107;
	private static final int METHOD_printAll108 = 108;
	private static final int METHOD_printComponents109 = 109;
	private static final int METHOD_putClientProperty110 = 110;
	private static final int METHOD_registerKeyboardAction111 = 111;
	private static final int METHOD_remove112 = 112;
	private static final int METHOD_removeAll113 = 113;
	private static final int METHOD_removeColumn114 = 114;
	private static final int METHOD_removeColumnSelectionInterval115 = 115;
	private static final int METHOD_removeEditor116 = 116;
	private static final int METHOD_removeNotify117 = 117;
	private static final int METHOD_removePropertyChangeListener118 = 118;
	private static final int METHOD_removeRowSelectionInterval119 = 119;
	private static final int METHOD_repaint120 = 120;
	private static final int METHOD_requestDefaultFocus121 = 121;
	private static final int METHOD_requestFocus122 = 122;
	private static final int METHOD_requestFocusInWindow123 = 123;
	private static final int METHOD_resetKeyboardActions124 = 124;
	private static final int METHOD_reshape125 = 125;
	private static final int METHOD_resize126 = 126;
	private static final int METHOD_revalidate127 = 127;
	private static final int METHOD_rowAtPoint128 = 128;
	private static final int METHOD_scrollRectToVisible129 = 129;
	private static final int METHOD_selectAll130 = 130;
	private static final int METHOD_setBounds131 = 131;
	private static final int METHOD_setDefaultEditor132 = 132;
	private static final int METHOD_setDefaultLocale133 = 133;
	private static final int METHOD_setDefaultRenderer134 = 134;
	private static final int METHOD_setInputMap135 = 135;
	private static final int METHOD_setLocation136 = 136;
	private static final int METHOD_setRowHeight137 = 137;
	private static final int METHOD_setSize138 = 138;
	private static final int METHOD_setValueAt139 = 139;
	private static final int METHOD_show140 = 140;
	private static final int METHOD_size141 = 141;
	private static final int METHOD_sizeColumnsToFit142 = 142;
	private static final int METHOD_tableChanged143 = 143;
	private static final int METHOD_toString144 = 144;
	private static final int METHOD_transferFocus145 = 145;
	private static final int METHOD_transferFocusBackward146 = 146;
	private static final int METHOD_transferFocusDownCycle147 = 147;
	private static final int METHOD_transferFocusUpCycle148 = 148;
	private static final int METHOD_unregisterKeyboardAction149 = 149;
	private static final int METHOD_update150 = 150;
	private static final int METHOD_updateUI151 = 151;
	private static final int METHOD_validate152 = 152;
	private static final int METHOD_valueChanged153 = 153;

	// Method array
    /*lazy MethodDescriptor*/
	private static MethodDescriptor[] getMdescriptor()
	{
		MethodDescriptor[] methods = new MethodDescriptor[154];

		try
		{
			methods[METHOD_action0] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("action",
				java.awt.Event.class, Object.class));
			methods[METHOD_action0].setDisplayName("");
			methods[METHOD_add1] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("add",
				java.awt.Component.class));
			methods[METHOD_add1].setDisplayName("");
			methods[METHOD_addColumn2] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("addColumn",
				javax.swing.table.TableColumn.class));
			methods[METHOD_addColumn2].setDisplayName("");
			methods[METHOD_addColumnSelectionInterval3] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("addColumnSelectionInterval",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_addColumnSelectionInterval3].setDisplayName("");
			methods[METHOD_addNotify4] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("addNotify"));
			methods[METHOD_addNotify4].setDisplayName("");
			methods[METHOD_addPropertyChangeListener5] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("addPropertyChangeListener",
				String.class, java.beans.PropertyChangeListener.class));
			methods[METHOD_addPropertyChangeListener5].setDisplayName("");
			methods[METHOD_addRowSelectionInterval6] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("addRowSelectionInterval",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_addRowSelectionInterval6].setDisplayName("");
			methods[METHOD_applyComponentOrientation7] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("applyComponentOrientation",
				java.awt.ComponentOrientation.class));
			methods[METHOD_applyComponentOrientation7].setDisplayName("");
			methods[METHOD_areFocusTraversalKeysSet8] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("areFocusTraversalKeysSet",
				Integer.TYPE));
			methods[METHOD_areFocusTraversalKeysSet8].setDisplayName("");
			methods[METHOD_bounds9] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("bounds"));
			methods[METHOD_bounds9].setDisplayName("");
			methods[METHOD_changeSelection10] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("changeSelection",
				Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE));
			methods[METHOD_changeSelection10].setDisplayName("");
			methods[METHOD_checkImage11] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("checkImage",
				java.awt.Image.class, java.awt.image.ImageObserver.class));
			methods[METHOD_checkImage11].setDisplayName("");
			methods[METHOD_clearSelection12] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("clearSelection"));
			methods[METHOD_clearSelection12].setDisplayName("");
			methods[METHOD_columnAdded13] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("columnAdded",
				javax.swing.event.TableColumnModelEvent.class));
			methods[METHOD_columnAdded13].setDisplayName("");
			methods[METHOD_columnAtPoint14] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("columnAtPoint",
				java.awt.Point.class));
			methods[METHOD_columnAtPoint14].setDisplayName("");
			methods[METHOD_columnMarginChanged15] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("columnMarginChanged",
				javax.swing.event.ChangeEvent.class));
			methods[METHOD_columnMarginChanged15].setDisplayName("");
			methods[METHOD_columnMoved16] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("columnMoved",
				javax.swing.event.TableColumnModelEvent.class));
			methods[METHOD_columnMoved16].setDisplayName("");
			methods[METHOD_columnRemoved17] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("columnRemoved",
				javax.swing.event.TableColumnModelEvent.class));
			methods[METHOD_columnRemoved17].setDisplayName("");
			methods[METHOD_columnSelectionChanged18] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("columnSelectionChanged",
				javax.swing.event.ListSelectionEvent.class));
			methods[METHOD_columnSelectionChanged18].setDisplayName("");
			methods[METHOD_computeVisibleRect19] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("computeVisibleRect",
				java.awt.Rectangle.class));
			methods[METHOD_computeVisibleRect19].setDisplayName("");
			methods[METHOD_contains20] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("contains",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_contains20].setDisplayName("");
			methods[METHOD_convertColumnIndexToModel21] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("convertColumnIndexToModel",
				Integer.TYPE));
			methods[METHOD_convertColumnIndexToModel21].setDisplayName("");
			methods[METHOD_convertColumnIndexToView22] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("convertColumnIndexToView",
				Integer.TYPE));
			methods[METHOD_convertColumnIndexToView22].setDisplayName("");
			methods[METHOD_countComponents23] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("countComponents"));
			methods[METHOD_countComponents23].setDisplayName("");
			methods[METHOD_createDefaultColumnsFromModel24] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("createDefaultColumnsFromModel"));
			methods[METHOD_createDefaultColumnsFromModel24].setDisplayName("");
			methods[METHOD_createImage25] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("createImage",
				java.awt.image.ImageProducer.class));
			methods[METHOD_createImage25].setDisplayName("");
			methods[METHOD_createScrollPaneForTable26] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("createScrollPaneForTable",
				javax.swing.JTable.class));
			methods[METHOD_createScrollPaneForTable26].setDisplayName("");
			methods[METHOD_createToolTip27] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("createToolTip"));
			methods[METHOD_createToolTip27].setDisplayName("");
			methods[METHOD_createVolatileImage28] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("createVolatileImage",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_createVolatileImage28].setDisplayName("");
			methods[METHOD_deliverEvent29] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("deliverEvent",
				java.awt.Event.class));
			methods[METHOD_deliverEvent29].setDisplayName("");
			methods[METHOD_disable30] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("disable"));
			methods[METHOD_disable30].setDisplayName("");
			methods[METHOD_dispatchEvent31] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("dispatchEvent",
				java.awt.AWTEvent.class));
			methods[METHOD_dispatchEvent31].setDisplayName("");
			methods[METHOD_doLayout32] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("doLayout"));
			methods[METHOD_doLayout32].setDisplayName("");
			methods[METHOD_editCellAt33] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("editCellAt",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_editCellAt33].setDisplayName("");
			methods[METHOD_editingCanceled34] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("editingCanceled",
				javax.swing.event.ChangeEvent.class));
			methods[METHOD_editingCanceled34].setDisplayName("");
			methods[METHOD_editingStopped35] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("editingStopped",
				javax.swing.event.ChangeEvent.class));
			methods[METHOD_editingStopped35].setDisplayName("");
			methods[METHOD_enable36] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("enable"));
			methods[METHOD_enable36].setDisplayName("");
			methods[METHOD_enableInputMethods37] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("enableInputMethods",
				Boolean.TYPE));
			methods[METHOD_enableInputMethods37].setDisplayName("");
			methods[METHOD_findComponentAt38] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("findComponentAt",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_findComponentAt38].setDisplayName("");
			methods[METHOD_firePropertyChange39] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("firePropertyChange",
				String.class, Byte.TYPE, Byte.TYPE));
			methods[METHOD_firePropertyChange39].setDisplayName("");
			methods[METHOD_getActionForKeyStroke40] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getActionForKeyStroke",
				javax.swing.KeyStroke.class));
			methods[METHOD_getActionForKeyStroke40].setDisplayName("");
			methods[METHOD_getBounds41] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getBounds",
				java.awt.Rectangle.class));
			methods[METHOD_getBounds41].setDisplayName("");
			methods[METHOD_getCellEditor42] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getCellEditor",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_getCellEditor42].setDisplayName("");
			methods[METHOD_getCellRect43] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getCellRect",
				Integer.TYPE, Integer.TYPE, Boolean.TYPE));
			methods[METHOD_getCellRect43].setDisplayName("");
			methods[METHOD_getCellRenderer44] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getCellRenderer",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_getCellRenderer44].setDisplayName("");
			methods[METHOD_getClientProperty45] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getClientProperty",
				Object.class));
			methods[METHOD_getClientProperty45].setDisplayName("");
			methods[METHOD_getColumn46] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getColumn",
				Object.class));
			methods[METHOD_getColumn46].setDisplayName("");
			methods[METHOD_getComponentAt47] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getComponentAt",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_getComponentAt47].setDisplayName("");
			methods[METHOD_getConditionForKeyStroke48] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getConditionForKeyStroke",
				javax.swing.KeyStroke.class));
			methods[METHOD_getConditionForKeyStroke48].setDisplayName("");
			methods[METHOD_getDefaultEditor49] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getDefaultEditor",
				Class.class));
			methods[METHOD_getDefaultEditor49].setDisplayName("");
			methods[METHOD_getDefaultLocale50] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getDefaultLocale"));
			methods[METHOD_getDefaultLocale50].setDisplayName("");
			methods[METHOD_getDefaultRenderer51] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getDefaultRenderer",
				Class.class));
			methods[METHOD_getDefaultRenderer51].setDisplayName("");
			methods[METHOD_getFontMetrics52] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getFontMetrics",
				java.awt.Font.class));
			methods[METHOD_getFontMetrics52].setDisplayName("");
			methods[METHOD_getInputMap53] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getInputMap",
				Integer.TYPE));
			methods[METHOD_getInputMap53].setDisplayName("");
			methods[METHOD_getInsets54] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getInsets",
				java.awt.Insets.class));
			methods[METHOD_getInsets54].setDisplayName("");
			methods[METHOD_getListeners55] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getListeners",
				Class.class));
			methods[METHOD_getListeners55].setDisplayName("");
			methods[METHOD_getLocation56] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getLocation",
				java.awt.Point.class));
			methods[METHOD_getLocation56].setDisplayName("");
			methods[METHOD_getPropertyChangeListeners57] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getPropertyChangeListeners",
				String.class));
			methods[METHOD_getPropertyChangeListeners57].setDisplayName("");
			methods[METHOD_getRowHeight58] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getRowHeight"));
			methods[METHOD_getRowHeight58].setDisplayName("");
			methods[METHOD_getScrollableBlockIncrement59] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getScrollableBlockIncrement",
				java.awt.Rectangle.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_getScrollableBlockIncrement59].setDisplayName("");
			methods[METHOD_getScrollableUnitIncrement60] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getScrollableUnitIncrement",
				java.awt.Rectangle.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_getScrollableUnitIncrement60].setDisplayName("");
			methods[METHOD_getSize61] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getSize",
				java.awt.Dimension.class));
			methods[METHOD_getSize61].setDisplayName("");
			methods[METHOD_getToolTipLocation62] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getToolTipLocation",
				java.awt.event.MouseEvent.class));
			methods[METHOD_getToolTipLocation62].setDisplayName("");
			methods[METHOD_getToolTipText63] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getToolTipText",
				java.awt.event.MouseEvent.class));
			methods[METHOD_getToolTipText63].setDisplayName("");
			methods[METHOD_getValueAt64] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("getValueAt",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_getValueAt64].setDisplayName("");
			methods[METHOD_gotFocus65] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("gotFocus",
				java.awt.Event.class, Object.class));
			methods[METHOD_gotFocus65].setDisplayName("");
			methods[METHOD_grabFocus66] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("grabFocus"));
			methods[METHOD_grabFocus66].setDisplayName("");
			methods[METHOD_handleEvent67] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("handleEvent",
				java.awt.Event.class));
			methods[METHOD_handleEvent67].setDisplayName("");
			methods[METHOD_hasFocus68] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("hasFocus"));
			methods[METHOD_hasFocus68].setDisplayName("");
			methods[METHOD_hide69] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("hide"));
			methods[METHOD_hide69].setDisplayName("");
			methods[METHOD_imageUpdate70] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("imageUpdate",
				java.awt.Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE,
				Integer.TYPE));
			methods[METHOD_imageUpdate70].setDisplayName("");
			methods[METHOD_insets71] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("insets"));
			methods[METHOD_insets71].setDisplayName("");
			methods[METHOD_inside72] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("inside",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_inside72].setDisplayName("");
			methods[METHOD_invalidate73] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("invalidate"));
			methods[METHOD_invalidate73].setDisplayName("");
			methods[METHOD_isAncestorOf74] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("isAncestorOf",
				java.awt.Component.class));
			methods[METHOD_isAncestorOf74].setDisplayName("");
			methods[METHOD_isCellEditable75] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("isCellEditable",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_isCellEditable75].setDisplayName("");
			methods[METHOD_isCellSelected76] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("isCellSelected",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_isCellSelected76].setDisplayName("");
			methods[METHOD_isColumnSelected77] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("isColumnSelected",
				Integer.TYPE));
			methods[METHOD_isColumnSelected77].setDisplayName("");
			methods[METHOD_isFocusCycleRoot78] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("isFocusCycleRoot",
				java.awt.Container.class));
			methods[METHOD_isFocusCycleRoot78].setDisplayName("");
			methods[METHOD_isLightweightComponent79] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("isLightweightComponent",
				java.awt.Component.class));
			methods[METHOD_isLightweightComponent79].setDisplayName("");
			methods[METHOD_isRowSelected80] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("isRowSelected",
				Integer.TYPE));
			methods[METHOD_isRowSelected80].setDisplayName("");
			methods[METHOD_keyDown81] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("keyDown",
				java.awt.Event.class, Integer.TYPE));
			methods[METHOD_keyDown81].setDisplayName("");
			methods[METHOD_keyUp82] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("keyUp",
				java.awt.Event.class, Integer.TYPE));
			methods[METHOD_keyUp82].setDisplayName("");
			methods[METHOD_layout83] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("layout"));
			methods[METHOD_layout83].setDisplayName("");
			methods[METHOD_list84] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("list",
				java.io.PrintStream.class, Integer.TYPE));
			methods[METHOD_list84].setDisplayName("");
			methods[METHOD_locate85] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("locate",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_locate85].setDisplayName("");
			methods[METHOD_location86] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("location"));
			methods[METHOD_location86].setDisplayName("");
			methods[METHOD_lostFocus87] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("lostFocus",
				java.awt.Event.class, Object.class));
			methods[METHOD_lostFocus87].setDisplayName("");
			methods[METHOD_minimumSize88] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("minimumSize"));
			methods[METHOD_minimumSize88].setDisplayName("");
			methods[METHOD_mouseDown89] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("mouseDown",
				java.awt.Event.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_mouseDown89].setDisplayName("");
			methods[METHOD_mouseDrag90] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("mouseDrag",
				java.awt.Event.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_mouseDrag90].setDisplayName("");
			methods[METHOD_mouseEnter91] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("mouseEnter",
				java.awt.Event.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_mouseEnter91].setDisplayName("");
			methods[METHOD_mouseExit92] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("mouseExit",
				java.awt.Event.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_mouseExit92].setDisplayName("");
			methods[METHOD_mouseMove93] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("mouseMove",
				java.awt.Event.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_mouseMove93].setDisplayName("");
			methods[METHOD_mouseUp94] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("mouseUp",
				java.awt.Event.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_mouseUp94].setDisplayName("");
			methods[METHOD_move95] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("move",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_move95].setDisplayName("");
			methods[METHOD_moveColumn96] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("moveColumn",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_moveColumn96].setDisplayName("");
			methods[METHOD_nextFocus97] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("nextFocus"));
			methods[METHOD_nextFocus97].setDisplayName("");
			methods[METHOD_paint98] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("paint",
				java.awt.Graphics.class));
			methods[METHOD_paint98].setDisplayName("");
			methods[METHOD_paintAll99] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("paintAll",
				java.awt.Graphics.class));
			methods[METHOD_paintAll99].setDisplayName("");
			methods[METHOD_paintComponents100] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("paintComponents",
				java.awt.Graphics.class));
			methods[METHOD_paintComponents100].setDisplayName("");
			methods[METHOD_paintImmediately101] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("paintImmediately",
				Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE));
			methods[METHOD_paintImmediately101].setDisplayName("");
			methods[METHOD_postEvent102] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("postEvent",
				java.awt.Event.class));
			methods[METHOD_postEvent102].setDisplayName("");
			methods[METHOD_preferredSize103] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("preferredSize"));
			methods[METHOD_preferredSize103].setDisplayName("");
			methods[METHOD_prepareEditor104] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("prepareEditor",
				javax.swing.table.TableCellEditor.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_prepareEditor104].setDisplayName("");
			methods[METHOD_prepareImage105] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("prepareImage",
				java.awt.Image.class, java.awt.image.ImageObserver.class));
			methods[METHOD_prepareImage105].setDisplayName("");
			methods[METHOD_prepareRenderer106] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("prepareRenderer",
				javax.swing.table.TableCellRenderer.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_prepareRenderer106].setDisplayName("");
			methods[METHOD_print107] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("print",
				java.awt.Graphics.class));
			methods[METHOD_print107].setDisplayName("");
			methods[METHOD_printAll108] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("printAll",
				java.awt.Graphics.class));
			methods[METHOD_printAll108].setDisplayName("");
			methods[METHOD_printComponents109] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("printComponents",
				java.awt.Graphics.class));
			methods[METHOD_printComponents109].setDisplayName("");
			methods[METHOD_putClientProperty110] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("putClientProperty",
				Object.class, Object.class));
			methods[METHOD_putClientProperty110].setDisplayName("");
			methods[METHOD_registerKeyboardAction111] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("registerKeyboardAction",
				java.awt.event.ActionListener.class, String.class, javax.swing.KeyStroke.class,
				Integer.TYPE));
			methods[METHOD_registerKeyboardAction111].setDisplayName("");
			methods[METHOD_remove112] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("remove",
				Integer.TYPE));
			methods[METHOD_remove112].setDisplayName("");
			methods[METHOD_removeAll113] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("removeAll"));
			methods[METHOD_removeAll113].setDisplayName("");
			methods[METHOD_removeColumn114] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("removeColumn",
				javax.swing.table.TableColumn.class));
			methods[METHOD_removeColumn114].setDisplayName("");
			methods[METHOD_removeColumnSelectionInterval115] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("removeColumnSelectionInterval",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_removeColumnSelectionInterval115].setDisplayName("");
			methods[METHOD_removeEditor116] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("removeEditor"));
			methods[METHOD_removeEditor116].setDisplayName("");
			methods[METHOD_removeNotify117] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("removeNotify"));
			methods[METHOD_removeNotify117].setDisplayName("");
			methods[METHOD_removePropertyChangeListener118] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("removePropertyChangeListener",
				String.class, java.beans.PropertyChangeListener.class));
			methods[METHOD_removePropertyChangeListener118].setDisplayName("");
			methods[METHOD_removeRowSelectionInterval119] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("removeRowSelectionInterval",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_removeRowSelectionInterval119].setDisplayName("");
			methods[METHOD_repaint120] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("repaint",
				Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE));
			methods[METHOD_repaint120].setDisplayName("");
			methods[METHOD_requestDefaultFocus121] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("requestDefaultFocus"));
			methods[METHOD_requestDefaultFocus121].setDisplayName("");
			methods[METHOD_requestFocus122] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("requestFocus"));
			methods[METHOD_requestFocus122].setDisplayName("");
			methods[METHOD_requestFocusInWindow123] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("requestFocusInWindow"));
			methods[METHOD_requestFocusInWindow123].setDisplayName("");
			methods[METHOD_resetKeyboardActions124] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("resetKeyboardActions"));
			methods[METHOD_resetKeyboardActions124].setDisplayName("");
			methods[METHOD_reshape125] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("reshape",
				Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE));
			methods[METHOD_reshape125].setDisplayName("");
			methods[METHOD_resize126] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("resize",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_resize126].setDisplayName("");
			methods[METHOD_revalidate127] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("revalidate"));
			methods[METHOD_revalidate127].setDisplayName("");
			methods[METHOD_rowAtPoint128] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("rowAtPoint",
				java.awt.Point.class));
			methods[METHOD_rowAtPoint128].setDisplayName("");
			methods[METHOD_scrollRectToVisible129] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("scrollRectToVisible",
				java.awt.Rectangle.class));
			methods[METHOD_scrollRectToVisible129].setDisplayName("");
			methods[METHOD_selectAll130] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("selectAll"));
			methods[METHOD_selectAll130].setDisplayName("");
			methods[METHOD_setBounds131] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setBounds",
				Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE));
			methods[METHOD_setBounds131].setDisplayName("");
			methods[METHOD_setDefaultEditor132] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setDefaultEditor",
				Class.class, javax.swing.table.TableCellEditor.class));
			methods[METHOD_setDefaultEditor132].setDisplayName("");
			methods[METHOD_setDefaultLocale133] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setDefaultLocale",
				java.util.Locale.class));
			methods[METHOD_setDefaultLocale133].setDisplayName("");
			methods[METHOD_setDefaultRenderer134] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setDefaultRenderer",
				Class.class, javax.swing.table.TableCellRenderer.class));
			methods[METHOD_setDefaultRenderer134].setDisplayName("");
			methods[METHOD_setInputMap135] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setInputMap",
				Integer.TYPE, javax.swing.InputMap.class));
			methods[METHOD_setInputMap135].setDisplayName("");
			methods[METHOD_setLocation136] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setLocation",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_setLocation136].setDisplayName("");
			methods[METHOD_setRowHeight137] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setRowHeight",
				Integer.TYPE));
			methods[METHOD_setRowHeight137].setDisplayName("");
			methods[METHOD_setSize138] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setSize",
				Integer.TYPE, Integer.TYPE));
			methods[METHOD_setSize138].setDisplayName("");
			methods[METHOD_setValueAt139] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("setValueAt",
				Object.class, Integer.TYPE, Integer.TYPE));
			methods[METHOD_setValueAt139].setDisplayName("");
			methods[METHOD_show140] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("show"));
			methods[METHOD_show140].setDisplayName("");
			methods[METHOD_size141] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("size"));
			methods[METHOD_size141].setDisplayName("");
			methods[METHOD_sizeColumnsToFit142] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("sizeColumnsToFit",
				Boolean.TYPE));
			methods[METHOD_sizeColumnsToFit142].setDisplayName("");
			methods[METHOD_tableChanged143] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("tableChanged",
				javax.swing.event.TableModelEvent.class));
			methods[METHOD_tableChanged143].setDisplayName("");
			methods[METHOD_toString144] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("toString"));
			methods[METHOD_toString144].setDisplayName("");
			methods[METHOD_transferFocus145] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("transferFocus"));
			methods[METHOD_transferFocus145].setDisplayName("");
			methods[METHOD_transferFocusBackward146] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("transferFocusBackward"));
			methods[METHOD_transferFocusBackward146].setDisplayName("");
			methods[METHOD_transferFocusDownCycle147] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("transferFocusDownCycle"));
			methods[METHOD_transferFocusDownCycle147].setDisplayName("");
			methods[METHOD_transferFocusUpCycle148] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("transferFocusUpCycle"));
			methods[METHOD_transferFocusUpCycle148].setDisplayName("");
			methods[METHOD_unregisterKeyboardAction149] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("unregisterKeyboardAction",
				javax.swing.KeyStroke.class));
			methods[METHOD_unregisterKeyboardAction149].setDisplayName("");
			methods[METHOD_update150] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("update",
				java.awt.Graphics.class));
			methods[METHOD_update150].setDisplayName("");
			methods[METHOD_updateUI151] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("updateUI"));
			methods[METHOD_updateUI151].setDisplayName("");
			methods[METHOD_validate152] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("validate"));
			methods[METHOD_validate152].setDisplayName("");
			methods[METHOD_valueChanged153] = new MethodDescriptor(com.fr3ts0n.pvs.gui.PvTable.class.getMethod("valueChanged",
				javax.swing.event.ListSelectionEvent.class));
			methods[METHOD_valueChanged153].setDisplayName("");
		} catch (Exception ignored)
		{
		}//GEN-HEADEREND:Methods

		// Here you can add code for customizing the methods array.

		return methods;
	}//GEN-LAST:Methods

	private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
	private static final int defaultEventIndex = -1;//GEN-END:Idx
//GEN-FIRST:Superclass
	// Here you can add code for customizing the Superclass BeanInfo.
//GEN-LAST:Superclass

	/**
	 * Gets the bean's <code>BeanDescriptor</code>s.
	 *
	 * @return BeanDescriptor describing the editable
	 * properties of this bean.  May return null if the
	 * information should be obtained by automatic analysis.
	 */
	public BeanDescriptor getBeanDescriptor()
	{
		return getBdescriptor();
	}

	/**
	 * Gets the bean's <code>PropertyDescriptor</code>s.
	 *
	 * @return An array of PropertyDescriptors describing the editable
	 * properties supported by this bean.  May return null if the
	 * information should be obtained by automatic analysis.
	 * If a property is indexed, then its entry in the result array will
	 * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
	 * A client of getPropertyDescriptors can use "instanceof" to check
	 * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
	 */
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return getPdescriptor();
	}

	/**
	 * Gets the bean's <code>EventSetDescriptor</code>s.
	 *
	 * @return An array of EventSetDescriptors describing the kinds of
	 * events fired by this bean.  May return null if the information
	 * should be obtained by automatic analysis.
	 */
	public EventSetDescriptor[] getEventSetDescriptors()
	{
		return getEdescriptor();
	}

	/**
	 * Gets the bean's <code>MethodDescriptor</code>s.
	 *
	 * @return An array of MethodDescriptors describing the methods
	 * implemented by this bean.  May return null if the information
	 * should be obtained by automatic analysis.
	 */
	public MethodDescriptor[] getMethodDescriptors()
	{
		return getMdescriptor();
	}

	/**
	 * A bean may have a "default" property that is the property that will
	 * mostly commonly be initially chosen for update by human's who are
	 * customizing the bean.
	 *
	 * @return Index of default property in the PropertyDescriptor array
	 * returned by getPropertyDescriptors.
	 * <P>	Returns -1 if there is no default property.
	 */
	public int getDefaultPropertyIndex()
	{
		return defaultPropertyIndex;
	}

	/**
	 * A bean may have a "default" event that is the event that will
	 * mostly commonly be used by human's when using the bean.
	 *
	 * @return Index of default event in the EventSetDescriptor array
	 * returned by getEventSetDescriptors.
	 * <P>	Returns -1 if there is no default event.
	 */
	public int getDefaultEventIndex()
	{
		return defaultEventIndex;
	}
}

