/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.api;

import java.util.List;
import net.wgr.xenmaster.controller.BadAPICallException;
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
public class VIFTest {

    public VIFTest() {
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
        root.setLevel(Level.DEBUG);
        root.addAppender(new ConsoleAppender(new TTCCLayout()));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() throws BadAPICallException {
        List<Network> all = Network.getAll();
    }
}
