package xyz.epicebic.simplechatgames.utils;

import org.bukkit.Bukkit;
import xyz.epicebic.simplechatgames.configs.GlobalConfig;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class Debug {

    public static void debug(Supplier<String> message) {
        if (GlobalConfig.DEBUG) {
            Logger.getLogger("SimpleChatGames").info("[DEBUG] " + message.get());
        }
    }
}
