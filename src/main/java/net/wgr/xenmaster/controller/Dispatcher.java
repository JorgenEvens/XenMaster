/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.connectivity.Connections;
import org.apache.commons.collections.CollectionUtils;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Dispatcher {

    protected Connections conn;

    public Dispatcher(URL xen) {
        this.conn = new Connections(xen);
    }

    public Dispatcher(Connections conn) {
        this.conn = conn;
    }

    public Object dispatch(String methodName, Object[] params) throws BadAPICallException {
        return dispatch(methodName, params, 0);
    }

    public Object dispatch(String methodName, Object[] params, int connection) throws BadAPICallException {
        ArrayList list = new ArrayList();
        CollectionUtils.addAll(list, params);

        return execute(methodName, list, connection);
    }

    protected Object execute(String methodName, List params, int connection) throws BadAPICallException {
        // Preflight check
        for (Object o : params) {
            if (o == null) {
                throw new BadAPICallException(methodName, params, "Illegal argument", "A null argument has been passed");
            }
        }

        Map result = this.conn.executeCommand(methodName, params, connection);
        if (result == null) {
            throw new BadAPICallException(methodName, params);
        }

        switch (result.get("Status").toString()) {
            case "Success":
                return result.get("Value");
            case "Failure":
                Object[] info = (Object[]) result.get("ErrorDescription");
                ArrayList<String> errInfo = new ArrayList<>();
                for (Object o : info) {
                    if (o instanceof String) {
                        errInfo.add((String) o);
                    }
                }

                throw new BadAPICallException(methodName, params, errInfo.get(0), errInfo);
            default:
                return null;
        }
    }

    public Object dispatchWithSession(String methodName, Object[] params) throws BadAPICallException {
        return dispatchWithSession(methodName, params, 0);
    }

    public Object dispatchWithSession(String methodName, Object[] params, int connection) throws BadAPICallException {
        if (conn.getSession().getReference() == null) {
            throw new Error("Session has not been initialized");
        }
        ArrayList list = new ArrayList();
        list.add(conn.getSession().getReference());
        CollectionUtils.addAll(list, params);

        return execute(methodName, list, connection);
    }

    public Connections getConnections() {
        return conn;
    }
}
