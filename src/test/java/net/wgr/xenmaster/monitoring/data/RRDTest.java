/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.monitoring.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author double-u
 */
public class RRDTest {
    
    public RRDTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    //@Test
    public void testSomeMethod() throws MalformedURLException, IOException {
        RRD rrd = RRD.parse(new FileInputStream("rrd.xml"));
    }
}
