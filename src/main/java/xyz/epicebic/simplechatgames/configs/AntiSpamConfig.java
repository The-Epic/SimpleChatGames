package xyz.epicebic.simplechatgames.configs;

import xyz.epicebic.ebiclib.config.annotation.Comment;
import xyz.epicebic.ebiclib.config.annotation.ConfigEntry;

public class AntiSpamConfig {

    @ConfigEntry("cooldown.enabled")
    public static boolean COOLDOWN_ENABLED = false;

    @ConfigEntry("cooldown.time")
    @Comment("In seconds")
    public static int COOLDOWN_TIME = 3;
}
