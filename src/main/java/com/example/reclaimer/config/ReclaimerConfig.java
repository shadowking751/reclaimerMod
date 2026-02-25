package com.example.reclaimer.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "reclaimer")
public class ReclaimerConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public int scanInterval = 20;

    @ConfigEntry.Gui.Tooltip
    public int scanRadius = 6;

    @ConfigEntry.Gui.Tooltip
    public int maxBlocksPerTick = 4;

    @ConfigEntry.Gui.Tooltip
    public boolean revertTerrain = true;

    @ConfigEntry.Gui.Tooltip
    public boolean removeLights = true;

    @ConfigEntry.Gui.Tooltip
    public boolean structureGriefEnabled = true;

    @ConfigEntry.Gui.Tooltip
    public int structureRadius = 8;

    @ConfigEntry.Gui.Tooltip
    public int maxStructureChangesPerTick = 16;

    @ConfigEntry.Gui.Tooltip
    public boolean chestCorruptionEnabled = true;

    @ConfigEntry.Gui.Tooltip
    public int chestRadius = 6;
}
