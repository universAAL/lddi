package org.universAAL.lddi.zwave.exporter.Server;


/**
 * Abstract class with the basics of a  {@link ISocketSeverProtocolDecoder} .
 * @author  fraperod
 * @version  $Rev: 1246 $ $Date: 2009-03-23 13:28:24 +0100 (lun, 23 mar 2009) $
 */
public abstract class AbstractProtocolDecoder implements ISocketSeverProtocolDecoder {

    /**
     * ProtocolDecoder listener.
     * @uml.property  name="listener"
     * @uml.associationEnd  
     */
    protected ISocketServerProtocolDecoderListener listener;


    /**
     * Constructor. {@link AbstractProtocolDecoder setListener} should be called
     * after construction.
     */
    public AbstractProtocolDecoder() {
        /** EMPTY */
    }


    /**
     * Constructor.
     * 
     * @param listener the listener to notify
     */
    public AbstractProtocolDecoder(final ISocketServerProtocolDecoderListener listener) {
        this.listener = listener;
    }


    /**
     * Sets the listener.
     * @param listener  the listener to notify
     * @uml.property  name="listener"
     */
    public void setListener(final ISocketServerProtocolDecoderListener listener) {
        this.listener = listener;
    }

}
