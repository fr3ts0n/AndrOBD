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

package com.fr3ts0n.pvs.io;

import com.fr3ts0n.pvs.ProcessVar;
import com.fr3ts0n.pvs.PvChangeEvent;
import com.fr3ts0n.pvs.PvChangeListener;
import com.fr3ts0n.pvs.PvList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Content handler for ProcessVar XML-Streams
 *
 * @author $Author: erwin $
 */
public class PvXMLHandler
	extends DefaultHandler
{

	/** List of XML-Tags handled by this Handler */
	static final String[] XML_TAGS =
		{
			PvXMLWriter.TAG_PVLIST,
			PvXMLWriter.TAG_PROCESSVAR,
			PvXMLWriter.TAG_PVATTRIBUTE
		};
	// numeric indices to above List
	/** ID of TAG for PV-List */
	static final int TAG_ID_PVLIST = 0;
	/** ID for Tag Processvar */
	static final int TAG_ID_PROCESSVAR = 1;
	/** ID for tag Attribute */
	static final int TAG_ID_PVATTRIBUTE = 2;
	/** Stack of recursive process variables */
	Stack<Object[]> pvStack = new Stack<Object[]>();
	/** The root Process variable to be parsed on */
	private com.fr3ts0n.pvs.PvList rootPv = new PvList();
	/** currently parsed process variable */
	private ProcessVar currPv = rootPv;
	/** currently parsed attribute */
	private Object currAttrib;
	/** currently parsed value for attribute */
	private Object currValue;
	/** list of process var change listeners */
	private HashMap<PvChangeListener, Integer> PvChangeListeners = new HashMap<PvChangeListener, Integer>();
	/** the logger object */
	static Logger log = Logger.getLogger(PvXMLHandler.class.getPackage().getName());

	/**
	 * Creates a new instance of PvXMLContentHandler
	 *
	 * @throws org.xml.sax.SAXException Exception thrown at Instantiation of SAX-parser
	 */
	public PvXMLHandler()
		throws SAXException
	{
		rootPv.setKeyValue("root");
	}

	/**
	 * Creates a new instance of PvXMLContentHandler
	 *
	 * @throws org.xml.sax.SAXException Exception thrown at Instantiation of SAX-parser
	 */
	public PvXMLHandler(PvList rootPv)
		throws SAXException
	{
		setRootPv(rootPv);
	}

	/**
	 * get the numeric ID of XML-Tag
	 *
	 * @param tagName Name of XML-Tag
	 * @return numeric ID for XML-Tag or -1 if nott found
	 */
	public int getTagID(String tagName)
	{
		int result = -1;
		for (int i = 0; i < XML_TAGS.length; i++)
		{
			if (XML_TAGS[i].equals(tagName))
			{
				result = i;
				break;
			}
		}
		return (result);
	}

	/**
	 * handle incoming Text
	 *
	 * @param ch     incoming text
	 * @param start  start position
	 * @param length length of text
	 */
	public void characters(char[] ch, int start, int length)
	{
		String currStr = String.copyValueOf(ch, start, length).trim();

		// If this is a valid String, then handle it as the new data
		if (currStr.length() > 0)
		{
			log.fine("Chars:'" + currStr + "'");
			currValue = currStr;
		}
	}

	/**
	 * handle start tag of a nw element
	 *
	 * @param namespaceURI element's namespace
	 * @param localName    element's local name
	 * @param qName        element's qualified name
	 * @param atts         attributes within element tag
	 */
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		if (log.isLoggable(Level.FINE))
		{
			StringBuffer sb = new StringBuffer("StartElement:" + qName + ":");
			for (int i = 0; i < atts.getLength(); i++)
			{
				sb.append(atts.getQName(i) + "=" + atts.getValue(i) + ",");
			}
			log.fine(sb.toString());
		}

		int tagID = getTagID(qName);
		try
		{
			switch (tagID)
			{
				case TAG_ID_PROCESSVAR:
					if (currPv != null)
					{
						pvStack.push(new Object[]{currPv, currAttrib});
					}
					currPv = (ProcessVar) Class.forName(atts.getValue(PvXMLWriter.ATTR_TYPE)).newInstance();
					currPv.setKeyValue(atts.getValue(PvXMLWriter.ATTR_KEY));
					break;

				case TAG_ID_PVATTRIBUTE:
					currAttrib = atts.getValue(PvXMLWriter.ATTR_NAME);
					break;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * handle end tag of element
	 *
	 * @param namespaceURI element's namespace
	 * @param localName    element's local name
	 * @param qName        element's qualified name
	 */
	public void endElement(String namespaceURI, String localName, String qName)
	{
		log.fine("EndElement:" + qName);
		int tagID = getTagID(qName);
		switch (tagID)
		{
			case TAG_ID_PROCESSVAR:
				log.fine("PV:" + currPv.toString());
				firePvChanged(new PvChangeEvent(this, currPv.getKeyValue(), currPv, PvChangeEvent.PV_CONFIRMED));
				currValue = currPv;
				// If we have PV's on stack, the new PV is a recursive attribute
				if (!pvStack.empty())
				{
					Object[] stackElems = (Object[]) pvStack.pop();
					currPv = (ProcessVar) stackElems[0];
					currAttrib = (String) stackElems[1];
				} else
				{
					log.severe("NO more PV's on Stack");
				}
				if (currPv == rootPv)
				{
					currAttrib = ((ProcessVar) currValue).getKeyValue();
				}

				currPv.put(currAttrib, currValue);
				break;

			case TAG_ID_PVATTRIBUTE:
				// if currently parsed PV is null, then we just finished a recursive one
				if (currPv != null)
				{
					// This is a plain attribute
					currPv.put(currAttrib, currValue);
				}
				currValue = null;
				break;
		}
	}

	/**
	 * Handling for list of PvChangeListeners
	 */
	/**
	 * remove listener for Pv changes
	 *
	 * @param l Listener to be removed
	 */
	public synchronized void removePvChangeListener(PvChangeListener l)
	{
		log.fine("-PvListener:" + String.valueOf(this) + "->" + String.valueOf(l));

		PvChangeListeners.remove(l);
	}

	/**
	 * add listener for Pv changes with specified change events
	 *
	 * @param l         Listener to be added
	 * @param eventMask events (Bitmask) which this listener wants to listen on
	 */
	public synchronized void addPvChangeListener(PvChangeListener l, int eventMask)
	{
		Object oldListener = PvChangeListeners.get(l);
		PvChangeListeners.put(l, new Integer(eventMask));

		if (oldListener == null)
		{
			log.fine("+PvListener:" + String.valueOf(this) + "->" + String.valueOf(l));
		}
	}

	/**
	 * add listener for Pv changes
	 *
	 * @param l Listener to be added
	 */
	public void addPvChangeListener(PvChangeListener l)
	{
		addPvChangeListener(l, PvChangeEvent.PV_ALLEVENTS);
	}

	/**
	 * fire a Pv Change event
	 *
	 * @param e Event for Process variable change
	 */
	@SuppressWarnings("rawtypes")
	protected void firePvChanged(PvChangeEvent e)
	{
		log.fine("PvChange:" + e.toString());

		Integer evtMask;
		Map.Entry curr;

		Set entries = PvChangeListeners.entrySet();
		Iterator it = entries.iterator();

		while (it.hasNext())
		{
			curr = (Map.Entry) it.next();

			if (curr.getKey() != null && curr.getKey() != this)
			{
				// check if listener wants to be notified by this event
				evtMask = (Integer) curr.getValue();

				if ((evtMask.intValue() & e.getType()) != 0)
				{
					log.fine("Notify:" + curr);
					((PvChangeListener) curr.getKey()).pvChanged(e);
				}
			}
		}
	}

	public PvList getRootPv()
	{
		return rootPv;
	}

	public void setRootPv(PvList mainPV)
	{
		this.rootPv = mainPV;
		this.currPv = mainPV;
	}

	/**
	 * main routine for testing class implementation
	 *
	 * @param argv command line arguments
	 */
	public static void main(String[] argv)
	{
		try
		{
			// Create a new Parser
			PvXMLHandler handler = new PvXMLHandler();
			// Use the default (non-validating) parser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new FileInputStream(argv[0]), handler);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
