/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import com.google.gson.Gson;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.wgr.server.web.handling.WebCommandHandler;
import net.wgr.wcp.Command;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 1, 2011
 * @author double-u
 */
public class Hook extends WebCommandHandler {

    public Hook() {
        super("xen");
    }

    public Object execute(Command cmd) {
        Gson gson = new Gson();
        APICall apic = gson.fromJson(cmd.getData(), APICall.class);
        if (apic.args == null) {
            apic.args = new Object[0];
        }
        if (apic.command == null || apic.command.isEmpty()) {
            return null;
        }
        switch (cmd.getName()) {
            case "execute":
                return executeInstruction(apic.command);
        }
        return null;
    }

    protected Object executeInstruction(String command) {
        String[] split = StringUtils.split(command, '.');
        Class clazz = null;
        Object current = null;

        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            try {
                if (i == 0) {
                    int opening = s.indexOf('[');
                    String className = (opening != -1 ? s.substring(0, opening) : s);
                    clazz = Class.forName("net.wgr.xenmaster.api." + className);
                    
                    String ref = null;
                    if (opening != -1) {
                        ref = s.substring(opening + 1, s.indexOf(']'));
                    }

                    if (ref != null) {
                        Constructor c = clazz.getConstructor(String.class, boolean.class);
                        current = c.newInstance(ref, !ref.isEmpty());
                    }
                } else {
                    int open = s.indexOf('(');
                    String methodName = (open != -1 ? s.substring(0, open) : s);
                    Object[] args = null;
                    if (open != -1) {
                        String argstr = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
                        argstr = argstr.replace(", ", ",");
                        args = StringUtils.split(argstr, ',');
                    }
                    boolean match = false;
                    if (current != null) {
                        clazz = current.getClass();
                    }

                    for (Method m : clazz.getDeclaredMethods()) {
                        if (m.getName().equals(methodName)) {
                            match = true;

                            if (Modifier.isStatic(m.getModifiers())) {
                                current = m.invoke(null, (Object[]) args);
                            } else {
                                m.setAccessible(true);
                                current = m.invoke(current, (Object[]) args);
                            }

                            break;
                        }
                    }
                    
                    if (!match) {
                        Logger.getLogger(getClass()).warn("Method not found " + s + " in " + command);
                        return null;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error("Instruction failed " + s, ex);
            }
        }

        return current;
    }

    public static class APICall {

        public Object[] args;
        public String command;
    }
}
