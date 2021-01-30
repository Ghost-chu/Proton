package me.drepic.proton.common;

import me.drepic.proton.common.adapters.ConfigAdapter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Proton {

    private final ProtonBootstrap bootstrap;
    private ConfigAdapter config;

    public Proton(ProtonBootstrap bootstrap){
        this.bootstrap = bootstrap;
    }

    public final void enable(){
        this.config = getBootstrap().getConfiguration();
        this.config.saveDefault();

        Logger logger = getBootstrap().getPluginLogger();

        String clientName = this.config.getString("identification.clientName");
        if (clientName == null) {
            logger.log(Level.SEVERE, "The clientName must be set.");
            getBootstrap().disable();
            return;
        }

        String[] groups = config.getStringList("identification.groups").toArray(new String[0]);

        if (!verifyIdentification(clientName, groups)) {
            logger.log(Level.SEVERE, "The clientName/groups cannot contain `.` - Shutting down.");
            getBootstrap().disable();
            return;
        }

        boolean useRabbitMQ = config.getBoolean("rabbitMQ.useRabbitMQ");
        boolean useRedis = config.getBoolean("redis.useRedis");

        if (!useRabbitMQ && !useRedis) {
            logger.log(Level.SEVERE, "Neither RabbitMQ nor Redis is enabled. Shutting down.");
            getBootstrap().disable();
            return;
        }

        if (useRabbitMQ) {
            try {
                setupRabbitMQ(clientName, groups);
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                logger.severe("Failed to setup RabbitMQ Connection");
                getBootstrap().disable();
                return;
            }
        } else {
            setupRedis(clientName, groups);
        }

        boolean checkForUpdates = config.getBoolean("checkForUpdates");

        if (checkForUpdates) {
            try {
                new UpdateChecker(this, getBootstrap().getVersion());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final void disable(){
        try {
            ProtonManager manager = ProtonProvider.get();
            manager.tearDown();
            ProtonProvider.unregister();
        }catch(Exception ignored){
        }
    }

    private void setupRabbitMQ(String clientName, String[] groups) throws IOException, TimeoutException {
        String host = this.config.getString("rabbitMQ.host");
        String virtualHost = this.config.getString("rabbitMQ.virtualHost");
        int port = this.config.getInt("rabbitMQ.port");
        boolean useAuthorization = this.config.getBoolean("rabbitMQ.authorization.useAuthorization");

        if (!useAuthorization) {
            ProtonProvider.register(new RabbitMQManager(this, clientName, groups, host, virtualHost, port));
        } else {
            String username = this.config.getString("rabbitMQ.authorization.username");
            String password = this.config.getString("rabbitMQ.authorization.password");
            ProtonProvider.register(new RabbitMQManager(this, clientName, groups, host, virtualHost, port, username, password));
        }
    }

    private void setupRedis(String clientName, String[] groups) {
        String host = this.config.getString("redis.host");
        int port = this.config.getInt("redis.port");
        boolean usePassword = this.config.getBoolean("redis.usePassword");

        if (!usePassword) {
            ProtonProvider.register(new RedisManager(this, clientName, groups, host, port));
        } else {
            String password = this.config.getString("redis.password");
            ProtonProvider.register(new RedisManager(this, clientName, groups, host, port, password));
        }
    }

    private boolean verifyIdentification(String clientName, String[] groups) {
        if (clientName.contains("\\.")) {
            return false;
        }

        for (String group : groups) {
            if (group.contains("\\.")) {
                return false;
            }
        }

        return true;
    }

    protected ProtonBootstrap getBootstrap(){
        return this.bootstrap;
    }
}


