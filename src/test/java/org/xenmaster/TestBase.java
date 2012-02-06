/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package org.xenmaster;

import java.net.URL;
import net.wgr.settings.Settings;
import org.xenmaster.api.util.CachingFacility;
import org.xenmaster.controller.Controller;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.junit.BeforeClass;

/**
 * 
 * @created Nov 3, 2011
 * @author double-u
 */
public class TestBase {
    @BeforeClass
    public static void setUpClass() throws Exception {
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.INFO);
        root.addAppender(new ConsoleAppender(new TTCCLayout()));
        
        Controller.build(new URL(Settings.getInstance().getString("Xen.URL")));
        Controller.getSession().loginWithPassword("root", "r00tme");
        
        // Make sure tests never execute with JGroups based cache, it will fail
        CachingFacility.instance(false);
    }
}
