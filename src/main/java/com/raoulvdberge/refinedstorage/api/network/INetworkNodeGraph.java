package com.raoulvdberge.refinedstorage.api.network;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represents a graph of all the nodes connected to a network.
 */
public interface INetworkNodeGraph {
    /**
     * Rebuilds the network graph.
     *
     * @deprecated Use {@link #invalidate(Action, World, BlockPos)} - needed to support simulating the calculation of network connections
     */
    @Deprecated
    default void rebuild() {
        invalidate(Action.PERFORM, getNetworkForBCReasons().world(), getNetworkForBCReasons().getPosition());
    }

    /**
     * DON'T USE THIS!
     * This method exists to support a default {@link #rebuild()} method to maintain backward compatibility.
     */
    @Deprecated
    INetwork getNetworkForBCReasons();

    /**
     * Rebuilds the network graph.
     *
     * @param action whether to perform or simulate
     * @param world  the origin world
     * @param origin the origin, usually the network position
     */
    void invalidate(Action action, World world, BlockPos origin);

    /**
     * Runs an action on the network.
     * If the network is rebuilding it's graph, the action will be executed after the graph was built.
     *
     * @param handler the action to run
     * @deprecated Use {@link #runActionWhenPossible(Consumer)} - just a method rename
     */
    @Deprecated
    default void addPostRebuildHandler(Consumer<INetwork> handler) {
        runActionWhenPossible(handler);
    }

    /**
     * Runs an action on the network.
     * If the network is rebuilding it's graph, the action will be executed after the graph was built.
     *
     * @param handler the action to run
     */
    void runActionWhenPossible(Consumer<INetwork> handler);

    /**
     * @return a collection of all connected nodes
     */
    Collection<INetworkNode> all();

    /**
     * @param listener the listener
     */
    void addListener(INetworkNodeGraphListener listener);

    /**
     * Disconnects and notifies all connected nodes.
     */
    void disconnectAll();
}
