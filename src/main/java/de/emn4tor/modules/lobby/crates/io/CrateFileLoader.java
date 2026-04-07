package de.emn4tor.modules.lobby.crates.io;

import de.emn4tor.YellowMCCoreV2;

import java.io.File;

public class CrateFileLoader {
    private final File cratesFolder;

    public CrateFileLoader(){
        cratesFolder = new File(YellowMCCoreV2.getInstance().getDataFolder(), "crates");
        if (!cratesFolder.exists()) cratesFolder.mkdirs();
    }

    public File[] getCrateFiles() {
        if (!cratesFolder.exists()) {
            boolean created = cratesFolder.mkdirs();
            if (created) {
                YellowMCCoreV2.getInstance().getLogger().info("Crates folder created at: " + cratesFolder.getAbsolutePath());
            }
        }

        return cratesFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
    }
}
