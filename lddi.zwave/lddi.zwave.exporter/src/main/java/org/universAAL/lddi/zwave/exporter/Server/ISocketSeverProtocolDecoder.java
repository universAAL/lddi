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
