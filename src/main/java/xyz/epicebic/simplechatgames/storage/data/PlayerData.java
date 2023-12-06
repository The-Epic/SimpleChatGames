package xyz.epicebic.simplechatgames.storage.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Objects;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final String name;
    private int wins;
    private int losses;
    private int unclaimedRewards;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
    }

    public PlayerData(UUID uuid, int wins, int losses, int unclaimedRewards) {
        this(uuid);
        this.wins = wins;
        this.losses = losses;
        this.unclaimedRewards = unclaimedRewards;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getUnclaimedRewards() {
        return unclaimedRewards;
    }

    public String getName() {
        return name;
    }

    public void increaseWins() {
        this.wins++;
    }

    public void increaseLosses() {
        this.losses++;
    }

    public void increaseUnclaimedRewards() {
        this.unclaimedRewards++;
    }

    public void decreaseUnclaimedRewards() {
        this.unclaimedRewards--;
    }

    public void resetUnclaimedRewards() {
        this.unclaimedRewards = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlayerData that = (PlayerData) o;
        return wins == that.wins && losses == that.losses && unclaimedRewards == that.unclaimedRewards && Objects.equals(
                uuid,
                that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, wins, losses, unclaimedRewards);
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "uuid=" + uuid +
                ", wins=" + wins +
                ", losses=" + losses +
                ", unclaimedRewards=" + unclaimedRewards +
                '}';
    }
}
