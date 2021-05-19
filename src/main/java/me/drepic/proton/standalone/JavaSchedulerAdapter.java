package me.drepic.proton.standalone;

import me.drepic.proton.common.adapters.SchedulerAdapter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class JavaSchedulerAdapter implements SchedulerAdapter {


    public JavaSchedulerAdapter() {

    }

    @Override
    public void runTask(Runnable runnable) {
        //There is no synchronous runTask in bungee, it is generally threadsafe
        runnable.run();
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
