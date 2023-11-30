package xyz.epicebic.simplechatgames.games;


import org.bukkit.scheduler.BukkitRunnable;

/**
 * For Starting a game
 */
public class GameRunnable extends BukkitRunnable {

    private final GameManager gameManager;

    public GameRunnable(GameManager game) {
        this.gameManager = game;
    }

    @Override
    public void run() {

    }
}
