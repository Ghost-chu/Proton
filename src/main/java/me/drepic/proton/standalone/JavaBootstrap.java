package me.drepic.proton.standalone;

import me.drepic.proton.common.Proton;
import me.drepic.proton.common.ProtonBootstraper;
import me.drepic.proton.common.adapters.ConfigAdapter;
import me.drepic.proton.common.adapters.SchedulerAdapter;

import java.util.logging.Logger;

public class JavaBootstrap implements ProtonBootstraper {

    private JavaSchedulerAdapter schedulerAdapter;
    private JavaConfigAdapter configAdapter;
    private Proton proton;

    public void load() {
        this.schedulerAdapter = new JavaSchedulerAdapter();
        this.configAdapter = new JavaConfigAdapter();
        this.proton = new Proton(this);
        this.proton.enable();
    }

    public void unload() {
        this.proton.disable();
    }

    @Override
    public Logger getPluginLogger() {
        return Logger.getLogger("Proton-Standalone");
    }

    public SchedulerAdapter getScheduler() {
        return this.schedulerAdapter;
    }

    public ConfigAdapter getConfiguration() {
        return this.configAdapter;
    }

    @Override
    public String getVersion() {
        return "1.3.0";
    }

    @Override
    public void disable() {
        this.unload();
    }
}
