package com.fr3ts0n.ecu.prot.obd;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
	private static final String BUNDLE_NAME = "com.fr3ts0n.ecu.prot.obd.res.messages"; //$NON-NLS-1$
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private Messages()
	{
		this(BUNDLE_NAME);
	}

	private Messages(String bundleName)
	{
		RESOURCE_BUNDLE = ResourceBundle.getBundle(bundleName);
	}

	public static String getString(String key, String defaultString)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e)
		{
			return defaultString;
		}
	}
	
	public static String getString(String key)
	{
		return getString(key, '!' + key + '!');
	}
}
