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
public class VDITest extends TestBase {

    /**
     * Test of getStorageRepository method, of class VDI.
     */
    //@Test
    public void testDeletion() throws BadAPICallException {
        List<VDI> all = VDI.getAll();
        for (VDI vdi : all) {
            vdi.destroy();
        }

        all = VDI.getAll();
    }

    @Test
    public void testCreation() throws BadAPICallException {
       
    }
}
