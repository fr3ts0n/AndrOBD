package com.fr3ts0n.pvs.gui;

import com.fr3ts0n.pvs.ProcessVar;

import java.awt.datatransfer.DataFlavor;

class PvDataFlavor extends DataFlavor
{
	private static final String PV_MIME_TYPE = "application/processvar";
	
	public PvDataFlavor()
	{
		super(ProcessVar.class, PV_MIME_TYPE);
	}
	
	public PvDataFlavor(String arg0) throws ClassNotFoundException
	{
		super(arg0);
	}

	public PvDataFlavor(String arg0, String arg1)
	{
		super(arg0, arg1);
	}

	public PvDataFlavor(String arg0, String arg1, ClassLoader arg2)
			throws ClassNotFoundException
	{
		super(arg0, arg1, arg2);
	}

}
