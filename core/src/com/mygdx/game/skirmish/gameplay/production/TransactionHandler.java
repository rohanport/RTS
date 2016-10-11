package com.mygdx.game.skirmish.gameplay.production;

import com.mygdx.game.skirmish.World;
import com.mygdx.game.skirmish.player.Player;

/**
 * Created by paddlefish on 09-Oct-16.
 *
 * Handles buying of units, upgrades, etc. in game
 */
public class TransactionHandler {

    public class Cost {
        public final int food;

        public Cost(int food) {
            this.food = food;
        }
    }

    public final Cost SOLDIER1_BUILDING1 = new Cost(100);
    public final Cost BUILDING1_SOLDIER1 = new Cost(50);

    private final World world;

    public TransactionHandler(World world) {
        this.world = world;
    }

    /**
     * Performs an action if the player can afford the cost
     * @param playerID
     * @param cost
     * @param action
     * @return true if the transaction is successful
     */
    public boolean processTransaction(int playerID, Cost cost, Runnable action) {
        Player player = world.getPlayerManager().getPlayerByID(playerID);
        if (player.food >= cost.food) {
            player.food -= cost.food;
            action.run();
            return true;
        }

        return false;
    }
}
