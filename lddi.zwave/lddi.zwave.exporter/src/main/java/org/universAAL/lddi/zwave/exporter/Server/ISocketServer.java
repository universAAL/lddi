package org.universAAL.lddi.zwave.exporter.Server;

import java.io.Closeable;
import java.io.IOException;

/**
 * Socket server interface. All sockets servers will implement it.
 * 
 * @author fraperod
 * @version $Rev: 5339 $ $Date: 2012-09-25 17:17:28 +0200 (mar, 25 sep 2012) $
 */
public interface ISocketServer extends Closeable {


    /**
     * Gracefully close server.
     * 
     * @throws IOException exception raised while closing the server
     */
    void close() throws IOException;


    /**
     * Returns the total number of bytes read.
     * 
     * @return the total number of bytes read
     */
    long getTotalReadBytes();


    /**
     * Retuns the total number of bytes sent.
     * 
     * @return the total number of bytes sent
     */
    long getTotalSentBytes();


    /**
     * Incoming data transfer rate in bytes/second.
     * 
     * @return the incoming data transfer rate in bytes/second
     */
    float getTransferRateIn();


    /**
     * Outgoing data transfer rate in bytes/second.
     * 
     * @return the Outgoing data transfer rate in bytes/second
     */
    float getTransferRateOut();


    /**
     * Starts server in current thread.
     * 
     * @throws IOException exception raised while starting the server on a
     *         separated thread
     */
    void run() throws IOException;


    /**
     * Sets the IP to be used by the server to bind the port for incoming
     * connections.
     * 
     * @param ip server IP in string format: "xxx.xxxx.xxx.xxx"
     */
    void setIP(String ip);


    /**
     * Sets the port to be used to listen for connections.
     * 
     * @param port usded to listen for incoming connections
     */
    void setPort(int port);


    /**
     * Starts server in independent thread.
     * 
     * @throws IOException exception raised while starting the server in
     *         independent thread
     */
    void start() throws IOException;


}
