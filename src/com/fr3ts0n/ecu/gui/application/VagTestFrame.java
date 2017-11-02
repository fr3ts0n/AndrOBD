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

package com.fr3ts0n.ecu.gui.application;

import com.fr3ts0n.common.UTF8Bundle;
import com.fr3ts0n.common.UTF8Control;
import com.fr3ts0n.ecu.EcuCodeItem;
import com.fr3ts0n.ecu.EcuCodeList;
import com.fr3ts0n.ecu.EcuDataItem;
import com.fr3ts0n.ecu.EcuDataPv;
import com.fr3ts0n.ecu.prot.vag.Kw1281Prot;
import com.fr3ts0n.prot.gui.KLHandlerGeneric;
import com.fr3ts0n.pvs.PvList;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jfree.data.time.TimeSeries;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 * Main application frame for OBD com.fr3ts0n.test application
 *
 * @author erwin
 */
public class VagTestFrame extends javax.swing.JFrame
	implements PropertyChangeListener
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3393967156524083211L;

	/**
	 * Program version information
	 */

	/** prduct name string */
	static final String product = "JaVAG Diagnose";
	/** product version string */
	static final String version = "V0.8.0";
	/** copyright string */
	static final String copyright = "Copyright (C) 2009-2010 Erwin Scheuch-Heilig";
	/** Initialize UTF8 resource bundle */
	static UTF8Bundle res = new UTF8Bundle(new UTF8Control());
	/** application icon */
	static final ImageIcon appIcon = new ImageIcon(VagTestFrame.class.getResource("/com/fr3ts0n/ecu/gui/res/JaVAG_Logo.png"));
	/** Application Logger */
	static final Logger log = Logger.getLogger("app");

	/** protocol handler */
	static Kw1281Prot prt = new Kw1281Prot();
	/** Serial communication handler */
	// static KLHandler ser = new KLHandler();
	static KLHandlerGeneric ser = new KLHandlerGeneric();

	/** is this a simulation, or the real world? */
	static boolean isSimulation = false;
	/** ECU addresses */
	static EcuCodeList AddressList = new EcuCodeList("com.fr3ts0n.ecu.prot.vag.res.ecuadr", 16);
	/**
	 * Action listener to handle read/clear code actions
	 */
	ActionListener hdlrCodeButtons = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// if source is not defined, ignore event
			if (e.getSource() == null) return;

			if (e.getActionCommand().equals("ReadCodes"))
			{
				prt.setService(Kw1281Prot.SVC_READ_DFCS);
			} else if (e.getActionCommand().equals("ClearCodes"))
			{
				prt.setService(Kw1281Prot.SVC_CLEAR_DFCS);
			}
		}
	};

	/**
	 * Property change listener to ELM-Protocol
	 *
	 * @param evt the property change event to be handled
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		Object val = evt.getNewValue();
	/* handle protocol status changes */
		if ("status".equals(evt.getPropertyName()))
		{
			log.fine("Com status:" + val.toString());
			if (val != null) lblStatus.setText(val.toString());
			lblStatus.setBackground(KLHandlerGeneric.statColor[((KLHandlerGeneric.ProtStatus) val).ordinal()]);

			// re-connect ECU if no shutdown service has been selected
			if (prt.getService() != Kw1281Prot.SVC_FINISHED)
			{
				// on connection error, set service to finished
				if (KLHandlerGeneric.ProtStatus.ERROR.equals(val))
					prt.setService(Kw1281Prot.SVC_FINISHED);

				// on timeout w/o shutdown try to re-connect
				if (KLHandlerGeneric.ProtStatus.TIMEOUT.equals(val))
					connectEcu(null);
			}
		} else if ("baud".equals(evt.getPropertyName()))
		{
			if (val != null) lblBaud.setText(val.toString());
		} else if ("preset".equals(evt.getPropertyName()))
		{
      /* update group selector only if preset value has changed
       * This is because update group selector changes selected data
       * group to ALL (since list items get cleared once)
       */
			if (evt.getOldValue() == null
				|| !evt.getNewValue().equals(evt.getOldValue()))
			{
				updateGroupSelector();
			}
		}
	}

	/** Creates new form VagTestFrame */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public VagTestFrame()
	{
		// set up serial handler and protocol drivers
		ser.setMessageHandler(prt);
		prt.addTelegramWriter(ser);
		initComponents();
		setIconImage(appIcon.getImage());

		// initialize About-Dialog
		panAbout.setApplicationName(product);
		panAbout.setApplicationVersion(version);
		panAbout.setCopyrightString(copyright);
		panAbout.setIcon(appIcon);

		// initialize other dialogs
		panObdData.setPidPvs(Kw1281Prot.PidPvs);
		panObdData.setTitle("Data Graph");

		// initialize DTC list
		panObdDtc.setTcList(Kw1281Prot.tCodes);
		panObdDtc.btnReadPending.setVisible(false);
		panObdDtc.btnReadPermanent.setVisible(false);
		panObdDtc.addActionListener(hdlrCodeButtons);

    /* handle number of DTC changes */
		prt.addPropertyChangeListener(panObdDtc);

    /* handle protocol status changes */
		ser.addPropertyChangeListener(this);
		prt.addPropertyChangeListener(this);

		Object[] adresses = AddressList.values().toArray();
		Arrays.sort(adresses);
		cbAddress.setModel(new DefaultComboBoxModel(adresses));

		// put logging messages of root logger to the status bar
		Logger.getLogger("").addHandler(new Handler(){

			@Override
			public void close() throws SecurityException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void publish(LogRecord arg0) {
				lblMessage.setText(arg0.getMessage());
				
			}
			
		});

		// initially update group selector
		updateGroupSelector();
	}

	/**
	 * Update ComboBox for group selection
	 */
	@SuppressWarnings("unchecked")
	void updateGroupSelector()
	{
		cbFrameNum.removeAllItems();
		cbFrameNum.addItem("All Groups");
		Object[] frames = prt.knownGrpItems.keySet().toArray();
		Arrays.sort(frames);
		for (int i = 0; i < frames.length; i++)
		{
			cbFrameNum.addItem(String.format("Group %s", frames[i]));
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void initComponents()
	{
		java.awt.GridBagConstraints gridBagConstraints;

		fChoose = new javax.swing.JFileChooser();
		panAbout = new com.fr3ts0n.ecu.gui.application.AboutPanel();
		tabMain = new javax.swing.JTabbedPane();
		panStart = new javax.swing.JPanel();
		lblFooter = new javax.swing.JLabel();
		jPanel1 = new javax.swing.JPanel();
		lblTitle = new javax.swing.JLabel();
		TblVehIDs = new com.fr3ts0n.pvs.gui.PvTable(Kw1281Prot.VidPvs);
		jLabel1 = new javax.swing.JLabel();
		panObdDtc = new com.fr3ts0n.ecu.gui.application.ObdDtcPanel();
		panObdData = new com.fr3ts0n.ecu.gui.application.ObdDataPanel();
		panFooter = new javax.swing.JPanel();
		lblMessage = new javax.swing.JLabel();
		lblStatus = new javax.swing.JLabel();
		lblBaud = new javax.swing.JLabel();
		cbCnvSystem = new javax.swing.JComboBox();
		panHeader = new javax.swing.JPanel();
		tbMain = new javax.swing.JToolBar();
		btnLoad = new javax.swing.JButton();
		btnSave = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JToolBar.Separator();
		btnConnect = new javax.swing.JButton();
		btnStop = new javax.swing.JButton();
		btnConfig = new javax.swing.JButton();
		jSeparator2 = new javax.swing.JToolBar.Separator();
		cbAddress = new javax.swing.JComboBox();
		jSeparator3 = new javax.swing.JToolBar.Separator();
		cbFrameNum = new javax.swing.JComboBox();
		mbMain = new javax.swing.JMenuBar();
		mnuFile = new javax.swing.JMenu();
		miLoad = new javax.swing.JMenuItem();
		miSave = new javax.swing.JMenuItem();
		mnuComm = new javax.swing.JMenu();
		miCommConfigure = new javax.swing.JMenuItem();
		miCommInit = new javax.swing.JMenuItem();
		miCommStop = new javax.swing.JMenuItem();
		mnuHelp = new javax.swing.JMenu();
		miAbout = new javax.swing.JMenuItem();

		FormListener formListener = new FormListener();

		fChoose.setFileFilter(new ObdFileFilter());
		fChoose.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);

		panAbout.setIcon(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle(product + " " + version);

		tabMain.setFont(new java.awt.Font("Dialog", 0, 10));
		tabMain.setPreferredSize(new java.awt.Dimension(520, 350));
		tabMain.addChangeListener(formListener);

		panStart.setBackground(new java.awt.Color(255, 255, 255));
		panStart.setLayout(new java.awt.BorderLayout());

		lblFooter.setFont(new java.awt.Font("Dialog", 0, 10));
		lblFooter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblFooter.setText(copyright);
		panStart.add(lblFooter, java.awt.BorderLayout.SOUTH);

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		jPanel1.setLayout(new java.awt.BorderLayout(0, 25));

		lblTitle.setFont(new java.awt.Font("Dialog", 1, 18));
		lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblTitle.setText(product);
		lblTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		jPanel1.add(lblTitle, java.awt.BorderLayout.NORTH);

		TblVehIDs.setAutoResizeMode(5);
		TblVehIDs.setFocusable(false);
		TblVehIDs.setName("TblVids"); // NOI18N
		TblVehIDs.setOpaque(false);
		TblVehIDs.setRowSelectionAllowed(false);
		TblVehIDs.setShowGrid(false);
		jPanel1.add(TblVehIDs, java.awt.BorderLayout.CENTER);

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fr3ts0n/ecu/gui/res/javag.png"))); // NOI18N
		jPanel1.add(jLabel1, java.awt.BorderLayout.SOUTH);

		panStart.add(jPanel1, java.awt.BorderLayout.CENTER);

		tabMain.addTab("About", panStart);
		tabMain.addTab("Fault Codes", panObdDtc);
		tabMain.addTab("Data", panObdData);

		getContentPane().add(tabMain, java.awt.BorderLayout.CENTER);

		panFooter.setLayout(new java.awt.GridBagLayout());

		lblMessage.setFont(new java.awt.Font("Dialog", 0, 10));
		lblMessage.setText(String.format("%s %s", product, version));
		lblMessage.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		panFooter.add(lblMessage, gridBagConstraints);

		lblStatus.setFont(new java.awt.Font("Dialog", 0, 10));
		lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblStatus.setText("Status");
		lblStatus.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3)));
		lblStatus.setMinimumSize(new java.awt.Dimension(90, 18));
		lblStatus.setOpaque(true);
		lblStatus.setPreferredSize(new java.awt.Dimension(90, 18));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		panFooter.add(lblStatus, gridBagConstraints);

		lblBaud.setFont(new java.awt.Font("Dialog", 0, 10));
		lblBaud.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
		lblBaud.setText("BaudRate");
		lblBaud.setToolTipText("Baud rate");
		lblBaud.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		lblBaud.setMinimumSize(new java.awt.Dimension(54, 16));
		lblBaud.setPreferredSize(new java.awt.Dimension(54, 16));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		panFooter.add(lblBaud, gridBagConstraints);

		cbCnvSystem.setFont(new java.awt.Font("Dialog", 0, 10));
		cbCnvSystem.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Metric", "Imperial"}));
		cbCnvSystem.setToolTipText("Select conversion system");
		cbCnvSystem.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		cbCnvSystem.setPreferredSize(new java.awt.Dimension(71, 23));
		cbCnvSystem.addItemListener(formListener);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		panFooter.add(cbCnvSystem, gridBagConstraints);

		getContentPane().add(panFooter, java.awt.BorderLayout.SOUTH);

		panHeader.setLayout(new java.awt.BorderLayout());

		tbMain.setFloatable(false);
		tbMain.setRollover(true);
		tbMain.setFont(new java.awt.Font("Dialog", 0, 10));

		btnLoad.setFont(new java.awt.Font("Dialog", 0, 10));
		btnLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fr3ts0n/common/res/fileopen.png"))); // NOI18N
		btnLoad.setToolTipText("Load measurements");
		btnLoad.setBorderPainted(false);
		btnLoad.setFocusable(false);
		btnLoad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnLoad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		btnLoad.addActionListener(formListener);
		tbMain.add(btnLoad);

		btnSave.setFont(new java.awt.Font("Dialog", 0, 10));
		btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fr3ts0n/common/res/filesaveas.png"))); // NOI18N
		btnSave.setToolTipText("Save  measurements");
		btnSave.setBorderPainted(false);
		btnSave.setFocusable(false);
		btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		btnSave.addActionListener(formListener);
		tbMain.add(btnSave);
		tbMain.add(jSeparator1);

		btnConnect.setFont(new java.awt.Font("Dialog", 0, 10));
		btnConnect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fr3ts0n/common/res/connect_established.png"))); // NOI18N
		btnConnect.setToolTipText("Start communication");
		btnConnect.setBorderPainted(false);
		btnConnect.setFocusable(false);
		btnConnect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnConnect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		btnConnect.addActionListener(formListener);
		tbMain.add(btnConnect);

		btnStop.setFont(new java.awt.Font("Dialog", 0, 10));
		btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fr3ts0n/common/res/no.png"))); // NOI18N
		btnStop.setToolTipText("Stop communication");
		btnStop.setBorderPainted(false);
		btnStop.setFocusable(false);
		btnStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		btnStop.addActionListener(formListener);
		tbMain.add(btnStop);

		btnConfig.setFont(new java.awt.Font("Dialog", 0, 10));
		btnConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fr3ts0n/common/res/fileexport.png"))); // NOI18N
		btnConfig.setToolTipText("Serial port configuratopn");
		btnConfig.setBorderPainted(false);
		btnConfig.setFocusable(false);
		btnConfig.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		btnConfig.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		btnConfig.addActionListener(formListener);
		tbMain.add(btnConfig);
		tbMain.add(jSeparator2);

		cbAddress.setFont(new java.awt.Font("Dialog", 0, 10));
		cbAddress.setToolTipText("Select device/address");
		cbAddress.addActionListener(formListener);
		tbMain.add(cbAddress);
		tbMain.add(jSeparator3);

		cbFrameNum.setFont(new java.awt.Font("Dialog", 0, 10));
		cbFrameNum.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
		cbFrameNum.setToolTipText("select Frame number to display");
		cbFrameNum.addActionListener(formListener);
		tbMain.add(cbFrameNum);

		panHeader.add(tbMain, java.awt.BorderLayout.NORTH);

		getContentPane().add(panHeader, java.awt.BorderLayout.NORTH);

		mbMain.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

		mnuFile.setMnemonic('F');
		mnuFile.setText("File");
		mnuFile.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

		miLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
		miLoad.setFont(new java.awt.Font("Dialog", 0, 10));
		miLoad.setMnemonic('L');
		miLoad.setText("Load measurement");
		miLoad.addActionListener(formListener);
		mnuFile.add(miLoad);

		miSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
		miSave.setFont(new java.awt.Font("Dialog", 0, 10));
		miSave.setMnemonic('S');
		miSave.setText("Save measurement");
		miSave.addActionListener(formListener);
		mnuFile.add(miSave);

		mbMain.add(mnuFile);

		mnuComm.setMnemonic('C');
		mnuComm.setText("Communication");
		mnuComm.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

		miCommConfigure.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
		miCommConfigure.setFont(new java.awt.Font("Dialog", 0, 10));
		miCommConfigure.setMnemonic('C');
		miCommConfigure.setText("Port Configuration...");
		miCommConfigure.addActionListener(formListener);
		mnuComm.add(miCommConfigure);

		miCommInit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
		miCommInit.setFont(new java.awt.Font("Dialog", 0, 10));
		miCommInit.setMnemonic('I');
		miCommInit.setText("Initialize");
		miCommInit.addActionListener(formListener);
		mnuComm.add(miCommInit);

		miCommStop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
		miCommStop.setFont(new java.awt.Font("Dialog", 0, 10));
		miCommStop.setMnemonic('p');
		miCommStop.setText("Stop");
		miCommStop.addActionListener(formListener);
		mnuComm.add(miCommStop);

		mbMain.add(mnuComm);

		mnuHelp.setMnemonic('H');
		mnuHelp.setText("Help");
		mnuHelp.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

		miAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
		miAbout.setFont(new java.awt.Font("Dialog", 0, 10));
		miAbout.setMnemonic('A');
		miAbout.setText("About");
		miAbout.addActionListener(formListener);
		mnuHelp.add(miAbout);

		mbMain.add(mnuHelp);

		setJMenuBar(mbMain);

		pack();
	}

	// Code for dispatching events from components to event handlers.

	private class FormListener implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.ChangeListener
	{
		FormListener()
		{
		}

		public void actionPerformed(java.awt.event.ActionEvent evt)
		{
			if (evt.getSource() == btnLoad)
			{
				VagTestFrame.this.miLoadActionPerformed(evt);
			} else if (evt.getSource() == btnSave)
			{
				VagTestFrame.this.miSaveActionPerformed(evt);
			} else if (evt.getSource() == btnConnect)
			{
				VagTestFrame.this.connectEcu(evt);
			} else if (evt.getSource() == btnStop)
			{
				VagTestFrame.this.miCommStopActionPerformed(evt);
			} else if (evt.getSource() == btnConfig)
			{
				VagTestFrame.this.miCommConfigureActionPerformed(evt);
			} else if (evt.getSource() == cbAddress)
			{
				VagTestFrame.this.cbAddressActionPerformed(evt);
			} else if (evt.getSource() == cbFrameNum)
			{
				VagTestFrame.this.cbFrameNumActionPerformed(evt);
			} else if (evt.getSource() == miLoad)
			{
				VagTestFrame.this.miLoadActionPerformed(evt);
			} else if (evt.getSource() == miSave)
			{
				VagTestFrame.this.miSaveActionPerformed(evt);
			} else if (evt.getSource() == miCommConfigure)
			{
				VagTestFrame.this.miCommConfigureActionPerformed(evt);
			} else if (evt.getSource() == miCommInit)
			{
				VagTestFrame.this.connectEcu(evt);
			} else if (evt.getSource() == miCommStop)
			{
				VagTestFrame.this.miCommStopActionPerformed(evt);
			} else if (evt.getSource() == miAbout)
			{
				VagTestFrame.this.miAboutActionPerformed(evt);
			}
		}

		public void itemStateChanged(java.awt.event.ItemEvent evt)
		{
			if (evt.getSource() == cbCnvSystem)
			{
				VagTestFrame.this.cbCnvSystemItemStateChanged(evt);
			}
		}

		public void stateChanged(javax.swing.event.ChangeEvent evt)
		{
			if (evt.getSource() == tabMain)
			{
				VagTestFrame.this.tabMainStateChanged(evt);
			}
		}
	}// </editor-fold>//GEN-END:initComponents

	private void miCommConfigureActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miCommConfigureActionPerformed
	{//GEN-HEADEREND:event_miCommConfigureActionPerformed
		ser.configure();
	}//GEN-LAST:event_miCommConfigureActionPerformed

	private void miCommStopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miCommStopActionPerformed
	{//GEN-HEADEREND:event_miCommStopActionPerformed
		// switch off PID's supported'
		prt.setService(Kw1281Prot.SVC_SHUTDOWN);
	}//GEN-LAST:event_miCommStopActionPerformed

	private void miSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miSaveActionPerformed
	{//GEN-HEADEREND:event_miSaveActionPerformed
		if (fChoose.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fChoose.getSelectedFile();
			// ask for overwrite existing file
			if (!file.exists()
				|| JOptionPane.showConfirmDialog(this,
				"Really want to overwrite " + file.getPath(),
				"File overwrite",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				try
				{

					FileOutputStream out = new FileOutputStream(file);
					ObjectOutputStream oOut = new ObjectOutputStream(out);
        /* remember current measurement page for loading again */
					Integer currPage = new Integer(tabMain.getSelectedIndex());
					oOut.writeObject(currPage);
        /* save the data */
					oOut.writeObject(Kw1281Prot.VidPvs);
					oOut.writeObject(Kw1281Prot.PidPvs);
					oOut.writeObject(Kw1281Prot.tCodes);
					oOut.writeObject(panObdData.selPids);
					oOut.close();
				} catch (IOException ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this,
						ex.getLocalizedMessage(),
						"Save ERROR",
						JOptionPane.ERROR_MESSAGE);
				}
		}
	}//GEN-LAST:event_miSaveActionPerformed

	@SuppressWarnings({"unchecked"})
	private void miLoadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miLoadActionPerformed
	{//GEN-HEADEREND:event_miLoadActionPerformed
		if (fChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fChoose.getSelectedFile();
			try
			{
				FileInputStream in = new FileInputStream(file);
				ObjectInputStream oIn = new ObjectInputStream(in);
        /* ensure that measurement page is activated
           to avoid deletion of loaded data afterwards */
				Integer currPage = (Integer) oIn.readObject();
				tabMain.setSelectedIndex(currPage);
        /* read in the data */
				Kw1281Prot.VidPvs = (PvList) oIn.readObject();
				Kw1281Prot.PidPvs = (PvList) oIn.readObject();
				Kw1281Prot.tCodes = (PvList) oIn.readObject();
				// re-setup data connection
				panObdData.setPidPvs(Kw1281Prot.PidPvs);
				panObdDtc.setTcList(Kw1281Prot.tCodes);
				TblVehIDs.setProcessVar(Kw1281Prot.VidPvs);
				// read measurement history
				panObdData.selPids = (HashMap<Object, TimeSeries>) oIn.readObject();
				oIn.close();
			} catch (Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this,
					ex.getMessage(),
					"Load ERROR",
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}//GEN-LAST:event_miLoadActionPerformed

	/**
	 * handle change of conversion system
	 */
	private void cbCnvSystemItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_cbCnvSystemItemStateChanged
	{//GEN-HEADEREND:event_cbCnvSystemItemStateChanged
		// set new conversion system
		EcuDataItem.cnvSystem = cbCnvSystem.getSelectedIndex();

		// update currently selected display
		switch (tabMain.getSelectedIndex())
		{
			case 2:
				panObdData.updateAllTableRows(EcuDataPv.FID_UNITS);
				break;

			default:
				// intentionally do nothing ...
		}
	}//GEN-LAST:event_cbCnvSystemItemStateChanged

	/**
	 * update form/tab selection
	 */
	private void tabMainStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_tabMainStateChanged
	{//GEN-HEADEREND:event_tabMainStateChanged
		// request OBD service for selected Tab
		requestServiceForSelectedTab();
	}//GEN-LAST:event_tabMainStateChanged

	private void cbAddressActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbAddressActionPerformed
	{//GEN-HEADEREND:event_cbAddressActionPerformed
		EcuCodeItem itm = (EcuCodeItem) cbAddress.getSelectedItem();
		int newAddress = Integer.parseInt(itm.get(EcuCodeItem.FID_CODE).toString(), 16);
		if (newAddress > 0)
		{
			setControllerAddress(newAddress);
		}
	}//GEN-LAST:event_cbAddressActionPerformed

	private void cbFrameNumActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbFrameNumActionPerformed
	{//GEN-HEADEREND:event_cbFrameNumActionPerformed
		int sel = cbFrameNum.getSelectedIndex();
		// single selection
		if (sel > 0 && sel < prt.knownGrpItems.size())
		{
			String selStr = cbFrameNum.getSelectedItem().toString();
			int frmNum = Integer.parseInt(selStr.replaceAll("Group ", "").trim());
			prt.setSelectedDataGroup((char) frmNum);
		}
		// request corresponding service
		requestServiceForSelectedTab();
	}//GEN-LAST:event_cbFrameNumActionPerformed

	private void connectEcu(java.awt.event.ActionEvent evt)//GEN-FIRST:event_connectEcu
	{//GEN-HEADEREND:event_connectEcu
		Kw1281Prot.VidPvs.clear();
		ser.start();
	}//GEN-LAST:event_connectEcu

	private void miAboutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miAboutActionPerformed
	{//GEN-HEADEREND:event_miAboutActionPerformed
		JOptionPane.showMessageDialog(this, panAbout, "About ...", JOptionPane.PLAIN_MESSAGE);
	}//GEN-LAST:event_miAboutActionPerformed

	/**
	 * request corresponding OBD service for selected Tab
	 */
	private void requestServiceForSelectedTab()
	{
		// handle page change ...
		switch (tabMain.getSelectedIndex())
		{
			case 0: // About panel
				// switch off PID's supported'
				prt.setService(Kw1281Prot.SVC_NONE);
				break;

			case 1: // Trouble code panel
				// we don't set any service here
				// since service is selected by buttons
				prt.setService(Kw1281Prot.SVC_NONE);
				break;

			case 2: // data item panel
				// set service depending on ALL / SINGLE Selection
				prt.setService(cbFrameNum.getSelectedIndex() == 0
					? Kw1281Prot.SVC_READ_DATA_ALL
					: Kw1281Prot.SVC_READ_DATA_GRP);
				break;

			default:
				// switch off PID's supported'
				prt.setService(Kw1281Prot.SVC_NONE);
				// do nothing
		}
	}

	/**
	 * set controller address and re-start communication with new address
	 *
	 * @param newAddress new controller to be accessed
	 */
	void setControllerAddress(int newAddress)
	{
		cbAddress.setSelectedItem(AddressList.get(newAddress));
		if (newAddress != ser.getCurrAddress())
		{
			prt.initialize();
			ser.setCurrAddress(newAddress);
			ser.start();
		}
	}


	/**
	 * The main routine
	 *
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		VagTestFrame frm = new VagTestFrame();
		frm.setVisible(true);
		// command line argument is the com port
		if (args.length > 0)
		{
			try
			{
				/** set up serial port to be used */
				ser.setDeviceName(args[0]);
				/**
				 * only auto-connect to vehicle system if address is
				 * specified (in HEX)
				 */
				if (args.length > 1)
				{
					frm.setControllerAddress(Integer.parseInt(args[1], 16));
				}
			} catch (Exception ex)
			{
				JOptionPane.showMessageDialog(frm,
					args[0],
					ex.toString(),
					JOptionPane.ERROR_MESSAGE);
			}
		} else
		{
			// without parameter we do internal telegram simulation ...
			prt.simulation.start();
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private com.fr3ts0n.pvs.gui.PvTable TblVehIDs;
	private javax.swing.JButton btnConfig;
	private javax.swing.JButton btnConnect;
	private javax.swing.JButton btnLoad;
	private javax.swing.JButton btnSave;
	private javax.swing.JButton btnStop;
	@SuppressWarnings("rawtypes")
	private javax.swing.JComboBox cbAddress;
	@SuppressWarnings("rawtypes")
	private javax.swing.JComboBox cbCnvSystem;
	@SuppressWarnings("rawtypes")
	private javax.swing.JComboBox cbFrameNum;
	private javax.swing.JFileChooser fChoose;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JToolBar.Separator jSeparator1;
	private javax.swing.JToolBar.Separator jSeparator2;
	private javax.swing.JToolBar.Separator jSeparator3;
	private javax.swing.JLabel lblBaud;
	private javax.swing.JLabel lblFooter;
	private javax.swing.JLabel lblMessage;
	private javax.swing.JLabel lblStatus;
	private javax.swing.JLabel lblTitle;
	private javax.swing.JMenuBar mbMain;
	private javax.swing.JMenuItem miAbout;
	private javax.swing.JMenuItem miCommConfigure;
	private javax.swing.JMenuItem miCommInit;
	private javax.swing.JMenuItem miCommStop;
	private javax.swing.JMenuItem miLoad;
	private javax.swing.JMenuItem miSave;
	private javax.swing.JMenu mnuComm;
	private javax.swing.JMenu mnuFile;
	private javax.swing.JMenu mnuHelp;
	private com.fr3ts0n.ecu.gui.application.AboutPanel panAbout;
	private javax.swing.JPanel panFooter;
	private javax.swing.JPanel panHeader;
	private com.fr3ts0n.ecu.gui.application.ObdDataPanel panObdData;
	private com.fr3ts0n.ecu.gui.application.ObdDtcPanel panObdDtc;
	private javax.swing.JPanel panStart;
	private javax.swing.JTabbedPane tabMain;
	private javax.swing.JToolBar tbMain;
	// End of variables declaration//GEN-END:variables

}
