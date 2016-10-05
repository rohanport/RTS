package com.mygdx.game.skirmish.gameobjects;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.skirmish.gameplay.Commandable;
import com.mygdx.game.skirmish.gameplay.pathfinding.GroundNode;

import java.util.List;

/**
 * Created by paddlefish on 05-Oct-16.
 */
public interface GameObjectManager<N> {
    GameObjectType getObjectType();

    void render(boolean debug);
    void add(N gameObject);
    void remove(N gameObject);

    List<N> getIntersecting(Vector2 point);
    List<N> getIntersecting(Polygon box);
    List<Commandable> getIntersectingCommandables(Vector2 point);
    List<Commandable> getIntersectingCommandables(Polygon box);
    List<N> getAtNode(GroundNode node);
}
