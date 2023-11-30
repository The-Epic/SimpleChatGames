package xyz.epicebic.simplechatgames.games;

import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {

    private final long endTime;
    private final ChatGame<?> game;

    public GameRunnable(ChatGame<?> game) {
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