package net.wgr.xenmaster;

import java.io.IOException;
import java.net.URL;
import net.wgr.core.access.Authorize;
import net.wgr.core.data.DataPool;
import net.wgr.server.application.DefaultApplication;
import net.wgr.server.http.Server;
import net.wgr.server.web.handling.ServerHook;
import net.wgr.settings.Settings;
import net.wgr.utility.GlobalExecutorService;
import net.wgr.xenmaster.controller.Controller;
import net.wgr.xenmaster.monitoring.MonitoringAgent;
import net.wgr.xenmaster.pool.Pool;
import net.wgr.xenmaster.setup.debian.Bootstrapper;
import net.wgr.xenmaster.web.Hook;
import net.wgr.xenmaster.web.SetupHook;
import net.wgr.xenmaster.web.TemplateHook;
import net.wgr.xenmaster.web.VNCHook;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class App implements Daemon {

    protected Server server;
    public static final String LOGPATTERN = "%d{HH:mm:ss,SSS} | %-5p | %t | %c{1.} %m%n";

    public static void main(String[] args) {
        try {
            App app = new App();
            app.init(null);
            app.start();
        } catch (Exception ex) {
            Logger.getLogger(App.class).error("An unhandled exception ocurred", ex);
        }
    }

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        if (context != null) {
            Logger.getLogger(getClass()).info("Starting XenMaster service");
            if (context.getArguments() != null && context.getArguments()[0] != null) {
                Settings.loadFromFile(context.getArguments()[0]);
            }
        }

        Logger root = Logger.getRootLogger();
        root.setLevel(Level.toLevel(Settings.getInstance().getString("Logging.Level")));
        root.addAppender(new ConsoleAppender(new EnhancedPatternLayout(LOGPATTERN)));
    }

    @Override
    public void start() throws Exception {
        DataPool.simpleBoot(Settings.getInstance().getString("Cassandra.PoolName"), Settings.getInstance().getString("Cassandra.Host"), Settings.getInstance().getString("Cassandra.Keyspace"));
        Bootstrapper b = new Bootstrapper();
        b.boot();

        Controller.build(new URL(Settings.getInstance().getString("Xen.URL")));
        Controller.getSession().loginWithPassword("root", "r00tme");

        server = new Server();
        server.boot();
        
        Authorize.disable();

        if (Controller.getSession().getReference() == null) {
            Logger.getLogger(getClass()).error("Failed to connect to XAPI instance, running in bootstrap mode");

            ServerHook sh = new ServerHook("/*");
            sh.addWebHook(new SetupHook());
            sh.hookIntoServer(server);
            server.start();

            b.waitForServerToQuit();
            return;
        }

        Pool.get().boot();
        MonitoringAgent.instance().boot();
        MonitoringAgent.instance().start();

        server = new Server();
        server.boot();

        ServerHook sh = new ServerHook("/*");
        sh.addWebHook(new Hook());
        sh.addWebHook(new TemplateHook());
        sh.addWebHook(new SetupHook());
        sh.addWebHook(new VNCHook());

        server.addServlet(sh.getHttpHandler());
        server.addServlet(sh.getWebSocketHandler());

        DefaultApplication da = DefaultApplication.create("/", Settings.getInstance().getString("WebContentPath"));
        server.addHook(da);
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
        Pool.get().stop();
        DataPool.stop();
        GlobalExecutorService.get().shutdownNow();
    }

    @Override
    public void destroy() {
    }
}
