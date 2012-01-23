/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

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
    protected final static String SENSORS_PACKAGE = "net.wgr.xenmaster.monitoring.sensors";

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
