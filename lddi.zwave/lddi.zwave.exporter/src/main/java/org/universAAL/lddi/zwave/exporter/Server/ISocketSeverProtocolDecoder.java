package org.universAAL.lddi.zwave.exporter.Server;

/**
 * Interface to be implemented by the classes used to decode the incoming bytes
 * of a connection into packets or frames. When a new frame has been reassembled
 * it is passed using the {@link ISocketServerProtocolDecoderListener
 * packetReceived} method
 * 
 * @author fraperod
 * @version $Rev: 5339 $ $Date: 2012-09-25 17:17:28 +0200 (mar, 25 sep 2012) $
 */
public interface ISocketSeverProtocolDecoder {

    /**
     * Function called when incoming data is received by a connection.
     * 
     * @param bBuffer the new data received
     */
    void decode(byte[] bBuffer);


    /**
     * Sets the listener which will be notified about the new incoming
     * frames/packets.
     * 
     * @param listener the listener to receive the notification of new
     *        frames/packets
     */
    void setListener(ISocketServerProtocolDecoderListener listener);

}
