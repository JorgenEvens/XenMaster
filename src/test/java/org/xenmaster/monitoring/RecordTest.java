/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xenmaster.monitoring;

import org.xenmaster.monitoring.data.Record;
import java.util.List;
import org.xenmaster.api.entity.GuestMetrics;
import org.xenmaster.api.entity.VM;
import org.xenmaster.api.entity.VMMetrics;
import org.xenmaster.controller.BadAPICallException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.Logger;
import org.xenmaster.controller.Controller;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author double-u
 */
public class RecordTest {

    public RecordTest() {
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

    //@Test http://lists.xensource.com/archives/html/xen-devel/2011-10/msg00584.html
    public void testRecord() throws BadAPICallException {
        //GuestMetrics.getAll();
        List<VM> residentVMs = Controller.getSession().getThisHost().getResidentVMs();

        VMMetrics vm = residentVMs.get(0).getMetrics();
        GuestMetrics guestMetrics = residentVMs.get(0).getGuestMetrics();
        Record r = new Record(Controller.getSession().getThisHost().getResidentVMs().get(0).getReference(), true);
        assertNotNull(r);
    }
}
