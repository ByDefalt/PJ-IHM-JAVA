package com.ubo.tp.message.observableProperty;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Set observable — pas de doublons, pas d'ordre garanti (HashMap en dessous).
 * index toujours -1 dans les Change (un Set n'a pas de notion d'index).
 */
public class ObservableSet<T> extends ObservableCollection<T> {

    private final Set<T> data = new HashSet<>();

    @Override
    public boolean add(T element) {
        if (!data.add(element)) return false; // doublon ignoré silencieusement
        fire(ChangeType.ADDED, -1, element);
        return true;
    }

    @Override
    public boolean remove(T element) {
        if (!data.remove(element)) return false;
        fire(ChangeType.REMOVED, -1, element);
        return true;
    }

    @Override
    public void clear() {
        data.clear();
        fire(ChangeType.CLEARED, -1, null);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean contains(T e) {
        return data.contains(e);
    }

    @Override
    public Set<T> snapshot() {
        return Collections.unmodifiableSet(data);
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
