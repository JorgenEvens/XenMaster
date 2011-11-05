/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.api;

import java.util.List;
import net.wgr.xenmaster.TestBase;
import net.wgr.xenmaster.controller.BadAPICallException;
import org.junit.Test;

/**
 *
 * @author double-u
 */
public class PoolTest extends TestBase {

    @Test
    public void testSomeMethod() throws BadAPICallException {
        List<Pool> all = Pool.getAll();
    }
}
