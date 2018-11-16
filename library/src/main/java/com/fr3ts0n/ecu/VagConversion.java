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

/**
 * VAG data conversions (used by Kw1281 ...)
 *
 * @author Erwin Scheuch-Heilig
 */
public class VagConversion extends NumericConversion
{
	/**
	 *
	 */
	private static final long serialVersionUID = 9130358043909319282L;


	/*
	  * Formulas:
	  *
	  *	 0 :	Tabelle
	  *	10 :	(MW+Offset)*NW*Faktor
	  *	11 :	(MW.NW+Offset)*Faktor
	  *	12 :	(NW.MW+Offset)*Faktor
	  *	13 :	(NW*255+MW+Offset)*Faktor
	  *	14 :	(MW+Offset)/NW*Faktor
	  *	15 :	 MW*Faktor+NW*Offset
	  *	16 :	(MW+Offset)*Faktor
	  *	17 :	 1+(MW+Offset)*NW*Faktor
	  *	18 :	(MW*NW*Faktor)+Offset
	  * 20 :     Bitdarstellung
	  * 21 :     2 Ascii-Zeichen
	  * 22 :     Ascii-Text
	  * 23 :     Uhrzeit
	  * 24 :     Hexdarstellung
	  */
	public static final int CNV_ID_TBL = 0;


	private int cnvId = 10;
	private double factor = 1.0;
	private double offset = 0.0;
	/** table values as they come from the meta package */
	/** Value 2 for calcualtion as it comes from meta package */
	private char metaNw = 0;
	/** table values as they come from the meta package */
	private char[] metaTblValues = {0, 255};

	public VagConversion()
	{
	}

	public VagConversion(int cnvId, double factor, double offset, String units)
	{
		this.cnvId = cnvId;
		this.factor = factor;
		this.offset = offset;
		this.units = units;
	}

	/**
	 * @param metaNw the metaNw to set
	 */
	public void setMetaNw(char metaNw)
	{
		this.metaNw = metaNw;
	}

	/**
	 * @param metaTblValues the metaTblValues to set
	 */
	public void setMetaTblValues(char[] metaTblValues)
	{
		this.metaTblValues = metaTblValues;
	}

	/**
	 * calculation of interpolated table value
	 * table values are transferred with the meta package
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double tableValue(int mw, int nw)
	{
		// mask value to 1 byte
		int internalVal = mw & 0xFF;
		// get position into table
		double stepwidth = (double) 0xFF / (metaTblValues.length - 1);
		int pos = (int) (internalVal / stepwidth);
		int ofs = (int) (internalVal % stepwidth);
		// get both table values
		int valBefore = metaTblValues[pos];
		int valAfter = pos < metaTblValues.length - 1 ? metaTblValues[pos + 1] : valBefore;
		// interpolate value between table entries
		return ((valBefore + (valAfter - valBefore) * ofs / stepwidth) + offset - nw) * factor;
	}

	/**
	 * calculation of formula ID 10
	 * <pre>(MW+Offset)*NW*Faktor</pre>
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double formula10Value(int mw, int nw)
	{
		return ((mw + offset) * nw * factor);
	}

	/**
	 * calculation of formula ID 11
	 * <pre>(MW.NW+Offset)*Faktor</pre>
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double formula11Value(int mw, int nw)
	{
		return ((Double.parseDouble(String.format("%d.%d", mw, nw)) + offset) * factor);
	}

	/**
	 * calculation of formula ID 12
	 * <pre>(NW.MW+Offset)*Faktor</pre>
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double formula12Value(int mw, int nw)
	{
		return ((Double.parseDouble(String.format("%d.%d", nw, mw)) + offset) * factor);
	}

	/**
	 * calculation of formula ID 13
	 * <pre>(NW*255+MW+Offset)*Faktor</pre>
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double formula13Value(int mw, int nw)
	{
		return ((nw * 255 + mw + offset) * factor);
	}

	/**
	 * calculation of formula ID 14
	 * <pre>(MW+Offset)/NW*Faktor</pre>
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double formula14Value(int mw, int nw)
	{
		return ((mw + offset) / nw * factor);
	}

	/**
	 * calculation of formula ID 15
	 * <pre>MW*Faktor+NW*Offset</pre>
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double formula15Value(int mw, int nw)
	{
		return (mw * factor + nw * offset);
	}

	/**
	 * calculation of formula ID 16
	 * <pre>(MW+Offset)*Faktor</pre>
	 *
	 * @param mw value 1 for calculation
	 * @return result of calculation
	 */
	private double formula16Value(int mw)
	{
		return ((mw + offset) * factor);
	}

	/**
	 * calculation of formula ID 17
	 * <pre>1+(MW+Offset)*NW*Faktor</pre>
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double formula17Value(int mw, int nw)
	{
		return (1 + (mw + offset) * nw * factor);
	}

	/**
	 * calculation of formula ID 18
	 * <pre>(MW*NW*Faktor)+Offset</pre>
	 *
	 * @param mw value 1 for calculation
	 * @param nw value 2 for calcualtion
	 * @return result of calculation
	 */
	private double formula18Value(int mw, int nw)
	{
		return (mw * nw * factor + offset);
	}

	/**
	 * convert memory value to physical value
	 *
	 * @param value memory value
	 * @return physical value
	 */
	public Number memToPhys(long value)
	{
		double result = 0;
		int mw = (int) (value % 0x100);
		// if meta value is set, then it will be used
		int nw = metaNw != 0 ? metaNw : (int) (value / 0x100);
		switch (cnvId)
		{
			case 0:
				result = tableValue(mw, nw);
				break;
			case 10:
				result = formula10Value(mw, nw);
				break;
			case 11:
				result = formula11Value(mw, nw);
				break;
			case 12:
				result = formula12Value(mw, nw);
				break;
			case 13:
				result = formula13Value(mw, nw);
				break;
			case 14:
				result = formula14Value(mw, nw);
				break;
			case 15:
				result = formula15Value(mw, nw);
				break;
			case 16:
				result = formula16Value(mw);
				break;
			case 17:
				result = formula17Value(mw, nw);
				break;
			case 18:
				result = formula18Value(mw, nw);
				break;
			case 20:
				result = mw & nw;
				break;
			case 21:
			case 22:
			case 23:
				result = nw << 8 | mw;
				break;

			default:
				log.info(String.format("Unsupported Formula: ID=%d [%s]", cnvId, units));
		}
		return (float) result;
	}

	public Number physToMem(Number value)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String physToPhysFmtString(Number physValue, String format)
	{
		String result;
		switch (cnvId)
		{
			case 20: // * 20 :     Bitdarstellung
				result = Integer.toBinaryString(physValue.intValue());
				break;

			case 21: // * 21 :     2 Ascii-Zeichen
			case 22: // * 22 :     Ascii-Text
				result = String.format("%c%c",
					physValue.intValue() / 0x100,
					physValue.intValue() % 0x100);
				break;

			case 23: // * 23 :     Uhrzeit
				result = String.format("%02d:%02d",
					physValue.intValue() / 0x100,
					physValue.intValue() % 0x100);
				break;

			case 24: // * 24 :     Hexdarstellung
				result = String.format("%04X", physValue.intValue());
				break;

			default:
				result = super.physToPhysFmtString(physValue, format);
		}
		return (result);
	}
}
