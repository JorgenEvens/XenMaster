/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.api;

import java.util.List;
import net.wgr.xenmaster.controller.BadAPICallException;
import org.junit.Test;

/**
 *
 * @author double-u
 */
public class VIFTest {
    @Test
    public void testSomeMethod() throws BadAPICallException {
        List<Network> all = Network.getAll();
    }
}
