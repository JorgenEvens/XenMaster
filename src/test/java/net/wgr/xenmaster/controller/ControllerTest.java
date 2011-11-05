/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.controller;

import java.util.List;
import net.wgr.xenmaster.TestBase;
import net.wgr.xenmaster.api.Host;
import net.wgr.xenmaster.api.VM;
import org.junit.Test;

/**
 *
 * @author double-u
 */
public class ControllerTest extends TestBase {
    
    @Test
    public void test() throws BadAPICallException {
        Host host = Controller.getSession().getThisHost();
        List<VM> residentVMs = host.getResidentVMs();
    }
}
