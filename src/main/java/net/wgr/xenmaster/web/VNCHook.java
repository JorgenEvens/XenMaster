/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.wgr.server.web.handling.WebCommandHandler;
import net.wgr.wcp.Commander;
import net.wgr.wcp.Scope;
import net.wgr.wcp.command.Command;
import net.wgr.xenmaster.api.Console;
import net.wgr.xenmaster.api.VM;
import net.wgr.xenmaster.connectivity.ConnectionMultiplexer;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * 
 * @created Dec 15, 2011
 * @author double-u
 */
public class VNCHook extends WebCommandHandler {

    protected static ConnectionMultiplexer cm = new ConnectionMultiplexer();
    protected ConcurrentHashMap<Integer, Client> conversationIds;
    protected HashMap<InetAddress, Client> pendingConnections;
    protected ConnectionMultiplexer.ActivityListener al;

    public VNCHook() {
        super("vnc");

        al = new ConnectionMultiplexer.ActivityListener() {

            @Override
            public void dataReceived(ByteBuffer data, int connection, ConnectionMultiplexer cm) {
                Command cmd = new Command("vnc", "vncData", conversationIds.get(connection).tag, Base64.encodeBase64(data.array()));
                ArrayList<UUID> ids = new ArrayList<>();
                ids.add(conversationIds.get(connection).connId);
                Scope scope = new Scope(ids);
                Commander.getInstance().commandeer(cmd, scope);
            }

            @Override
            public void connectionClosed(int connection) {
                Command cmd = new Command("vnc", "connectionClosed", connection);
                Commander.getInstance().commandeer(cmd, new Scope(Scope.Target.ALL));
                if (conversationIds.containsKey(connection)) {
                    conversationIds.remove(connection);
                }
            }

            @Override
            public void connectionEstablished(int connection, Socket socket) {
                Entry<InetAddress, Client> entry = null;
                for (Iterator<Entry<InetAddress, Client>> it = pendingConnections.entrySet().iterator(); it.hasNext();) {
                    entry = it.next();
                    if (entry.getKey().equals(socket.getInetAddress())) {
                        conversationIds.put(connection, entry.getValue());
                        it.remove();
                    }
                }

                Command cmd = new Command("vnc", "connectionEstablished", entry.getValue().tag, connection);
                ArrayList<UUID> ids = new ArrayList<>();
                ids.add(entry.getValue().connId);
                Scope scope = new Scope(ids);
                Commander.getInstance().commandeer(cmd, scope);
            }
        };

        cm.addActivityListener(al);
        cm.start();
        conversationIds = new ConcurrentHashMap<>();
        pendingConnections = new HashMap<>();
    }

    @Override
    public Object execute(Command cmd) {
        try {
            switch (cmd.getName()) {
                case "openConnection":
                    VM vm = new VM(cmd.getData().getAsString(), false);
                    for (Console c : vm.getConsoles()) {
                        if (c.getProtocol() == Console.Protocol.RFB) {
                            pendingConnections.put(InetAddress.getByName(c.getLocation()), new Client(cmd.getTag(), cmd.getConnection().getId()));
                            cm.addConnection(new InetSocketAddress(c.getLocation(), c.getPort()));
                        }
                    }
                    break;
                case "write":
                    for (Entry<Integer, Client> entry : conversationIds.entrySet()) {
                        if (entry.getValue().connId.equals(cmd.getConnection().getId()) && entry.getValue().tag.equals(cmd.getTag())) {
                            cm.write(entry.getKey(), ByteBuffer.wrap(Base64.decodeBase64(cmd.getData().getAsString())));
                            break;
                        }
                    }
                    break;
                case "closeConnection":
                    for (Entry<Integer, Client> entry : conversationIds.entrySet()) {
                        if (entry.getValue().connId.equals(cmd.getConnection().getId()) && entry.getValue().tag.equals(cmd.getTag())) {
                            cm.close(entry.getKey());
                            break;
                        }
                    }
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass()).error("Command failed : " + cmd.getName(), ex);
        }

        return null;
    }

    protected static class Client {

        public String tag;
        public UUID connId;

        public Client(String tag, UUID conn) {
            this.tag = tag;
            this.connId = conn;
        }
    }
}
