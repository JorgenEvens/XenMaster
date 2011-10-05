package net.wgr.xenmaster;

import net.wgr.core.access.Authorize;
import net.wgr.server.application.DefaultApplication;
import net.wgr.server.http.Server;
import net.wgr.server.web.handling.ServerHook;
import net.wgr.services.discovery.BasicDiscoverableService;
import net.wgr.services.discovery.Discovery;
import net.wgr.settings.Settings;
import net.wgr.xenmaster.web.Hook;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;


public class App implements Daemon {

    protected Server server;

    public static void main(String[] args) {
        try {
            App app = new App();
            app.init(null);
            app.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.INFO);
        root.addAppender(new ConsoleAppender(new TTCCLayout()));
    }

    @Override
    public void start() throws Exception {
        Settings ss = Settings.getInstance();

        new Thread(new Runnable() {

            @Override
            public void run() {
                if (Settings.getInstance().settingExists("DiscoveryServiceName")) {
                    Discovery.getInstance().announceService(BasicDiscoverableService.createInLocalNetwork("XenMaster", Settings.getInstance().getString("DiscoveryServiceName"), 16663));
                }
            }
        }).start();

        server = new Server();
        server.boot();
        
        ServerHook sh = new ServerHook();
        sh.addPandaHook(new Hook());
        
        Authorize.disable();
        
        server.addServlet(sh.getHttpHandler());
        server.addServlet(sh.getWebSocketHandler());
        
        DefaultApplication da = DefaultApplication.create("/", Settings.getInstance().getString("WebContentPath"));
        server.addHook(da);
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    @Override
    public void destroy() {
    }
}
