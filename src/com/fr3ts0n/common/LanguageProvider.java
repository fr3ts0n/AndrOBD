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
 *
 */

package com.fr3ts0n.common;

/**
 * Language provider Interface
 *   To be implemented with system-specific language/translation handling
 * Created by erwin on 27.11.15.
 */
public interface LanguageProvider
{
	/**
	 * Return native language String for specified key
	 *
	 * @param key Key for message String to be translated
	 * @param defaultString default String to be returned, if no translation is available
	 * @return native language String for specified key
	 */
	String getNativeString(String key, String defaultString);
}
