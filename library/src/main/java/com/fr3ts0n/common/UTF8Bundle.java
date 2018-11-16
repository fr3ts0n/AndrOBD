package com.fr3ts0n.common;

import java.util.ResourceBundle;

/**
 * Wrapper class to ensure UTF8 encoding for resource bundle reading
 *
 * @author fr3ts0n
 */

public class UTF8Bundle
{
    private static ResourceBundle.Control ctrl = null;

    public UTF8Bundle(ResourceBundle.Control control)
    {
        ctrl = control;
    }

    public static ResourceBundle getBundle(String bundleName)
    {
        if (ctrl != null)
        {
            return ResourceBundle.getBundle(bundleName, ctrl);
        }
        else
        {
            return ResourceBundle.getBundle(bundleName);
        }
    }
}
