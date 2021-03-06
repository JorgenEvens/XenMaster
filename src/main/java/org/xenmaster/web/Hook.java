/*
 * Hook.java
 * Copyright (C) 2011,2012 Wannes De Smet
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.xenmaster.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.wgr.core.ReflectionUtils;
import net.wgr.server.web.handling.WebCommandHandler;
import net.wgr.utility.GlobalExecutorService;
import net.wgr.wcp.command.Command;
import net.wgr.wcp.command.CommandException;
import net.wgr.wcp.command.Result;
import net.wgr.wcp.connectivity.Connection;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xenmaster.api.util.APIHook;
import org.xenmaster.api.util.APIUtil;
import org.xenmaster.api.util.CachingFacility;
import org.xenmaster.controller.BadAPICallException;

/**
 *
 * @created Oct 1, 2011
 * @author double-u
 */
public class Hook extends WebCommandHandler {

    protected ConcurrentHashMap<Integer, StoredValue> store;
    protected Class clazz = null;
    protected Object current = null;
    protected String className = "", commandName;
    protected Connection connection;

    public Hook() {
        super("xen");

        store = new ConcurrentHashMap<>();
        GlobalExecutorService.get().scheduleAtFixedRate(new Housekeeper(), 1, 5, TimeUnit.MINUTES);
    }

    @Override
    public Object execute(Command cmd) {
        // Cleanup
        clazz = null;
        current = null;
        className = commandName = "";
        connection = cmd.getConnection();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(APICall.class, new APICallDecoder());
        Gson gson = builder.create();
        APICall apic = gson.fromJson(cmd.getData(), APICall.class);

        return executeInstruction(cmd.getName(), apic.ref, apic.args);
    }

