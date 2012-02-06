/*
 * Comptroller.java
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
package org.xenmaster.monitoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import net.wgr.core.ReflectionUtils;

import org.apache.log4j.Logger;

/**
 * 
 * @created Jan 17, 2012
 * @author double-u
 */
public class Comptroller {

    protected ScheduledThreadPoolExecutor exec;
    protected List<Sensor> sensors;
    protected final static String SENSORS_PACKAGE = "org.xenmaster.monitoring.sensors";

    public Comptroller() {
        ThreadFactory tf = new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "Sensor thread");
                return t;
            }
        };
        
        this.exec = new ScheduledThreadPoolExecutor(1, tf);
        this.sensors = new ArrayList<>();
    }
    
    public void stop() {
        this.exec.shutdownNow();
    }

    protected void scheduleSensors() {
        try {
            List<Class> classes = ReflectionUtils.getClasses(SENSORS_PACKAGE, getClass());
            for (Class clazz : classes) {
                if (Sensor.class.isAssignableFrom(clazz)) {
                    try {
                        Sensor s = (Sensor) clazz.newInstance();
                        exec.scheduleAtFixedRate(s, 0, s.getDefaultSchedule().toStandardDuration().getMillis(), TimeUnit.MILLISECONDS);
                        sensors.add(s);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(getClass()).error("Failed to instantiate sensor", ex);
                    }
                }
            }
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(getClass()).error("Failed to get sensors from package", ex);
        }
    }
}
