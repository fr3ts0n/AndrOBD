package com.fr3ts0n.pvs.gui;

import java.awt.datatransfer.DataFlavor;

import com.fr3ts0n.pvs.ProcessVar;

public class PvDataFlavor extends DataFlavor
{
	static final String PV_MIME_TYPE = "application/processvar";

	public PvDataFlavor()
	{
		this(ProcessVar.class, PV_MIME_TYPE);
	}

	public PvDataFlavor(String arg0) throws ClassNotFoundException
	{
		super(arg0);
	}

	public PvDataFlavor(Class<?> representationClass, String humanPresentableName)
	{
		super(ProcessVar.class, PV_MIME_TYPE);
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
