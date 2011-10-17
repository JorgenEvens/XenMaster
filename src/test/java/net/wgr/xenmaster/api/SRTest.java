/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import net.wgr.xenmaster.api.helpers.iSCSI;
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
        SR nsr = new SR(null);
        nsr.setName("M00p");
        nsr.setDescription("Ah ja eh");
        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("location", "/var/vm/meep.img");
        iSCSI i = new iSCSI();
        i.setTarget(InetAddress.getByName("172.16.222.198"));
        i.setType(iSCSI.Type.LVMoISCSI);
        i.getAvailableIQNs(thisHost);
        //String probe = nsr.probe(thisHost, i);
        nsr.create(thisHost, i, "user", true, 0);
        
        Object[] dispatch = (Object[]) Controller.dispatch("SR.get_all");
        SR meep = new SR(dispatch[0].toString());
    }
}
