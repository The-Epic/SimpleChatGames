package xyz.epicebic.simplechatgames.configs;

import xyz.epicebic.ebiclib.config.annotation.Comment;
import xyz.epicebic.ebiclib.config.annotation.ConfigEntry;

public class GlobalConfig {

    @ConfigEntry("update-checker.enabled")
    public static Boolean UPDATE_CHECKER_ENABLED = true;

    @ConfigEntry("update-checker.interval")
    @Comment("In hours")
    public static Integer UPDATE_CHECKER_INTERVAL = 2;

    @ConfigEntry("debug")
    public static Boolean DEBUG = false;


}
