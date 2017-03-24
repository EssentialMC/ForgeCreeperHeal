package fr.eyzox.forgecreeperheal.scheduler.graph.dependency.updater;

import fr.eyzox.dependencygraph.interfaces.IDependencyUpdater;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class FullAndDependency implements IDependencyUpdater<BlockPos> {

    @Override
    public boolean isAvailable(Set<BlockPos> dependenciesLeft) {
        return dependenciesLeft.isEmpty();
    }

}
