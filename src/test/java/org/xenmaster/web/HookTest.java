/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xenmaster.web;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.wgr.wcp.command.Command;
import org.xenmaster.TestBase;
import org.xenmaster.api.entities.Host;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author double-u
 */
public class HookTest extends TestBase {
    
    protected Hook hook;

    @Before
    public void setUp() {
        hook = new Hook();
    }

    /**
     * Test of execute method, of class Hook.
     */
    @Test
    public void testExecute() {
        Hook.APICall apic = new Hook.APICall();
        apic.args = new Object[0];
        apic.ref = "";
        Gson gson = new Gson();
        JsonElement json = gson.toJsonTree(apic);
        Command cmd = new Command("xen", "Session[].getThisHost", apic);
        Object o = hook.execute(cmd);
        assertNotNull(o);
        Host s = (Host) o;
        System.out.println("Your current host id: " + s.getUUID().toString());
    }
}
