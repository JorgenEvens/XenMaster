package net.wgr.xenmaster;

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
        root.addAppender(new ConsoleAppender(new TTCCLayout()));
    }

    @Override
    public void start() throws Exception {
        Controller.build(new URL(Settings.getInstance().getString("Xen.URL")));
        Controller.getSession().loginWithPassword("root", "r00tme");
        
        DataPool.simpleBoot(Settings.getInstance().getString("Cassandra.PoolName"), Settings.getInstance().getString("Cassandra.Host"), Settings.getInstance().getString("Cassandra.Keyspace"));
        Pool.get().boot();
        MonitoringAgent.get().boot();
        MonitoringAgent.get().start();

        server = new Server();
        server.boot();

        ServerHook sh = new ServerHook("/*");
        sh.addWebHook(new Hook());
        sh.addWebHook(new TemplateHook());
        sh.addWebHook(new SetupHook());
        sh.addWebHook(new VNCHook());

        Authorize.disable();

        server.addServlet(sh.getHttpHandler());
        server.addServlet(sh.getWebSocketHandler());

        DefaultApplication da = DefaultApplication.create("/", Settings.getInstance().getString("WebContentPath"));
        server.addHook(da);
        server.start();

        Bootstrapper b = new Bootstrapper();
        b.boot();
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
