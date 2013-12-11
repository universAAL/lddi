/*
 * File: ConnectionLinker.java 
 * $Id: ConnectionLinker.java 5339 2012-09-25 15:17:28Z fraperod $
 *
 * Copyright (C) 2009 TSB Soluciones.
 * Ronda Auguste y Louis Lumiere 23, Nave 13
 * 46980 Parque Tecnologico de Valencia
 * Paterna, Valencia, Spain
 *
 */
package org.universAAL.lddi.zwave.exporter.Server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Links connection Id with actual connections.
 * 
 * @author fraperod
 * @version $Rev: 5339 $ $Date: 2012-09-25 17:17:28 +0200 (mar, 25 sep 2012) $
 */
public class ConnectionLinker {

    /** Value used to generate. */
    private int                                         currentConnectionId;


    /** Hash table to store the connections. */
    private final Map<Integer, ISocketServerConnection> connectionTable;


    /** Constructor. */
    public ConnectionLinker() {
        currentConnectionId = 0;
        connectionTable = new ConcurrentHashMap<Integer, ISocketServerConnection>(128, 1, Runtime.getRuntime()
            .availableProcessors() << 1);

    }


    /**
     * Adds a new connection.
     * 
     * @param connection connection
     */
    public void addConnection(final ISocketServerConnection connection) {

        connectionTable.put(new Integer(connection.getId()), connection);

    }


    /**
     * Returns the connection with the required id.
     * 
     * @param id id of the connection
     * @return connection, or null if not id not present
     */
    public ISocketServerConnection getConnectionById(final int id) {
        return connectionTable.get(id);
    }


    /**
     * .
     * 
     * @return next free connection id.
     * @throws Exception when no connection ID available.
     */
    public synchronized int getNextConnectionId() throws Exception {

        int connection;
        int lastValue;

        lastValue = currentConnectionId - 1;
        do {
            connection = currentConnectionId++;
        } while ((this.getConnectionById(connection) != null) && (connection != lastValue));


        if (connection == lastValue) {
            throw new Exception("ConnectionLinker, no connection numbers available!!!!");
        }

        return connection;
    }


    /**
     * Removes a connection.
     * 
     * @param connection connection.
     */
    public void removeConnection(final ISocketServerConnection connection) {
        connectionTable.remove(new Integer(connection.getId()));
    }
}
