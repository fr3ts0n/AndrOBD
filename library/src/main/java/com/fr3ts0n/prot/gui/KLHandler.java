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

package com.fr3ts0n.prot.gui;


import com.fr3ts0n.prot.ProtUtils;
import com.fr3ts0n.prot.SerialExt;
import com.fr3ts0n.prot.TelegramWriter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Timer;

/**
 * Communication handler for KL-Adaptor
 *
 * @author erwin
 */
public class KLHandler extends SerialHandler
	implements TelegramWriter
{

	/**
	 * Status of package transmission / reception - to determine if we are
	 * processing data, or complements
	 */
	public enum PacketStatus
	{
		ECHO,
		COMPLEMENT,
		DATA,
	}

	// baud rates supported by this adapter
	private final int[] baudRates =
		{
			10400, 9600, 4800, 2400
		};
	// synch char
	private static final char SYNC_CHAR = 0x55;
	// current packet status
	private PacketStatus pktStat = PacketStatus.COMPLEMENT;
	// telegram block counter
	private char blockCounter = 0;
	/* current address in use */
	private int currAddress = 0; // (see ecuadr.csv)
	/** current baud rate */
	private int currBaudRate = 0;
	// last byte sent;
	private char lastTxChar = 0;
	/** Queue of telegrams to be sent */
	private final Vector<char[]> txTelegramQueue = new Vector<char[]>();
	/** current telegram to be sent */
	private char[] currTxTgm = {};
	/** current position of byte to be sent from within currTxTgm */
	private int currTxCharPos = 0;

	/**
	 * Protocol timing parameters
	 */
	/** time of last reception */
	private long lastRxTime = 0;
	/** number of bits to pause (parameter fo calculation of interByteTime) */
	private static final int numBitsPause = 0;
	/** time [nanoseconds] to wait for sending next byte */
	private long interByteTime = 0; // [ns]

	/** time [ms] to detect communication timeout */
	private final int commTimeoutTime = 2000; // [ms]
	/** Timer to detect communication timeout */
	private Timer commTimer;

	/** Handler for communication Timeout */
	private final ActionListener commTimeoutHandler = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			log.warning("CommTimeout");
			close();
			setProtStat(ProtStatus.TIMEOUT);
		}
	};

	/**
	 * Default constructor
	 */
	public KLHandler()
	{
		// set the logger object
		log = Logger.getLogger("com.fr3ts0n.prot.kl");

		commTimer = new Timer(commTimeoutTime, commTimeoutHandler);
		commTimer.setInitialDelay(commTimeoutTime);
		commTimer.stop();
	}

	/**
	 * construct with connecting to device
	 *
	 * @param device device to connect
	 */
	public KLHandler(String device)
	{
		try
		{
			setDeviceName(device);
		} catch (Exception ex)
		{
			log.log(Level.SEVERE,"",ex);
		}
		commTimer = new Timer(commTimeoutTime, commTimeoutHandler);
		commTimer.setInitialDelay(commTimeoutTime);
		commTimer.stop();
	}

	/**
	 * @return the currBaudRate
	 */
	// current baud rate in use
	private int getCurrBaudRate()
	{
		return currBaudRate;
	}

	/**
	 * @param currBaudRate the currBaudRate to set
	 */
	private void setCurrBaudRate(int currBaudRate)
	{
		firePropertyChange(new PropertyChangeEvent(this, "baud", this.currBaudRate, currBaudRate));
		this.currBaudRate = currBaudRate;
	}

	/**
	 * @return the currAddress
	 */
	private int getCurrAddress()
	{
		return currAddress;
	}

	/**
	 * @param currAddress the currAddress to set
	 */
	private void setCurrAddress(int currAddress)
	{
		this.currAddress = currAddress;
	}

	/**
	 * close communication port
	 */
	@Override
	public void close()
	{
		txTelegramQueue.clear();
		if (commTimer != null)
		{
			commTimer.stop();
		}
		super.close();
		// set shutdown status
		setProtStat(ProtStatus.OFFLINE);
	}

	/**
	 * add new listener for communication timeout
	 *
	 * @param listener new timeout listener
	 */
	public void addTimeoutListener(ActionListener listener)
	{
		commTimer.addActionListener(listener);
	}

	/**
	 * set port speed to specified baud rate
	 *
	 * @param newBaudRate new baud rate to be set
	 */
	private void setCustomBaudRate(int newBaudRate)
	{
		try
		{
			SerialExt.setCustomBaudrate(newBaudRate);
		} catch (Exception e)
		{
			log.log(Level.SEVERE,"Set custom baudrate", e);
		}

		// show baudrate setting as debug output
		try
		{
			log.fine(String.format("Baudrate:%d/%d",
				SerialExt.getCustomBaudrate(),
				newBaudRate));
		} catch (Exception e)
		{
			log.log(Level.SEVERE,null, e);
		}
		// calculate new interByteTime (1M ns *numBits / bps)
		interByteTime = 1000000000 * numBitsPause / newBaudRate;
	}

	/**
	 * send a single bit with 5 baud bit width
	 *
	 * @param bitValue
	 */
	private void sendBit5Baud(boolean bitValue)
		throws IOException
	{
		// set line status
		SerialExt.setBreak(bitValue ? 0 : 1);
		// RTS line follows inverse data bit ...
		// device.setRTS(!bitValue);
		// and wait a single bit time
		try
		{
			Thread.sleep(200);
		} catch (InterruptedException e)
		{
			log.log(Level.SEVERE,null, e);
		}
	}

	/**
	 * send a byte with 5 baud line speed
	 * this version sends out timed line break bits w/o changing the nominal
	 * baud rate
	 *
	 * @param address CU to be addressed
	 */
	private void sendByte5Baud(int address)
		throws IOException
	{
		long start = System.currentTimeMillis();
		// ensure stop bit
		// sendBit5Baud(true);
		// start sending
		// device.setDTR(false);
		// start bit
		sendBit5Baud(false);
		// 8 data bits, LSB first
		for (int i = 0; i < 8; i++)
		{
			sendBit5Baud((address & (1 << i)) != 0);
		}
		// stop bit
		sendBit5Baud(true);
		// finish sending
		// device.setDTR(true);
		log.fine(String.format("TX-5Baud:%02X finished after %dms", address, System.currentTimeMillis() - start));
	}

	/**
	 * Initialize communication by sending specified ECU address with 5 baud
	 *
	 * @param address ECU to be addressed
	 * @return initialization status (0=OK, -1=Error)
	 */
	private void send5Baud(int address)
		throws IOException
	{
		int result = 0;
		setProtStat(ProtStatus.CONNECTING);

		sendByte5Baud(address);
		message = "";
		// return status
	}

	/**
	 * send specified char to output device
	 *
	 * @param txByte     byte to be sent
	 * @param rememberTx remember last TX character
	 */
	private void writeChar(char txByte, boolean rememberTx)
	{
		try
		{
			log.fine("TX:" + String.format("%02X", (byte) txByte));

			// if this byte should be remembered as TX byte, do it
			if (rememberTx)
			{
				lastTxChar = txByte;
			}
			wrtr.write((int) txByte);
			pktStat = PacketStatus.ECHO;
		} catch (Exception ex)
		{
			log.log(Level.SEVERE,this.toString(), ex);
		}
	}

	/**
	 * confirm reception of last byte by sending complement of it back
	 *
	 * @param lastByte last received byte
	 */
	private void confirmByte(char lastByte)
	{
		// change to complement status ...
		pktStat = PacketStatus.COMPLEMENT;
		writeChar((char) ~lastByte, true);
	}

	/**
	 * Handler for receiving chars from serial port
	 */
	class RxThread extends Thread
	{
		@Override
		public void run()
		{
			int chr;
			log.fine("Reader Thread started");
			while (rdr != null)
			{
				try
				{
					chr = rdr.read();
					log.fine(String.format("RX:%02X : %c\t%s\t%s", chr, chr < 32 || chr > 127 ? '.' : chr,
						getProtStat(), pktStat));
					switch (getProtStat())
					{
						case RECEIVING:
							// restart comm timeout timer
							commTimer.restart();
							switch (pktStat)
							{
								case ECHO:
									lastTxChar = 0;
									// ignore the echo of sent complement
									pktStat = PacketStatus.DATA;
									break;

								case COMPLEMENT:
									// ignore the echo of sent complement
									pktStat = PacketStatus.DATA;
									break;

								case DATA:
									// remember time of last RX
									lastRxTime = System.currentTimeMillis();
									// add char to current RX message
									message += (char) chr;
									// do we have received a complete package?
									if (chr == 0x03 && message.length() > message.charAt(0))
									{
										//
										// package is complete
										//
										log.fine("RX:" + ProtUtils.hexDumpBuffer(message.toCharArray()));
										// update block counter with the received one
										blockCounter = message.charAt(1);
										// notify protocol handler of the new package
										messageHandler.handleTelegram(message.toCharArray());
										// clear the message
										message = "";
										// and change to send status
										setProtStat(ProtStatus.SENDING);
										sendNextByte();
									} else
									{
										//
										// package is still incomplete
										//

										// and send out confirmation for received byte
										confirmByte((char) chr);
									}
									break;
							}
							break;

						case SENDING:
						{
							switch (pktStat)
							{
								case ECHO:
								case DATA:
									// ignore the echo of sent data
									pktStat = PacketStatus.COMPLEMENT;
									break;

								case COMPLEMENT:
									// restart comm timeout timer
									commTimer.restart();
									if (chr == (~lastTxChar & 0xFF))
									{
										pktStat = PacketStatus.DATA;
										sendNextByte();
									} else
									{
										log.log(Level.SEVERE,String.format("Wrong complement:%02X, expected:%02X", chr, ~lastTxChar));
										// TODO: handle communication error
									}
									break;
							}
						}
						break;

						case CONNECTING:
						{
							// if we receive the echo of the init byte, sending is done
							if (chr == lastTxChar)
							{
								// ignore echo byte
							} else
							{
								if (chr == SYNC_CHAR)
								{
									message = "";
								}
								// otherwise collect sync and key bytes
								message += (char) chr;
								// did we receive all INIT bytes (SYNC_CHAR, KW-lo, KW-hi)?
								if (message.length() > 2 && message.charAt(0) == SYNC_CHAR)
								{
									// notify protocol handler of initialization message to get keyword etc
									// messageHandler.handleTelegram(message.toCharArray());
									message = "";
									// Initialization is finished, change to status RECEIVING
									setProtStat(ProtStatus.RECEIVING);
									// confirm the last received byte
									confirmByte((char) chr);
								}
							}
						}
						default:
							break;
					}
				} catch (Exception ex)
				{
					log.log(Level.SEVERE,ex.toString());
					try
					{
						sleep(1000);
					} catch (InterruptedException ex1)
					{
						log.log(Level.SEVERE,ex1.toString());
					}
				}
			}
			log.warning("Reader Tread finished");
		}
	}
	
	/**
	 * write a telegram to serial port
	 * implementation of SerialHandler
	 */
	@Override
	public int writeTelegram(char[] buffer)
	{
		return writeTelegram(buffer, 0, null);
	}

	/**
	 * write a telegram to serial port
	 * implementation of SerialHandler
	 */
	@Override
	public int writeTelegram(char[] buffer, int type, Object id)
	{
		return enqueueTelegram(buffer);
	}

	/**
	 * start the serial handler
	 * Implementation for compatibility
	 */
	@Override
	public void start()
	{
		// start the receive thread
		new Thread()
		{
			@Override
			public void run()
			{
				init5Baud(getCurrAddress());
			}
		}.start();
		new RxThread().start();
	}

	/**
	 * send next available byte
	 *
	 * @return error status of dequeue and send operation
	 */
	private void sendNextByte()
	{
		int result = 0;
		// wait for slow ECUs
		try
		{
			// calculate remaining time to wait (Sytem time resolution sucks...)
			long tDiff = interByteTime - ((System.currentTimeMillis() - lastRxTime) * 1000000000);
			if (tDiff > 0)
			{
				log.fine("TX: waiting " + tDiff + " ns");
				Thread.sleep(tDiff / 1000000L, (int) (tDiff % 1000000L));
			}
		} catch (InterruptedException ex)
		{
			log.log(Level.SEVERE,"wait: " + ex.toString());
		}

		// if we have to dequeue a new telegram ...
		if (currTxCharPos >= currTxTgm.length)
		{
			// ... then try to dequeue one
			result = dequeueTelegram();
		}
		// if there is something to send, send the next char
		if (result == 0)
		{
			writeChar(currTxTgm[currTxCharPos++], true);
			if (currTxCharPos >= currTxTgm.length)
			{
				setProtStat(ProtStatus.RECEIVING);
			}
		}
	}

	/**
	 * enqueue a telegram to be sent
	 *
	 * @param buffer telegram to be sent
	 * @return error status of enqueue
	 */
	private int enqueueTelegram(char[] buffer)
	{
		int result = 0;

		// if the queue is empty and current message is completely sent
		if (txTelegramQueue.size() == 0 && currTxCharPos >= currTxTgm.length)
		{
			// make buffer the current telegram
			currTxTgm = buffer;
			currTxCharPos = 0;
		} else
		{
			// otherwise put it into queue
			result = txTelegramQueue.add(buffer) ? 0 : -1;
		}

		return (result);
	}

	/**
	 * get a acknowledge telegram for current protocol state
	 *
	 * @return ACK telegram with current block counter
	 */
	private char[] getAckTelegram()
	{
		return new char[]
			{
				0x03, ++blockCounter, 0x09, 0x03
			};
	}

	/**
	 * dequeue next telegram to be sent
	 *
	 * @return error code of dequeue operation
	 */
	private int dequeueTelegram()
	{
		int result;
		// is there any telegram in the queue?
		if (txTelegramQueue.size() > 0)
		{
			// ... then dequeue it
			currTxTgm = txTelegramQueue.remove(0);
			// if necessary, adjust block counter
			if (currTxTgm[1] != (blockCounter + 1))
			{
				log.info(String.format("MsgCounter adjusted:%02x->%02x", (int) currTxTgm[1], (int) blockCounter + 1));
				currTxTgm[1] = ++blockCounter;
			}
			// ... and set byte pointer to start of telegram
			currTxCharPos = 0;
			result = 0;
		} else
		{
			// nothing in the pipe, so just send an acknowledge telegram
			currTxTgm = getAckTelegram();
			currTxCharPos = 0;
			result = 0;
		}
		return (result);
	}

	/**
	 * Initialize communication by sending specified ECU address with 5 baud
	 *
	 * @param address ECU to be addressed
	 * @return baud rate of successful connection, or 0 if no connection could be established
	 */
	public synchronized int init5Baud(int address)
	{
		int result = 0;

		// set current address
		setCurrAddress(address);
		try
		{
			setDeviceName(deviceName);
			// loop through all known baud rates
			for (int i = 0; i < baudRates.length; i++)
			{
				log.info(String.format("Init Device:%s Address:%02x, Speed:%d", deviceName, address, baudRates[i]));
				// set communication baud rate
				setCustomBaudRate(baudRates[i]);
				// remember current baud rate
				setCurrBaudRate(baudRates[i]);
				setProtStat(ProtStatus.CONNECTING);
				// init communication with 5 baud address
				send5Baud(address);
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
					log.log(Level.SEVERE,"Sleep", e);
				}
				// if we received an answer, we are not initializing any more
				if (getProtStat() != ProtStatus.CONNECTING)
				{
					log.info(String.format("Init OK Device:%s Address:%02x, Speed:%d", deviceName, address, baudRates[i]));
					// set return value
					result = getCurrBaudRate();
					// start communication timer
					commTimer.restart();
					break;
				}
			}
			if (result == 0)
			{
				log.warning(String.format("Init Timeout Device:%s Address:%02x", deviceName, address));
				close();
			}
		} catch (Exception ex)
		{
			log.log(Level.SEVERE,deviceName + ": " + ex.toString());
			close();
			setProtStat(ProtStatus.ERROR);
			result = 0;
		}

		// remember current baud rate
		setCurrBaudRate(result);
		return (result);
	}

	/**
	 * The main routine for testing
	 *
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		// set default parameters
		int address = 0x01;
		String device = "/dev/ttyUSB0";
		// update parameters by command line params
		if (args.length > 0)
		{
			device = args[0];
		}
		if (args.length > 1)
		{
			address = Integer.parseInt(args[1], 16);
		}

		KLHandler hdlr = new KLHandler(device);

		hdlr.init5Baud(address);

		// wait until we are shutting down
		while (hdlr.getProtStat() != ProtStatus.CONNECTING
			&& hdlr.getProtStat() != ProtStatus.OFFLINE)
		{
			try
			{
				Thread.sleep(1000);
			} catch (Exception ignored)
			{
			}
		}
		hdlr.close();
	}
}
