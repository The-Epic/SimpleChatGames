package xyz.epicebic.simplechatgames.configs;

import org.bukkit.Sound;
import xyz.epicebic.ebiclib.config.annotation.Comment;
import xyz.epicebic.ebiclib.config.annotation.Comments;
import xyz.epicebic.ebiclib.config.annotation.ConfigEntry;

public class GamesGlobalConfig {

    @ConfigEntry("interval")
    @Comment("In Minutes")
    public static Integer INTERVAL = 5;

    @ConfigEntry("players-needed")
    public static Integer PLAYERS_NEEDED = 2;

    @ConfigEntry("not-enough-players-message")
    public static String NOT_ENOUGH_PLAYERS_MESSAGE = "<green>Not enough players online";

    @ConfigEntry("sounds.enabled")
    public static Boolean SOUNDS_ENABLED = false;

    @ConfigEntry("sounds.game-start")
    @Comment("Valid sounds can be found on the following link: ")
    @Comment("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html")
    public static Sound SOUNDS_GAME_START = Sound.BLOCK_ANVIL_LAND;

    @ConfigEntry("sounds.game-win")
    public static Sound SOUNDS_GAME_WIN = Sound.ENTITY_VILLAGER_YES;

    @ConfigEntry("sounds.game-end")
    public static Sound SOUNDS_GAME_END = Sound.ENTITY_VILLAGER_NO;
}
