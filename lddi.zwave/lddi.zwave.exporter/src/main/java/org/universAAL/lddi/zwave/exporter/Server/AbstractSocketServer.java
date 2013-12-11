package org.universAAL.lddi.zwave.exporter.Server;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



/**
 * Abstract class with the basics of implementing a {@link ISocketServer} .
 * 
 * @author fraperod
 * @version $Rev: 3702 $ $Date: 2011-02-22 10:11:15 +0100 (mar, 22 feb 2011) $
 * @uml.dependency 
 *                 supplier="es.tsbsoluciones.socketServer.ISocketServerProtocolDecoderFactory"
 */

public abstract class AbstractSocketServer implements ISocketServer {

    /**
     * SocketServer default size of the TCP buffers.
     */
    public static int                             DEFAULT_TCP_BUFFERS_SIZE = 5 * 1024;

    /** Server listening port. */
    protected int                                 listenPort;

    /** Sockets buffer input size. */
    protected int                                 tcpBufferSizeIn          = DEFAULT_TCP_BUFFERS_SIZE;
    /** Sockets buffer output size. */
    protected int                                 tcpBufferSizeOut         = DEFAULT_TCP_BUFFERS_SIZE;

    /** Number of worker threads to use for each available CPU. */
    protected int                                 nWorkersCPUmultiplier    = 50;

    /** Total number of worker threads. */
    protected int                                 nWorkersIO               = nWorkersCPUmultiplier
                                                                               * Runtime.getRuntime()
                                                                                   .availableProcessors();

    /**
     * SocketServer workerPool.
     */
    protected ThreadPoolExecutor                  workerPool;

    /**
     * SocketServer protocolDecoderFactory.
     * 
     * @uml.property name="protocolDecoderFactory"
     * @uml.associationEnd
     */
    protected ISocketServerProtocolDecoderFactory protocolDecoderFactory;


    /**
     * Constructor.
     * 
     * @param protocolFactory protocol decoder factory
     * @param listenPort listening port
     */
    public AbstractSocketServer(final ISocketServerProtocolDecoderFactory protocolFactory, final int listenPort) {
        this.listenPort = listenPort;
        this.protocolDecoderFactory = protocolFactory;


//        final java.util.concurrent.ThreadFactory threadFactory = new ThreadFactoryWithPriority(Thread.NORM_PRIORITY,
//            "SocketServer_Worker");
        // workerPool = (ThreadPoolExecutor)
        // Executors.newCachedThreadPool(threadFactory);

        workerPool = new ThreadPoolExecutor(1, 1, 120000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        //workerPool.setThreadFactory(threadFactory);

        workerPool.setCorePoolSize(4);
        workerPool.setMaximumPoolSize(nWorkersIO);
    }


    /**
     * Sets the server listening port.
     * 
     * @see ISocketServer setPort
     * @param port the port number
     */
    public void setPort(final int port) {
        this.listenPort = port;
    }

}
