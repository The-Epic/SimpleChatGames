package xyz.epicebic.simplechatgames.configs;

import xyz.epicebic.ebiclib.config.annotation.Comment;
import xyz.epicebic.ebiclib.config.annotation.ConfigEntry;

public class AntiSpamConfig {

    @ConfigEntry("cooldown.enabled")
    public static Boolean COOLDOWN_ENABLED = false;

    @ConfigEntry("cooldown.time")
    @Comment("In seconds")
    public static Integer COOLDOWN_TIME = 3;
}
