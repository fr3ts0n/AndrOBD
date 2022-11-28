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

package com.fr3ts0n.ecu;

import com.fr3ts0n.pvs.IndexedProcessVar;

/**
 * ECU fault code
 *
 * @author erwin
 */
public class EcuCodeItem extends IndexedProcessVar
  implements Comparable<EcuCodeItem>
{
  /** unique class id*/
  public static final long  serialVersionUID = -1;
  // Field IDs
  public static final int FID_CODE     = 0;
  public static final int FID_DESCRIPT = 1;
  public static final int FID_STATUS   = 2;

  public static final String[] FIELDS =
  {
    "CODE",
    "DESCRIPTION",
    "STATUS",
  };

  EcuCodeItem()
  {
  }

 /** Creates a new instance of EcuCodeItem
   * @param code String representation of DFC
   * @param description descriptive text of DFC
   */
  public EcuCodeItem(String code, String description)
  {
    put(FID_CODE, code);
    put(FID_DESCRIPT, description);
  }

 /** Creates a new instance of ObdCodeItem
   * @param numericCode numeric code ID
   * @param description descriptive text of DFC
   */
  public EcuCodeItem(int numericCode, String description)
  {
    put(FID_CODE, String.valueOf(numericCode));
    put(FID_DESCRIPT, description);
  }

  public String[] getFields()
  {
    return ObdCodeItem.FIELDS;
  }

  /**
   * Return String representation of Code item
   * @return String representation of code
   */
  @Override
  public String toString()
  {
    return String.format("%02X.%s", get(FID_STATUS), get(FID_CODE));
  }

  /**
   * Implementation of Comparable to allow sorting
   * @param o the other object
   * @return result of comparison
   */
  public int compareTo(EcuCodeItem o)
  {
    return toString().compareTo(o.toString());
  }

}
