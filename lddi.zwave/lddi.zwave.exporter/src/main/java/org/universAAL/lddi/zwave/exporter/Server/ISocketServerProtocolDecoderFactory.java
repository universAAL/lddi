package org.universAAL.lddi.zwave.exporter.Server;


/**
 * Factory class for   {@link ISocketSeverProtocolDecoder}  .
 * @author   fraperod
 * @version   $Rev: 1624 $ $Date: 2009-05-11 16:14:20 +0200 (lun, 11 may 2009) $
 * @uml.dependency   supplier="es.tsbsoluciones.socketServer.ISocketSeverProtocolDecoder"
 */
public interface ISocketServerProtocolDecoderFactory {

    /**
     * Returns a new protocol decoder.
     * 
     * @return a new protocol decoder to be used by a connection
     */
    ISocketSeverProtocolDecoder getNewProtocolDecoder();

}
