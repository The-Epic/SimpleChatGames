package xyz.epicebic.simplechatgames.configs;

import xyz.epicebic.ebiclib.config.annotation.Comment;
import xyz.epicebic.ebiclib.config.annotation.ConfigEntry;
import xyz.epicebic.simplechatgames.storage.StorageType;

public class StorageConfig {

    @ConfigEntry("type")
    @Comment("SQLITE, " /*TODO add the rest*/)
    public static StorageType STORAGE_TYPE = StorageType.SQLITE;

    @ConfigEntry("database-name")
    public static String DATABASE_NAME = "SimpleChatGames";

    @ConfigEntry("database-host")
    public static String DATABASE_HOST = "localhost";

    @ConfigEntry("database-port")
    @Comment("MySQL: 3306")
    @Comment("PostgreSQL: 5432")
    public static String DATABASE_PORT = "3306";

    @ConfigEntry("database-username")
    public static String DATABASE_USERNAME = "SimpleChatGames";

    @ConfigEntry("database-password")
    public static String DATABASE_PASSWORD = "SimpleChatGames";
}
