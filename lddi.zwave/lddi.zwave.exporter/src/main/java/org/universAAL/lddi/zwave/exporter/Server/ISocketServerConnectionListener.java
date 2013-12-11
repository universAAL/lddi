package org.universAAL.lddi.zwave.exporter.Server;


/**
 * Callback interface used to receive events from the ISocketServerConnection.
 * 
 * @author fraperod
 * @version $Rev: 5339 $ $Date: 2012-09-25 17:17:28 +0200 (mar, 25 sep 2012) $
 */
public interface ISocketServerConnectionListener {

    /**
     * Called when a packet is fully reassembled.
     * 
     * @param connection the source of the event.
     * @param pckt the reassembled packet. The type of the object is determined
     *        by the {@link ISocketSeverProtocolDecoder} class used.
     */
    void packetArrived(ISocketServerConnection connection, Object pckt);


    /**
     * Called after a packet has been sent. The packet is sent using
     * {@link ISocketServerConnection writeToConnection}
     * 
     * @param connection The connection which sent the packet
     * @param size The size of the packet sent
     */
    void packetSent(ISocketServerConnection connection, int size);


    /**
     * Called when the read operation reaches the end of stream. This means that
     * the socket was closed.
     * 
     * @param connection the source of the event.
     */
    void socketDisconnected(ISocketServerConnection connection);


    /**
     * Called when a new connection is established, and the handler has been
     * setup.
     * 
     * @param connection the source of the event.
     */
    void socketConnected(ISocketServerConnection connection);


    /**
     * Called when some error occurs while reading or writing to the socket.
     * 
     * @param connection the source of the event.
     * @param ex the exception representing the error.
     */
    void socketException(ISocketServerConnection connection, Exception ex);

}
