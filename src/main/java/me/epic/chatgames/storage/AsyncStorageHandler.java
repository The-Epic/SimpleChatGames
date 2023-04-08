package me.epic.chatgames.storage;

import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import me.epic.chatgames.utils.PlayerData;

public class AsyncStorageHandler {

    private final Executor executor;
    private final StorageHandler delegate;

    public AsyncStorageHandler(Executor executor, StorageHandler delegate) {
        this.executor = executor;
        this.delegate = delegate;
    }

    Integer getPlayerData(OfflinePlayer player) {
        return delegate.getPlayerData(player);
    }

    CompletableFuture<Void> incrementPlayerData(OfflinePlayer player) {
        return CompletableFuture.runAsync(() -> delegate.incrementPlayerData(player), executor);
    }

    CompletableFuture<List<PlayerData>> getTopPlayerData(int start, int count) {
        return CompletableFuture.supplyAsync(() -> delegate.getTopPlayerData(start, count), executor);
    }

    CompletableFuture<PlayerData> getPlayerDataAtPosition(int position) {
        return CompletableFuture.supplyAsync(() -> delegate.getPlayerDataAtPosition(position), executor);
    }
}
