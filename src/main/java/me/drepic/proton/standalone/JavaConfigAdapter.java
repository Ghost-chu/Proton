package me.drepic.proton.standalone;

import com.ericrabil.yamlconfiguration.configuration.file.YamlConfiguration;
import com.google.common.io.ByteStreams;
import me.drepic.proton.common.adapters.ConfigAdapter;

import java.io.*;
import java.util.List;

public class JavaConfigAdapter implements ConfigAdapter {

    private YamlConfiguration config;

    public JavaConfigAdapter() {

    }

    @Override
    public void saveDefault() {
        File configFile = new File( "proton.yml");
        if (!configFile.exists()) { //Simply save default config into datafolder
            try {
                configFile.createNewFile();
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("proton.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to save default configuration file.");
            }
        }
    }

    @Override
    public void loadConfig() {
        File configFile = new File("proton.yml");
        if (!configFile.exists()) {
            throw new RuntimeException("No config file exists");
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public String getString(String path) {
        return this.config.getString(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return this.config.getStringList(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return this.config.getBoolean(path);
    }

    @Override
    public int getInt(String path) {
        return this.config.getInt(path);
    }
}