    protected <T> String createLocalObject(Class<T> clazz, Object[] args) throws Exception {
        T obj = clazz.newInstance();

        if (args == null || args.length < 1 || args[0] == null || !(args[0] instanceof Map)) {
            throw new IllegalArgumentException("Illegal arguments map was given");
        }
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) args[0]).entrySet()) {
            String methodName = "set" + entry.getKey().toLowerCase();
            boolean set = false;
            for (Method m : clazz.getMethods()) {
                if (!m.getName().toLowerCase().equals(methodName) || m.getParameterTypes().length != 1) {
                    continue;
                }
                m.invoke(obj, APIUtil.deserializeToTargetType(entry.getValue(), m.getParameterTypes()[0]));
                set = true;
            }

            if (!set) {
                Logger.getLogger(getClass()).debug("Given field was not able to be set: " + entry.getKey());
            }
        }

        return storeLocalObject(obj);
    }

    protected void determineClass(String ref, int index, String[] values) throws Exception {
        String s = values[index];
        int refOpen = s.indexOf('[');
        if (refOpen != -1) {
            className = s.substring(0, refOpen);
            clazz = Class.forName("org.xenmaster.api." + className);
        } else if (index == values.length - 2) {
            className += s;
            clazz = Class.forName("org.xenmaster.api." + className);
        } else {
            className += s + '.';
        }

        if (refOpen != -1) {
            ref = s.substring(refOpen + 1, s.indexOf(']'));
        }

        initClassInstance(ref, clazz);
    }

    protected void initClassInstance(String ref, Class clazz) {
        if (clazz != null && ref == null && APIHook.class.isAssignableFrom(clazz)) {
            try {
                Constructor ctor = clazz.getConstructor(Connection.class);
                current = ctor.newInstance(connection);
                return;
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error("Failed to init APIHook", ex);
            }
        }

        // The reference may be an empty string, just not null
        if (ref != null && clazz != null) {
            if (ref.startsWith("LocalRef:")) {
                Integer localRef = Integer.parseInt(ref.substring(ref.indexOf(":") + 1));
                if (store.get(localRef) == null) {
                    current = new IllegalStateException("LocalRef has expired : " + localRef);
                    return;
                }
                current = store.get(localRef).value;
            } else {
                current = CachingFacility.get(ref, clazz);
            }
        }
    }

    protected Object findAndCallMethod(String ref, String s, Object[] args) throws Exception {
        int open = s.indexOf('(');
        String methodName = (open != -1 ? s.substring(0, open) : s);

        // Caller requested new instance of object
        if (methodName.equals("new")) {
            return current;
        }

        if (open != -1) {
            String argstr = s.substring(s.indexOf('(') + 1, s.indexOf(')'));
            argstr = argstr.replace(", ", ",");
            args = StringUtils.split(argstr, ',');
        }

        if (current != null) {
            clazz = current.getClass();
        }

        ArrayList<Method> matches = new ArrayList<>();

        // First find name matches
        for (Method m : ReflectionUtils.getAllMethods(clazz)) {
            if (m.getName().equals(methodName) && Modifier.isPublic(m.getModifiers())) {
                matches.add(m);
            }
        }

        // Then param count matches
        for (ListIterator<Method> it = matches.listIterator(); it.hasNext();) {
            Method m = it.next();

            if ((m.getParameterTypes().length > 0 && args == null) || (args != null && m.getParameterTypes().length != args.length)) {
                it.remove();
            }
        }

        if (matches.size() > 0 && matches.size() < 2) {
            parseAndExecuteMethod(matches.get(0), args);
        } else if (matches.isEmpty()) {
            if (methodName.equals("build")) {
                return createLocalObject(clazz, args);
            } else {
                Logger.getLogger(getClass()).warn("Method not found " + s + " in " + commandName);
                return new CommandException("Method " + methodName + " was not found", commandName);
            }
        } else {
            // We cannot match based on type information as we use the type information to cast the parameters
            return new CommandException("The function call was ambiguous with " + matches.size() + " matched methods", commandName);
        }

        return null;
    }

    protected void parseAndExecuteMethod(Method m, Object[] args) throws Exception {
        Class<?>[] types = m.getParameterTypes();

        // Check method signature
        if ((types != null && types.length != 0) && ((types.length > 0 && args == null) || (types.length != args.length))) {
            Logger.getLogger(getClass()).info("Hook call made with incorrect number of arguments: " + commandName);
            current = new CommandException("Illegal number of arguments in " + m.getName() + " call", commandName);
        } else if (args != null) {
            for (int j = 0; j < types.length; j++) {
                Class<?> type = types[j];
                Object value = args[j];

                if (value == null) {
                    throw new IllegalArgumentException("An argument for  " + clazz.getSimpleName() + '.' + m.getName() + " was null");
                } else if (value instanceof String) {
                    String str = value.toString();

                    if (str.startsWith("LocalRef:")) {
                        Integer localRef = Integer.parseInt(str.substring(str.indexOf(":") + 1));
                        if (!store.containsKey(localRef)) {
                            current = new CommandException("Local object reference does not exist", commandName);
                        }
                        args[j] = store.get(localRef).value;
                    } else {
                        args[j] = APIUtil.deserializeToTargetType(value, type);
                    }
                } else {
                    args[j] = APIUtil.deserializeToTargetType(value, type);
                }
            }
        }

        try {
            if (Modifier.isStatic(m.getModifiers())) {
                current = m.invoke(null, args);
            } else {
                if (current == null) {
                    throw new IllegalArgumentException("Instance method called as a static method.");
                }
                m.setAccessible(true);
                current = m.invoke(current, args);
            }
        } catch (InvocationTargetException ex) {
            // If it has a cause, it will be parsed by the next handler
            if (ex.getCause() == null) {
                Logger.getLogger(getClass()).info("Failed to invoke method", ex);
                current = new CommandException(ex, commandName);
            } else {
                Logger.getLogger(getClass()).info("Hook call threw Exception", ex.getCause());
                if (ex.getCause() instanceof BadAPICallException) {
                    current = new DetailedCommandException(commandName, ((BadAPICallException) ex.getCause()));
                } else {
                    current = new CommandException(ex.getCause(), commandName);
                }
            }
        }

    }

    protected Object executeInstruction(String command, String ref, Object[] args) {
        String[] split = StringUtils.split(command, '.');
        commandName = command;

        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            try {
                if (clazz == null) {
                    determineClass(ref, i, split);
                } else if (APIHook.class.isAssignableFrom(clazz) && ref == null) {
                    // API hooks are responsable for their own handling
                    return ((APIHook) current).handle(command, args, this);
                } else {
                    Object result = findAndCallMethod(ref, s, args);
                    if (result != null) {
                        return result;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error("Instruction failed " + s, ex);
                return new CommandException(ex, commandName);
            }
        }
        if (current == null) {
            current = new Result(null, null, "EMPTY");
        }
        return current;
    }

    public String storeLocalObject(Object obj) {
        int ref = store.size();
        store.put(ref, new StoredValue(obj));
        return "LocalRef:" + ref;
    }

    public String getReferenceForLocalObject(Object obj) {
        for (Map.Entry<Integer, StoredValue> entry : store.entrySet()) {
            if (entry.getValue().value.equals(obj)) {
                return "LocalRef:" + entry.getKey();
            }
        }

        return null;
    }

    public static class APICall {

        public String ref;
        public Object[] args;
    }

    public static class StoredValue {

        public long lastAccess = System.currentTimeMillis();
        public Object value;

        public StoredValue(Object value) {
            this.value = value;
        }
    }

    protected class Housekeeper implements Runnable {

        @Override
        public void run() {
            for (Iterator<Map.Entry<Integer, StoredValue>> it = store.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Integer, StoredValue> entry = it.next();
                Logger.getLogger(getClass()).debug("Deleting stale object with index LocalRef:" + entry.getKey());
                if (System.currentTimeMillis() - entry.getValue().lastAccess > 60000) {
                    it.remove();
                }
            }
        }
    }
}
