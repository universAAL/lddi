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
