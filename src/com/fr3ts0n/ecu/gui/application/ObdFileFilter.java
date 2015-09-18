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

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File filter for saving/loading OBD files
 *
 * @author erwin
 */
public class ObdFileFilter extends FileFilter
{

	static final String[] FLT_EXTENSIONS =
		{
			"obd",
		};
	static final String FLT_DESCRIPTION = "OBD Files";

	/** Creates a new instance of ObdFileFilter */
	public ObdFileFilter()
	{
	}

	/**
	 * Return the extension portion of the file's name .
	 *
	 * @param f file to get extension for
	 * @return file extension
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	public String getExtension(File f)
	{
		if (f != null)
		{
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1)
			{
				return filename.substring(i + 1);
			}
		}
		return null;
	}

	/**
	 * Whether the given file is accepted by this filter.
	 */
	public boolean accept(java.io.File f)
	{
		boolean result = f.isDirectory();
		String ext = getExtension(f);
		for (int i = 0; !result && i < FLT_EXTENSIONS.length; i++)
			result |= FLT_EXTENSIONS[i].equalsIgnoreCase(ext);
		return (result);
	}

	/**
	 * The description of this filter. For example: "JPG and GIF Images"
	 *
	 * @return description of filter (to be used within file chooser ...)
	 */
	public String getDescription()
	{
		return (FLT_DESCRIPTION);
	}

}
