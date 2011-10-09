/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.controller;

import java.util.List;
import junit.framework.TestCase;
import net.wgr.xenmaster.api.Host;
import net.wgr.xenmaster.api.VM;
import org.junit.Test;

/**
 *
 * @author double-u
 */
public class ControllerTest extends TestCase {
    
    public ControllerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void test() {
        Host host = Controller.getSession().getThisHost();
        List<VM> residentVMs = host.getResidentVMs();
    }
}
