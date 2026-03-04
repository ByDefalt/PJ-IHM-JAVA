package com.ubo.tp.message.observableProperty;

import java.util.*;

/**
 * TreeSet observable — pas de doublons, trié naturellement ou par Comparator.
 * Expose en plus first(), last(), headSet(), tailSet() du NavigableSet.
 */
public class ObservableTreeSet<T> extends ObservableCollection<T> {

    private final TreeSet<T> data;

    /**
     * Tri naturel (T doit implémenter Comparable).
     */
    public ObservableTreeSet() {
        this.data = new TreeSet<>();
    }

    /**
     * Tri personnalisé.
     */
    public ObservableTreeSet(Comparator<? super T> comparator) {
        this.data = new TreeSet<>(comparator);
    }

    @Override
    public boolean add(T element) {
        if (!data.add(element)) return false;
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

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Remplace oldElement par newElement en émettant un seul UPDATED.
     * Retourne false si oldElement n'existe pas dans le set.
     */
    public boolean update(T oldElement, T newElement) {
        if (!data.remove(oldElement)) return false;
        data.add(newElement);
        fire(ChangeType.UPDATED, -1, newElement);
        return true;
    }

    // ── Spécifique TreeSet ────────────────────────────────────────────────────

    public T first() {
        return data.first();
    }

    public T last() {
        return data.last();
    }

    public T floor(T e) {
        return data.floor(e);
    }  // <= e

    public T ceiling(T e) {
        return data.ceiling(e);
    }  // >= e

    public T lower(T e) {
        return data.lower(e);
    }  // < e

    public T higher(T e) {
        return data.higher(e);
    }  // > e

    public SortedSet<T> headSet(T toExclusive) {
        return data.headSet(toExclusive);
    }

    public SortedSet<T> tailSet(T fromInclusive) {
        return data.tailSet(fromInclusive);
    }

    public SortedSet<T> subSet(T from, T toExclusive) {
        return data.subSet(from, toExclusive);
    }

    public T pollFirst() {
        T e = data.pollFirst();
        if (e != null) fire(ChangeType.REMOVED, -1, e);
        return e;
    }

    public T pollLast() {
        T e = data.pollLast();
        if (e != null) fire(ChangeType.REMOVED, -1, e);
        return e;
    }

    // ── Lecture ───────────────────────────────────────────────────────────────

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
    public SortedSet<T> snapshot() {
        return Collections.unmodifiableSortedSet(data);
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