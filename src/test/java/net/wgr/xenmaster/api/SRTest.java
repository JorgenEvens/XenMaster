/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.xenmaster.api;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import net.wgr.xenmaster.TestBase;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;

/**
 *
 * @author double-u
 */
public class SRTest extends TestBase {

    //@Test
    public void testSomeMethod() throws BadAPICallException, UnknownHostException, IOException {
        Host thisHost = Controller.getSession().getThisHost();
        List<SR> all = SR.getAll();
        //all.getLocal(0).destroy();
        //all.getLocal(0).setAsDefault(Pool.getAll().getLocal(0));

        HashMap<String, String> data = new HashMap<>();
        data.put("location", "/var/vm/test");

        SR ns = new SR(null);
        ns.setName("File-based SR");
        ns.setDescription("Test");
        ns.create(Controller.getSession().getThisHost(), data, SR.Type.File, "user", true, 0);
        ns.setAsDefault(Pool.getAll().get(0));
    }

    //@Test
    public void testjeuh() throws BadAPICallException {
        SR n = new SR(null);
        n.setName("VM store");
        n.setDescription("Ein gans tolle Store fur die VMs!");
        HashMap<String, String> map = new HashMap<>();
        map.put("device", "/dev/sdb");
        n.create(Controller.getSession().getThisHost(), map, SR.Type.EXT, "user", true, 0);
    }
}
