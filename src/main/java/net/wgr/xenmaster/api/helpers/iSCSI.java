/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api.helpers;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 16, 2011
 * @author double-u
 */
public class iSCSI {
    
    protected InetAddress target;
    protected String targetIQN, localIQN;
    protected String user, password;
    protected int port;
    protected boolean useDiscoveryNumber;
    protected Type type;
    
    public static enum Type {
        iSCSI, LVMoISCSI, EXToISCSI
    }
    
    public Map<String, String> toDeviceConfig() {
        HashMap<String, String> map = new HashMap<>();
        map.put("target", target.getCanonicalHostName());
        map.put("targetIQN", targetIQN);
        map.put("localIQN", localIQN);
        if (user != null && password != null) {
            map.put("chapuser", user);
            map.put("chappassword", password);
        }
        if (port != 0) {
            map.put("port", "" + port);
        }
        map.put("usediscoverynumber", Boolean.toString(useDiscoveryNumber));
        return map;
    }

    public String getLocalIQN() {
        return localIQN;
    }

    public void setLocalIQN(String localIQN) {
        this.localIQN = localIQN;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getTarget() {
        return target;
    }

    public void setTarget(InetAddress target) {
        this.target = target;
    }

    public String getTargetIQN() {
        return targetIQN;
    }

    public void setTargetIQN(String targetIQN) {
        this.targetIQN = targetIQN;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isUseDiscoveryNumber() {
        return useDiscoveryNumber;
    }

    public void setUseDiscoveryNumber(boolean useDiscoveryNumber) {
        this.useDiscoveryNumber = useDiscoveryNumber;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    protected boolean isReachable() {
        if (target != null) {
            try {
                return target.isReachable(500);
            } catch (IOException ex) {
                Logger.getLogger(getClass()).error(ex);
            }
        }
        
        return false;
    }
}
