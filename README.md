# SimpleChatGames
Official repository for the SimpleChatGames plugin


# Using the api

## Repository and Dependency

### Maven

```xml

<repository>
  <id>epic-repository-public</id>
  <name>Epic Repository</name>
  <url>https://repo.epicebic.xyz/public</url>
</repository>

<dependency>
  <groupId>me.epic</groupId>
  <artifactId>chatgames</artifactId>
  <version>1.0.6-SNAPSHOT</version>
</dependency>
```

### Gradle (Groovy) 

```groovy
repositories {
  maven {
    url "https://repo.epicebic.xyz/public"
  }
}

repositories {
    compileOnly 'me.epic:chatgames:1.0.6-SNAPSHOT'
}
```

## Creating a game

#### Game file

coolgame.yml:
```yaml
duration: 20 # This is in seconds
enabled: true
``` 
Those are the only 2 required fields, the rest is all up to you

#### Registering the game

```java
import me.epic.chatgames.SimpleChatGames;
import me.epic.chatgames.spigotlib.utils.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {

    private SimpleChatGames simpleChatGames;

    @Override
    public void onEnable() {
        this.simpleChatGames = SimpleChatGames.getPlugin();
        FileUtils.loadResourceFile(this, "coolgame.yml").ifPresent(gameConfig -> simpleChatGames.getGameManager().registerGame(new CoolGameData(YamlConfiguration.loadConfiguration(gameConfig))));
    }
}
```

#### Creating the Game Data

```java

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.games.data.GameData;
import org.bukkit.configuration.file.YamlConfiguration;

public class CoolGameData extends GameData {

    public CoolGameData(YamlConfiguration config) {
        super(config);
    }

    @Override
    public ChatGame<?> createGame(GameManager manager) {
        return new CoolGame(this, manager);
    }
}

```

#### Creating the game class

```java

import me.epic.chatgames.games.ChatGame;
import me.epic.chatgames.games.GameManager;
import me.epic.chatgames.spigotlib.Timings;
import me.epic.chatgames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CoolGame extends ChatGame<CoolGameData> {

    private String answer = "";
    private YamlConfiguration gameConfig = gameData.getGameConfig();

    public CoolGame(CoolGameData data, GameManager manager) {
        super(data.getDuration(), manager, data);
    }

    @Override
    protected void start() {
        super.start();

        //Handle start logic


        // This may be changed to be handled in parent class logic, keep an eye out for updates
        if (manager.getPlugin().isDebugMode()) Bukkit.getOperators().forEach(offlinePlayer -> {
            if (offlinePlayer.isOnline()) {
                Bukkit.getPlayer(offlinePlayer.getName()).sendMessage("Chat Game Answer: " + answer);
            }
        });

        Timings.startTimings("coolgame-chatgame");
    }

    @Override
    protected void win(Player player) {
        super.win(player);

        //Handle win logic

        //This may be subject to change
        long timeTookLong = Timings.endTimings("coolgame-chatgame");
        String finalTimeTook = String.format("%.2f", ((double) timeTookLong / 1000.0));
        Utils.giveRewardAndNotify(manager.getPlugin(), player, gameData, finalTimeTook);
        answer = "";
    }

    @Override
    protected void end(boolean timeout) {
        super.end(timeout);

        //Handle timeout logic
    }

    @Override
    public void handleChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals(answer)) {
            win(event.getPlayer());
        }
    }
}
```

## Extra notes

The api may change in the future to keep an eye out for updates.

Any issues you find or come across you can come to [my discord](https://discord.com/invite/bpG46SDstM) for support or if you fix it yourself feel free to pr the fix and ill merge it as soon as i see it




