/*
 * APIHook.java
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
package org.xenmaster.api.util;

import java.lang.reflect.Method;
import net.wgr.core.ReflectionUtils;
import net.wgr.wcp.connectivity.Connection;
import org.apache.log4j.Logger;

public class APIHook {

    protected Connection connection;

    public APIHook(Connection connection) {
        this.connection = connection;
    }

    public Object handle(String method, Object[] args) {
        String methodName = method.substring(method.indexOf('.') + 1);
        for (Method m : ReflectionUtils.getAllMethods(getClass())) {
            if (m.getName().equals(methodName) && m.getParameterTypes().length == args.length) {
                Class<?>[] argTypes = m.getParameterTypes();
                Object[] properArgs = new Object[args.length];

                try {
                    for (int i = 0; i < args.length; i++) {
                        properArgs[i] = APIUtil.deserializeToTargetType(args[i], argTypes[i]);
                    }
                    return m.invoke(this, properArgs);
                } catch (Exception ex) {
                    Logger.getLogger(getClass()).error("Failed to do shizzle!", ex);
                }
            }
        }
        
        return null;
    }
}
