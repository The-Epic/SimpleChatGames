package xyz.epicebic.simplechatgames.games;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * For how long a game runs
 */
public class GameTimeRunnable extends BukkitRunnable {

    private final long endTime;
    private final ChatGame<?> game;

    public GameTimeRunnable(ChatGame<?> game) {
        this.game = game;
        this.endTime = game.getEndTime();
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() >= endTime) {
            game.end(true);
        }
    }

}