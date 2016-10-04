package com.mygdx.game.skirmish.player;

import com.mygdx.game.skirmish.SkirmishScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paddlefish on 04-Oct-16.
 */
public class PlayerManager {
    private final SkirmishScreen screen;
    private final List<Player> players;

    public PlayerManager(SkirmishScreen screen) {
        this.screen = screen;
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Player getPlayerByID(int id) {
        return players.get(id);
    }

    public int getNumPlayers() {
        return players.size();
    }
}
