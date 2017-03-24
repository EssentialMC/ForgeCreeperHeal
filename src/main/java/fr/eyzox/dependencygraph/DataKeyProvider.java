package fr.eyzox.dependencygraph;

import fr.eyzox.dependencygraph.exceptions.DuplicateKeyException;
import fr.eyzox.dependencygraph.interfaces.IData;

import java.util.Collection;
import java.util.Map;

public abstract class DataKeyProvider<K> {

    DataKeyProvider() {
    }

    protected abstract <D extends IData<K>> void buildIndex(final Map<K, DependencyGraph<K, D>.Node> index, final DependencyGraph<K, D>.Node theNode) throws DuplicateKeyException;

    protected abstract <D extends IData<K>> void removeFromIndex(final Map<K, DependencyGraph<K, D>.Node> index, final DependencyGraph<K, D>.Node theNode);

    protected abstract void removeKeyFrom(final Collection<? extends K> c);
}
