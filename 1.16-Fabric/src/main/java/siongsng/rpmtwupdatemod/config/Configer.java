package siongsng.rpmtwupdatemod.config;

import me.shedaniel.autoconfig.AutoConfig;

public class Configer {
    public static ConfigScreen config = AutoConfig.getConfigHolder(ConfigScreen.class).getConfig();
}