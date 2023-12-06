package xyz.epicebic.simplechatgames.configs;

import xyz.epicebic.ebiclib.config.annotation.Comment;
import xyz.epicebic.ebiclib.config.annotation.ConfigEntry;

public class GlobalConfig {

    @ConfigEntry("update-checker.enabled")
    public static boolean UPDATE_CHECKER_ENABLED = true;

    @ConfigEntry("update-checker.interval")
    @Comment("In hours")
    public static int UPDATE_CHECKER_INTERVAL = 2;

    @ConfigEntry("debug")
    public static boolean DEBUG = false;

    @ConfigEntry("leaderboard.entries-per-page")
    public static int LEADERBOARD_ENTRIES_PER_PAGE = 10;
}
