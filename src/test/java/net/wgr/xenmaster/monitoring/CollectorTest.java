/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.monitoring;

import java.net.UnknownHostException;
import net.wgr.xenmaster.controller.Controller;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author double-u
 */
public class CollectorTest {

    public CollectorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.INFO);
        root.addAppender(new ConsoleAppender(new TTCCLayout()));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of run method, of class Collector.
     */
    @Test
    public void testRun() throws UnknownHostException {
        Collector c = new Collector(Controller.getLocal().getDispatcher().getConnection());
        c.run();
    }
}
