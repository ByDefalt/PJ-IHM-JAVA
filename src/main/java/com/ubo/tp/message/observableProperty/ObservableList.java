package com.ubo.tp.message.observableProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Liste observable — conserve l'ordre d'insertion, autorise les doublons.
 * Ajoute les opérations indexées (get, set, add(index), remove(index)).
 */
public class ObservableList<T> extends ObservableCollection<T> {

    private final List<T> data = new ArrayList<>();

    // ── Mutations ─────────────────────────────────────────────────────────────

    @Override
    public boolean add(T element) {
        data.add(element);
        fire(ChangeType.ADDED, data.size() - 1, element);
        return true;
    }

    public void add(int index, T element) {
        data.add(index, element);
        fire(ChangeType.ADDED, index, element);
    }

    public T set(int index, T element) {
        T old = data.set(index, element);
        fire(ChangeType.UPDATED, index, element);
        return old;
    }

    public T remove(int index) {
        T removed = data.remove(index);
        fire(ChangeType.REMOVED, index, removed);
        return removed;
    }

    @Override
    public boolean remove(T element) {
        int index = data.indexOf(element);
        if (index < 0) return false;
        data.remove(index);
        fire(ChangeType.REMOVED, index, element);
        return true;
    }

    @Override
    public void clear() {
        data.clear();
        fire(ChangeType.CLEARED, -1, null);
    }

    // ── Lecture ───────────────────────────────────────────────────────────────

    public T get(int index) {
        return data.get(index);
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
    public List<T> snapshot() {
        return Collections.unmodifiableList(data);
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
