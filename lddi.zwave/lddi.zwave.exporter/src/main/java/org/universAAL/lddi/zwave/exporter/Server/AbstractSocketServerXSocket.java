/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

    See the NOTICE file distributed with this work for additional
    information regarding copyright ownership

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package org.universAAL.lddi.zwave.exporter.Server;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.net.ssl.SSLContext;

import org.xsocket.connection.ConnectionUtils;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IConnectionTimeoutHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.IIdleTimeoutHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;

/**
 * Socket server implemented using xSocket library.
 *
 * @author fraperod
 * @version $Rev: 4442 $ $Date: 2011-11-23 16:22:03 +0100 (mié, 23 nov 2011) $
 */
public abstract class AbstractSocketServerXSocket extends AbstractSocketServer
		implements IConnectHandler, IDisconnectHandler, IDataHandler, IIdleTimeoutHandler, IConnectionTimeoutHandler,
		ISocketServerConnectionListener {

	/**
	 * Calculates transfer rates.
	 *
	 * @uml.property name="transferRate"
	 * @uml.associationEnd
	 */
	protected TransferRateCalculator transferRate = new TransferRateCalculator();

	/**
	 * True to allow xSocket connection to log.
	 */
	protected static boolean activateConnectionLogging = false;

	// TODO: CHECK THIS FOR JMX
	// ////////////////
	/*
	 * 29. JMX support The ConnectionUtils class supports methods to register
	 * the related MBeans of IServer or IConnectionPool implementation on the
	 * MbeansServer // JMX support for a Server IServer srv = new Server(8090,
	 * new Handler()); // registers the server's mbeans on the platform
	 * MBeanServer ConnectionUtils.registerMBean(srv); srv.start(); // JMX
	 * support for a ConnectionPool BlockingConnectionPool pool = new
	 * BlockingConnectionPool(); // register the pool mbeans on the platform
	 * MBeanServer ConnectionUtils.registerMBean(pool); By registering the
	 * server, xSocket creates also an MBean for the assigned handler. By doin
	 * this, all getter and setter methods of the handler will be exported,
	 * which are not private. After registering the artefacts tools like
	 * JConsole can be used to monitor
	 */
	// ////////////////
	/**
	 * xSocket server.
	 */
	protected IServer srv;

	/**
	 * Sets the xSocket connection log.
	 *
	 * @param activateConnectionLogging
	 *            true to allow xSocket connection to log
	 * @uml.property name="activateConnectionLogging"
	 */
	public static void setActivateConnectionLogging(final boolean activateConnectionLogging) {
		AbstractSocketServerXSocket.activateConnectionLogging = activateConnectionLogging;
	}

	/**
	 * Constructor. No Idle timeout, nor connection timeout used.
	 *
	 * @param protocolFactory
	 *            protocol decoder factory to be used by the server
	 * @param listenPort
	 *            port to be used by the server to accept incoming connections
	 */
	public AbstractSocketServerXSocket(final ISocketServerProtocolDecoderFactory protocolFactory,
			final int listenPort) {
		this(protocolFactory, listenPort, null, 0, 0);
	}

	/**
	 * Constructor.
	 *
	 * @param protocolFactory
	 *            protocol decoder factory to be used by the server.
	 * @param listenPort
	 *            port to be used by the server to accept incoming connections.
	 * @param sslContext
	 *            SSLContext.
	 * @param idleTimeout
	 *            milliseconds to receive some data, connection will be closed
	 *            if no data is received. 0 to disable timeout.
	 * @param connectionTimeout
	 *            milliseconds to close the connection after being established.
	 *            0 to disable timeout.
	 */

	public AbstractSocketServerXSocket(final ISocketServerProtocolDecoderFactory protocolFactory, final int listenPort,
			final SSLContext sslContext, final int idleTimeout, final int connectionTimeout) {
		super(protocolFactory, listenPort);

		if (activateConnectionLogging) {
			// activate xSocket logging (for namespace org.xsocket.connection)
			final Logger logger = Logger.getLogger("org.xsocket.connection");
			logger.setLevel(Level.FINE);

			final ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.FINE);
			logger.addHandler(ch);
		} else {
			final Logger logger = Logger.getLogger("org.xsocket.connection");
			logger.setLevel(Level.OFF);

			final ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.OFF);
			logger.addHandler(ch);
		}

		try {

			/*
			 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! using
			 * direct buffers in windows results in a memory leak
			 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 */
			// DO NOT UNCOMMENT:
			// System.setProperty("org.xsocket.connection.server.readbuffer.usedirect","true");
			if (sslContext == null) {
				srv = new Server(listenPort, this);
			} else {
				srv = new Server(listenPort, this, sslContext, true);
			}

			if (idleTimeout > 0) {
				// Idle timeOut event
				srv.setIdleTimeoutMillis(idleTimeout);
			}

			if (connectionTimeout > 0) {
				/*
				 * will close connection after some time
				 */
				srv.setConnectionTimeoutMillis(connectionTimeout);
			}

			srv.setWorkerpool(workerPool);
			// ... or start it by using a dedicated thread

			// registers the server's mbeans on the platform MBeanServer
			try {
				ConnectionUtils.registerMBean(srv);
			} catch (final JMException e) {
				e.printStackTrace();
			}

		} catch (final IOException e) {

		}
	}

	/**
	 * Closes the server.
	 *
	 * @throws IOException
	 *             error closing the socket server.
	 */
	public void close() throws IOException {
		if (srv != null) {
			srv.close();
		}

	}

	/**
	 * Returns the xSocket server class.
	 *
	 * @return the xSocket server class
	 */
	public IServer getServerXSocket() {
		return srv;
	}

	/**
	 * Returns the total number of bytes read.
	 *
	 * @return the total number of bytes read
	 */
	public long getTotalReadBytes() {
		return transferRate.getTotalIncomingBytes();
	}

	/**
	 * Retuns the total number of bytes sent.
	 *
	 * @return the total number of bytes sent
	 */
	public long getTotalSentBytes() {
		return transferRate.getTotalOutgoingBytes();
	}

	/**
	 * Incoming data transfer rate in bytes/second.
	 *
	 * @return the incoming data transfer rate in bytes/second
	 */
	public float getTransferRateIn() {
		return transferRate.getIncomingByteRate();
	}

	/**
	 * Outgoing data transfer rate in bytes/second.
	 *
	 * @return the Outgoing data transfer rate in bytes/second
	 */
	public float getTransferRateOut() {
		return transferRate.getOutgoingByteRate();
	}

	// ///////////////////////////////////////////////////////////////////////////////
	/**
	 * Socket connected.
	 *
	 * @param connection
	 *            new xSocket connection opened
	 * @throws IOException
	 *             error handling new connection
	 * @return true if the event was handled
	 */
	public boolean onConnect(final INonBlockingConnection connection) throws IOException {

		final ConnectionDataHandler socketConnection = new ConnectionDataHandler(connection, this,
				protocolDecoderFactory.getNewProtocolDecoder());

		connection.setAttachment(socketConnection);

		socketConnected(socketConnection);

		return true;
	}

	/**
	 * Method called when a connection timeout is reached.
	 *
	 * @param connection
	 *            the connection
	 * @return true if the event was handled
	 * @throws IOException
	 *             error handling connection timeout
	 */
	public boolean onConnectionTimeout(final INonBlockingConnection connection) throws IOException {

		final ConnectionDataHandler handler = (ConnectionDataHandler) connection.getAttachment();
		String connectionInfo;
		if (handler != null) {
			connectionInfo = " Connection Id: " + handler.getId() + " Pckts sent: " + handler.getConnectionSentPackets()
					+ " Packts received: " + handler.getConnectionReceivedPackets();
		} else {
			connectionInfo = " ";
		}

		// Time to close the connection
		connection.close();
		return true;
	}

	/**
	 * New data available on xSocket connection.
	 *
	 * @param connection
	 *            the connection
	 * @throws IOException
	 *             error while processing incoming data.
	 * @return true if the event was handled
	 */
	public boolean onData(final INonBlockingConnection connection) throws IOException {

		final ConnectionDataHandler handler = (ConnectionDataHandler) connection.getAttachment();

		final int length = connection.available();
		if (length > 0) {
			transferRate.newIncomingData(length);
			final byte[] readBuffer = connection.readBytesByLength(length);
			handler.decode(readBuffer);
		}

		return true;
	}

	/**
	 * Disconnect event.
	 *
	 * @param connection
	 *            the disconnected connection
	 * @throws IOException
	 *             error handling disconnect event
	 * @return true if the event was handled
	 */
	public boolean onDisconnect(final INonBlockingConnection connection) throws IOException {

		this.socketDisconnected((ISocketServerConnection) connection.getAttachment());
		return false;
	}

	/**
	 * Idle time out event.
	 *
	 * @param connection
	 *            the idle connection
	 * @throws IOException
	 *             error handling idle timeout
	 * @return true if the event has handled
	 */
	public boolean onIdleTimeout(final INonBlockingConnection connection) throws IOException {
		final ConnectionDataHandler handler = (ConnectionDataHandler) connection.getAttachment();
		String connectionInfo;
		if (handler != null) {
			connectionInfo = " Connection Id: " + handler.getId() + " Pckts sent: " + handler.getConnectionSentPackets()
					+ " Packts received: " + handler.getConnectionReceivedPackets();
		} else {
			connectionInfo = " ";
		}

		connection.close();

		return false;
	}

	/**
	 * Data sent notification.
	 *
	 * @param connection
	 *            connection
	 * @param size
	 *            the size of the data sent
	 */
	public void packetSent(final ISocketServerConnection connection, final int size) {
		transferRate.newOutgoingData(size);
	}

	/**
	 * Stars the server in the current thread.
	 *
	 * @see es.tsbsoluciones.socketServer.ISocketServer#run()
	 * @throws IOException
	 *             error starting the server
	 */
	public void run() throws IOException {
		// uses this thread, will not return
		srv.run();

	}

	/**
	 * Sets the IP to bind the port.
	 *
	 * @param ip
	 *            the IP to be used to bind the port
	 */
	public void setIP(final String ip) {
		// TODO not implemented yet

	}

	/**
	 * Starts the server in a different thread.
	 *
	 * @throws IOException
	 *             error starting the server
	 */
	public void start() throws IOException {
		// returns after the server has been started
		srv.start();
	}

}
