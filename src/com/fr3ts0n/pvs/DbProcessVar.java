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

package com.fr3ts0n.pvs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Database backed process variable
 *
 * @author erwin
 */
public class DbProcessVar extends IndexedProcessVar
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1080116313924821619L;
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Vector<String> fields = new Vector();
	/**
	 * Holds value of property dbConnection.
	 */
	private Connection dbConnection;
	/**
	 * Holds value of property tableName.
	 */
	private String tableName;

	/** Creates a new instance of DbProcessVar */
	public DbProcessVar()
	{
	}

	public DbProcessVar(Connection dbConn)
	{
		this();
		setDbConnection(dbConn);
	}

	public DbProcessVar(Connection dbConn, String tableName)
	{
		this();
		setDbConnection(dbConn);
		setTableName(tableName);
	}

	/**
	 * return all available field names
	 */
	public String[] getFields()
	{
		return ((String[]) fields.toArray());
	}

	/**
	 * Getter for property dbConnection.
	 *
	 * @return Value of property dbConnection.
	 */
	public Connection getDbConnection()
	{

		return this.dbConnection;
	}

	/**
	 * Setter for property dbConnection.
	 *
	 * @param dbConnection New value of property dbConnection.
	 */
	public void setDbConnection(Connection dbConnection)
	{

		this.dbConnection = dbConnection;
	}

	/**
	 * Getter for property tableName.
	 *
	 * @return Value of property tableName.
	 */
	public String getTableName()
	{
		return this.tableName;
	}

	/**
	 * Setter for property tableName.
	 *
	 * @param tableName New value of property tableName.
	 */
	public void setTableName(String tableName)
	{

		this.tableName = tableName;
	}

	protected String getSelectSQL()
	{
		String result = "SELECT * from " + tableName;
		return (result);
	}

	protected ResultSet getResultSet(String sqlQuery)
	{
		ResultSet set = null;
		ResultSetMetaData rsMd;
		try
		{
			set = dbConnection.createStatement().executeQuery(sqlQuery);
			rsMd = set.getMetaData();
			for (int i = 0; i < rsMd.getColumnCount(); i++)
			{
				fields.add(i, rsMd.getColumnName(i));
			}
		} catch (Exception e)
		{
			log.log(Level.SEVERE, e.toString(), e);
		}
		return (set);
	}
}
