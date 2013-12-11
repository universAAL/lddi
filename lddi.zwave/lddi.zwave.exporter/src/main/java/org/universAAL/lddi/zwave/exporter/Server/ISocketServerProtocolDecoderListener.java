package org.universAAL.lddi.zwave.exporter.Server;


/**
 * Interface to receive notifications from {@link ISocketSeverProtocolDecoder}.
 * 
 * @author fraperod
 * @version $Rev: 3616 $ $Date: 2011-02-03 15:35:16 +0100 (jue, 03 feb 2011) $
 */
public interface ISocketServerProtocolDecoderListener {

    /**
     * Notifies that a new packet has been assembled.
     * 
     * @param pckt the new packet assembled
     */
    void packetReceived(Object pckt);


    /**
     * Error produced while decoding data.
     * 
     * @param errorDecoding .
     */
    void errorDecoding(String errorDecoding);
}
