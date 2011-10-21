/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.api;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import net.wgr.xenmaster.controller.BadAPICallException;
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
public class SRTest {
    
    public SRTest() {
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

    //@Test
    public void testSomeMethod() throws BadAPICallException, UnknownHostException, IOException {
        Host thisHost = Controller.getSession().getThisHost();
        List<SR> all = SR.getAll();
        //all.get(0).destroy();
        //all.get(0).setAsDefault(Pool.getAll().get(0));
        
        HashMap<String, String> data = new HashMap<>();
        data.put("location", "/var/vm/test");
        
        SR ns = new SR(null);
        ns.setName("File-based SR");
        ns.setDescription("Test");
        ns.create(Controller.getSession().getThisHost(), data, SR.Type.File, "user", true, 0);
        ns.setAsDefault(Pool.getAll().get(0));
    }
}
