/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.api;

import net.wgr.xenmaster.TestBase;
import net.wgr.xenmaster.controller.BadAPICallException;
import org.junit.Test;

/**
 *
 * @author double-u
 */
public class VMTest extends TestBase {

    @Test
    public void testSomeMethod() throws BadAPICallException {
        VM vm = new VM(null);
        vm.setName("test");
        vm.create(2, 512, 256, 512, 256);
    }
    
}
