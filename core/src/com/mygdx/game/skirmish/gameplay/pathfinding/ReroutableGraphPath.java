package com.mygdx.game.skirmish.gameplay.pathfinding;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by paddlefish on 23-Sep-16.
 */
public class ReroutableGraphPath<N> extends DefaultGraphPath {
    public final Array<N> rerouteNodes;

    public ReroutableGraphPath() {
        super();
        rerouteNodes = new Array<>();
    }

    @Override
    public N get(int index) {
        if (index < rerouteNodes.size) {
            return rerouteNodes.get(index);
        }

        return (N) super.get(index - rerouteNodes.size);
    }

    @Override
    public Iterator iterator() {
        return getAllNodes().iterator();
    }

    private Array<N> getAllNodes() {
        Array<N> allNodes = new Array<>();
        allNodes.addAll(nodes);
        allNodes.addAll(rerouteNodes);
        return allNodes;
    }

    public void addToReroute(N node, int index) {
        rerouteNodes.insert(index, node);
    }

    @Override
    public int getCount() {
        return rerouteNodes.size + super.getCount();
    }

    public void remove(int index) {
        if (index < rerouteNodes.size) {
            rerouteNodes.removeIndex(index);
        } else {
            nodes.removeIndex(index - rerouteNodes.size);
        }
    }

}
