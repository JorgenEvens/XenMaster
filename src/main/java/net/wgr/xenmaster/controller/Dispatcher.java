/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.controller;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.connectivity.Connection;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Dispatcher {

    protected Connection conn;

    public Dispatcher() {
        try {
            this.conn = new Connection();
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass()).error(ex);
        }
    }

    public Object dispatch(String methodName, Object[] params) throws BadAPICallException {
        ArrayList list = new ArrayList();
        CollectionUtils.addAll(list, params);
        return execute(methodName, list);

    }

    protected Object execute(String methodName, List params) throws BadAPICallException {
        Map result = this.conn.executeCommand(methodName, params);
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
                    if (o instanceof String) errInfo.add((String) o);
                }

                throw new BadAPICallException(methodName, params, errInfo.get(0), errInfo);
            default:
                return null;
        }
    }

    public Object dispatchWithSession(String methodName, Object[] params) throws BadAPICallException {
        ArrayList list = new ArrayList();
        list.add(conn.getSession().getReference());
        CollectionUtils.addAll(list, params);

        return execute(methodName, list);
    }

    public Connection getConnection() {
        return conn;
    }
}
