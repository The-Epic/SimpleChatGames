package xyz.epicebic.simplechatgames.storage;

import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import xyz.epicebic.simplechatgames.utils.PlayerData;

public class AsyncStorageHandler {

    private final Executor executor;
    private final StorageHandler delegate;

    public AsyncStorageHandler(Executor executor, StorageHandler delegate) {
        this.executor = executor;
        this.delegate = delegate;
    }

    public Integer getPlayerData(OfflinePlayer player) {
        return delegate.getPlayerData(player);
    }

    public void incrementPlayerData(OfflinePlayer player) {
        CompletableFuture.runAsync(() -> delegate.incrementPlayerData(player), executor);
    }

    public List<PlayerData> getTopPlayerData(int start, int count) {
        return delegate.getTopPlayerData(start, count);
    }

    public PlayerData getPlayerDataAtPosition(int position) {
        return delegate.getPlayerDataAtPosition(position);
    }
}
