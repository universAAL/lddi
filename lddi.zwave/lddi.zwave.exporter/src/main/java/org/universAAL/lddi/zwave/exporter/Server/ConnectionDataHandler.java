/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at
     
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
import java.net.InetAddress;
import java.nio.ByteBuffer;

import org.xsocket.connection.ConnectionUtils;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IWriteCompletionHandler;
import org.xsocket.connection.IConnection.FlushMode;

/**
 * Connection wrapper to be used with xSocket library.
 * 
 * @author fraperod
 * @version $Rev: 5339 $ $Date: 2012-09-25 17:17:28 +0200 (mar, 25 sep 2012) $
 */
public class ConnectionDataHandler implements IWriteCompletionHandler, ISocketServerConnection,
    ISocketServerProtocolDecoderListener {

    /** Logger. */


    /** object used for synchronizing threads. */
    private static final Object               LOCK            = new Object();

    /**
     * ConnectionDataHandler listener.
     * 
     * @uml.property name="listener"
     * @uml.associationEnd
     */
    private ISocketServerConnectionListener   listener;
    /**
     * ConnectionDataHandler connection.
     */
    private final INonBlockingConnection      connection;

    private final INonBlockingConnection      connectionSynchronized;
    /**
     * ConnectionDataHandler protocolDecoder.
     * 
     * @uml.property name="protocolDecoder"
     * @uml.associationEnd
     */
    private final ISocketSeverProtocolDecoder protocolDecoder;

    /**
     * ConnectionDataHandler identifier.
     * 
     * @uml.property name="id"
     */
    private int                               id;

    /**
     * Used to assign an unique identifier to the connections.
     */
    private static int                        currentId;


    /**
     * Total number of packets sent by the connection.
     */
    private int                               packetsSent     = 0;

    /**
     * Total number of packets received by the connection.
     */
    private int                               packetsReceived = 0;

    /**
     * Remote address connection.
     */
    private final InetAddress                 remoteAddress;

    /**
     * Remote port number connection.
     */
    private final int                         remotePort;


    /**
     * Constructor.
     * 
     * @param connection xSocket connection that wraps the socket channel
     * @param listener connection listener
     * @param protocolDecoder connection protocol decoder
     */
    public ConnectionDataHandler(final INonBlockingConnection connection,
        final ISocketServerConnectionListener listener, final ISocketSeverProtocolDecoder protocolDecoder) {
        synchronized (LOCK) {
            id = currentId++;
        }
        this.protocolDecoder = protocolDecoder;
        this.protocolDecoder.setListener(this);
        this.listener = listener;
        this.connection = connection;
        this.connection.setFlushmode(FlushMode.ASYNC);

        this.connectionSynchronized = ConnectionUtils.synchronizedConnection(connection);

        this.remoteAddress = connection.getRemoteAddress();
        this.remotePort = connection.getRemotePort();
        // Set asynchronous write mode

    }


    /**
     * Closes the connection.
     */
    public void close() {
        try {
            connectionSynchronized.close();
        } catch (final IOException e) {
            listener.socketException(this, e);
        }
    }


    /**
     * Decodes incoming data. New frames will be notified to the connection
     * Listener.
     * 
     * @param newData incoming new data
     */
    public void decode(final byte[] newData) {
        try {
            protocolDecoder.decode(newData);
        } catch (final Throwable e) {
            System.out.println("Decode error:");
            e.printStackTrace();
        }
    }


    /**
     * Gets the total number of received packets.
     * 
     * @return the total number of received packets
     */
    public int getConnectionReceivedPackets() {
        return packetsReceived;
    }


    /**
     * Gets the total number of sent packets.
     * 
     * @return the total number of sent packets
     */
    public int getConnectionSentPackets() {
        return packetsSent;
    }


    /**
     * Returns the connection identifier.
     * 
     * @return connection identifier
     * @uml.property name="id"
     */
    public int getId() {
        return id;
    }


    /**
     * Returns true if the connections is closed.
     * 
     * @return true if the connection is closed
     */
    public boolean isConnectionClosed() {
        return !connectionSynchronized.isOpen();
    }


    /**
     * Exception raised while trying to write.
     * 
     * @param ioe error while trying to write to the connection.
     */
    public void onException(final IOException ioe) {
        listener.socketException(this, ioe);
    }


    /**
     * Callback method that notifies that a packet has been sent.
     * 
     * @see org.xsocket.connection.IWriteCompletionHandler#onWritten(int)
     * @param written the size of the packet written
     * @throws IOException error handling onWritten event
     */
    public void onWritten(final int written) throws IOException {
        packetsSent++;
        listener.packetSent(this, written);
    }


    /**
     * Method called by the protocol decoder when a new packet has been
     * assembled.
     * 
     * @param pckt the new assembled packet
     */
    public void packetReceived(final Object pckt) {
        packetsReceived++;
        listener.packetArrived(this, pckt);

    }


    /**
     * {@inheritDoc}
     */
    public void setId(final int connectionId) {
        this.id = connectionId;
    }


    /**
     * Writes data to connection. Asynchronous method, retuns imediatly.
     * {@link ConnectionDataHandler onWritten} is called after the packet has
     * been written.
     * 
     * @param data data to be written
     */
    public void writeToConnection(final ByteBuffer data) {

        // if (connectionSynchronized.isOpen()) {
        try {
            connectionSynchronized.write(data, this);

        } catch (final IOException e) {
            e.printStackTrace();
            listener.socketException(this, e);
        }
        // } else {
        // listener.socketException(this, new
        // IOException("Trying to write in a closed connection"));
        // }

    }


    /**
     * {@inheritDoc}
     */
    public InetAddress getRemoteAddress() {
        return this.remoteAddress;
    }


    /**
     * getConnectionSynchronized.
     * 
     * @return .
     */
    public INonBlockingConnection getConnection() {
        return connectionSynchronized;
    }


    /**
     * {@inheritDoc}
     */
    public void errorDecoding(final String errorDecoding) {

        final StringBuilder sb = new StringBuilder();


        sb.append("Notified Decode error in connection ");
        try {
            sb.append(this.connection.getRemoteAddress().toString());
        } catch (final Throwable e) {
            e.printStackTrace();
            sb.append("Error: ");
            sb.append(e.getMessage());
        }
        sb.append(": \r\n");
        sb.append(errorDecoding);

   }


    /**
     * Returns the listener.
     * 
     * @return the listener
     */
    public ISocketServerConnectionListener getListener() {
        return listener;
    }


    /**
     * {@inheritDoc}
     */
    public void setConnectionLister(final ISocketServerConnectionListener listener) {
        this.listener = listener;

    }


    public int getRemotePort() {
        return this.remotePort;
    }


}
