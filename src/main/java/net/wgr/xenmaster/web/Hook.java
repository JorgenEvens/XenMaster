/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.wgr.server.web.handling.WebCommandHandler;
import net.wgr.wcp.Command;
import net.wgr.wcp.CommandException;
import net.wgr.xenmaster.api.XenApiEntity;
import net.wgr.xenmaster.controller.Controller;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 1, 2011
 * @author double-u
 */
public class Hook extends WebCommandHandler {

    protected ConcurrentHashMap<Integer, Object> store;
    protected Class clazz = null;
    protected Object current = null;
    protected String className = "", commandName;

    public Hook() {
        super("xen");

        store = new ConcurrentHashMap<>();
        Controller.getSession().loginWithPassword("root", "r00tme");
    }

    public Object execute(Command cmd) {
        // Cleanup
        clazz = null;
        current = null;
        className = commandName = "";
        
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(APICall.class, new APICallDecoder());
        Gson gson = new Gson();
        APICall apic = gson.fromJson(cmd.getData(), APICall.class);

        return executeInstruction(cmd.getName(), apic.ref, apic.args);
    }

    protected Object convertToJavaObject(Object value, Class type) throws Exception {
        switch (type.getSimpleName()) {
            case "boolean":
                return Boolean.parseBoolean(value.toString());
            case "int":
                return Integer.parseInt(value.toString());
            default:
                if (InetAddress.class.isAssignableFrom(type)) {
                    return InetAddress.getByName(value.toString());
                } else if (XenApiEntity.class.isAssignableFrom(type)) {
                    Constructor c = type.getConstructor(String.class, boolean.class);
                    return c.newInstance(value.toString(), false);
                }
                break;
        }
        return value;
    }

    protected <T> String createLocalObject(Class<T> clazz, Object[] args) throws Exception {
        T obj = clazz.newInstance();
        if (args.length < 1 || args[0] == null || !(args[0] instanceof Map)) {
            throw new IllegalArgumentException("No or illegal arguments map was given");
        }
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) args[0]).entrySet()) {
            String methodName = "set" + entry.getKey().toLowerCase();
            for (Method m : clazz.getMethods()) {
                if (!m.getName().toLowerCase().equals(methodName)) continue;
                m.invoke(obj, entry.getValue());
            }
        }
        store.put(store.size(), obj);
        return "LocalRef:" + store.size();
    }

    protected void determineClass(String ref, int index, String[] values) throws Exception {
        String s = values[index];
        int refOpen = s.indexOf('[');
        if (refOpen != -1) {
            className = s.substring(0, refOpen);
            clazz = Class.forName("net.wgr.xenmaster.api." + className);
        } else if (index == values.length - 2) {
            className += s;
            clazz = Class.forName("net.wgr.xenmaster.api." + className);
        } else {
            className += s + '.';
        }

        if (refOpen != -1) {
            ref = s.substring(refOpen + 1, s.indexOf(']'));
        }

        // The reference may be an empty string, just not null
        if (ref != null && clazz != null) {
            Constructor c = clazz.getConstructor(String.class, boolean.class);
            current = c.newInstance(ref, !ref.isEmpty());
        }
    }

    protected Object findAndCallMethod(String ref, String s, Object[] args) throws Exception {
        int open = s.indexOf('(');
        String methodName = (open != -1 ? s.substring(0, open) : s);
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
            if (!m.getName().equals(methodName)) {
                continue;
            }

            match = true;

            Class<?>[] types = m.getParameterTypes();
            if ((types != null && types.length != 0) && ((types.length > 0 && args == null) || (types.length != args.length))) {
                Logger.getLogger(getClass()).info("Hook call made with incorrect number of arguments: " + commandName);
                return new CommandException("Illegal number of arguments in " + methodName + " call", commandName);
            } else if (args != null) {
                for (int j = 0; j < types.length; j++) {
                    Class<?> type = types[j];
                    Object value = args[j];

                    if (!(value instanceof String)) {
                        continue;
                    }

                    args[j] = convertToJavaObject(value, clazz);
                }
            }

            try {
                if (Modifier.isStatic(m.getModifiers())) {
                    current = m.invoke(null, (Object[]) args);
                } else {
                    m.setAccessible(true);
                    current = m.invoke(current, (Object[]) args);
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass()).info("Hook call threw Exception", ex);
                return new CommandException(ex, commandName);
            }

            break;
        }

        if (!match) {
            if (methodName.equals("create")) {
                return createLocalObject(clazz, args);
            } else {
                Logger.getLogger(getClass()).warn("Method not found " + s + " in " + commandName);
                return new CommandException("Method " + methodName + " was not found", commandName);
            }
        }

        return null;
    }

    protected Object executeInstruction(String command, String ref, Object[] args) {
        String[] split = StringUtils.split(command, '.');
        commandName = command;

        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            try {
                if (clazz == null) {
                    determineClass(ref, i, split);
                } else {
                    Object result = findAndCallMethod(ref, s, args);
                    if (result != null) return result;
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error("Instruction failed " + s, ex);
                return new CommandException(ex, commandName);
            }
        }

        if (current == null) {
            current = "Success";
        }
        return current;
    }

    public static class APICall {

        public String ref;
        public Object[] args;
    }
}
