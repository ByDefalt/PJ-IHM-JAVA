package com.ubo.tp.message.observableProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Base commune à toutes les collections observables.
 */
public abstract class ObservableCollection<T> implements Iterable<T> {

    // ── Événements ────────────────────────────────────────────────────────────

    public enum ChangeType { ADDED, REMOVED, UPDATED, CLEARED }

    public record Change<T>(ChangeType type, int index, T element) {
        @Override public String toString() {
            return "Change{" + type + ", idx=" + index + ", el=" + element + "}";
        }
    }

    // ── Listeners ─────────────────────────────────────────────────────────────

    private final List<Consumer<Change<T>>> listeners = new ArrayList<>();

    public void addListener(Consumer<Change<T>> listener)    { listeners.add(listener); }
    public void removeListener(Consumer<Change<T>> listener) { listeners.remove(listener); }

    protected void fire(ChangeType type, int index, T element) {
        if (listeners.isEmpty()) return;
        Change<T> change = new Change<>(type, index, element);
        listeners.forEach(l -> l.accept(change));
    }

    public ListenerBuilder<T> on() {
        return new ListenerBuilder<>(this, null);
    }

    // ── Phase 1 : opère sur T ─────────────────────────────────────────────────

    public static class ListenerBuilder<T> {

        private final ObservableCollection<T> collection;
        private final Predicate<T>            filter;
        private Consumer<T>                   onAdded;
        private Consumer<T>                   onRemoved;
        private BiConsumer<Integer, T>        onUpdated;
        private Runnable                      onCleared;
        private Consumer<Change<T>>           onAny;

        ListenerBuilder(ObservableCollection<T> collection, Predicate<T> filter) {
            this.collection = collection;
            this.filter     = filter;
        }

        public ListenerBuilder<T> added  (Consumer<T> h)             { onAdded   = h; return this; }
        public ListenerBuilder<T> removed(Consumer<T> h)             { onRemoved = h; return this; }
        public ListenerBuilder<T> updated(BiConsumer<Integer, T> h)  { onUpdated = h; return this; }
        public ListenerBuilder<T> cleared(Runnable h)                { onCleared = h; return this; }
        public ListenerBuilder<T> any   (Consumer<Change<T>> h)      { onAny     = h; return this; }

        /** Filtre les éléments — seuls ceux qui passent le prédicat déclenchent les callbacks. */
        public ListenerBuilder<T> filter(Predicate<T> predicate) {
            Predicate<T> combined = filter == null ? predicate : filter.and(predicate);
            ListenerBuilder<T> next = new ListenerBuilder<>(collection, combined);
            next.onAdded   = onAdded;
            next.onRemoved = onRemoved;
            next.onUpdated = onUpdated;
            next.onCleared = onCleared;
            next.onAny     = onAny;
            return next;
        }

        /** Sucre — filtre les null. */
        public ListenerBuilder<T> nonNull() {
            return filter(Objects::nonNull);
        }

        /** Transforme T → R pour les callbacks added/removed/updated. */
        public <R> MappedListenerBuilder<T, R> map(Function<T, R> mapper) {
            return new MappedListenerBuilder<>(collection, filter, mapper, onCleared, onAny);
        }

        public Consumer<Change<T>> bind() {
            Consumer<Change<T>> listener = change -> {
                if (onAny != null) onAny.accept(change);
                // cleared ne porte pas d'élément, pas de filtre
                if (change.type == ChangeType.CLEARED) {
                    if (onCleared != null) onCleared.run();
                    return;
                }
                // applique le filtre sur l'élément
                if (filter != null && change.element != null && !filter.test(change.element)) return;
                switch (change.type) {
                    case ADDED   -> { if (onAdded   != null) onAdded.accept(change.element); }
                    case REMOVED -> { if (onRemoved != null) onRemoved.accept(change.element); }
                    case UPDATED -> { if (onUpdated != null) onUpdated.accept(change.index, change.element); }
                }
            };
            collection.addListener(listener);
            return listener;
        }
    }

    // ── Phase 2 : opère sur R après map ──────────────────────────────────────

    public static class MappedListenerBuilder<T, R> {

        private final ObservableCollection<T> collection;
        private final Predicate<T>            filter;
        private final Function<T, R>          mapper;
        private final Runnable                onCleared;
        private final Consumer<Change<T>>     onAny;
        private Consumer<R>                   onAdded;
        private Consumer<R>                   onRemoved;
        private BiConsumer<Integer, R>        onUpdated;

        MappedListenerBuilder(ObservableCollection<T> collection, Predicate<T> filter,
                              Function<T, R> mapper, Runnable onCleared, Consumer<Change<T>> onAny) {
            this.collection = collection;
            this.filter     = filter;
            this.mapper     = mapper;
            this.onCleared  = onCleared;
            this.onAny      = onAny;
        }

        public MappedListenerBuilder<T, R> added  (Consumer<R> h)            { onAdded   = h; return this; }
        public MappedListenerBuilder<T, R> removed(Consumer<R> h)            { onRemoved = h; return this; }
        public MappedListenerBuilder<T, R> updated(BiConsumer<Integer, R> h) { onUpdated = h; return this; }

        public MappedListenerBuilder<T, R> cleared(Runnable h) {
            return new MappedListenerBuilder<>(collection, filter, mapper, h, onAny);
        }

        /** Filtre sur T (avant le map). */
        public MappedListenerBuilder<T, R> filter(Predicate<T> predicate) {
            Predicate<T> combined = filter == null ? predicate : filter.and(predicate);
            return new MappedListenerBuilder<>(collection, combined, mapper, onCleared, onAny);
        }

        /** Filtre sur R (après le map). */
        public MappedListenerBuilder<T, R> filterMapped(Predicate<R> predicate) {
            // on compose : on wrappe le mapper pour tester après transformation
            Function<T, R> sameMapper = mapper;
            Predicate<T> asPredicateOnT = t -> predicate.test(sameMapper.apply(t));
            return filter(asPredicateOnT);
        }

        /** Chaîne un second map R → R2. */
        public <R2> MappedListenerBuilder<T, R2> map(Function<R, R2> next) {
            return new MappedListenerBuilder<>(collection, filter, mapper.andThen(next), onCleared, onAny);
        }

        public Consumer<Change<T>> bind() {
            Consumer<Change<T>> listener = change -> {
                if (onAny != null) onAny.accept(change);
                if (change.type == ChangeType.CLEARED) {
                    if (onCleared != null) onCleared.run();
                    return;
                }
                if (change.element == null) return;
                if (filter != null && !filter.test(change.element)) return;
                R mapped = mapper.apply(change.element);
                switch (change.type) {
                    case ADDED   -> { if (onAdded   != null) onAdded.accept(mapped); }
                    case REMOVED -> { if (onRemoved != null) onRemoved.accept(mapped); }
                    case UPDATED -> { if (onUpdated != null) onUpdated.accept(change.index, mapped); }
                }
            };
            collection.addListener(listener);
            return listener;
        }
    }

    // ── API abstraite ─────────────────────────────────────────────────────────

    public abstract boolean add(T element);
    public abstract boolean remove(T element);
    public abstract void    clear();
    public abstract int     size();
    public abstract boolean contains(T element);
    public abstract boolean isEmpty();
    public abstract Collection<T> snapshot();
}