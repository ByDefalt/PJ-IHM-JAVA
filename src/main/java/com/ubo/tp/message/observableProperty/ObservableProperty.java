package com.ubo.tp.message.observableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Une propriété observable simple (scalaire).
 */

public class ObservableProperty<T> {

    private T value;
    private final List<Consumer<T>> listeners = new ArrayList<>();

    public ObservableProperty(T initialValue) { this.value = initialValue; }
    public ObservableProperty() {}

    public T get() { return value; }

    public void set(T newValue) {
        this.value = newValue;
        listeners.forEach(l -> l.accept(newValue));
    }

    public void addListener(Consumer<T> listener)    { listeners.add(listener); }
    public void removeListener(Consumer<T> listener) { listeners.remove(listener); }

    public ListenerBuilder<T> on() {
        return new ListenerBuilder<>(this, false, null, null);
    }

    // ── Phase 1 : avant le map ────────────────────────────────────────────────

    public static class ListenerBuilder<T> {

        private final ObservableProperty<T> property;
        private final boolean               filterNonNull;
        private final Runnable              onNull;
        private final Consumer<T>           onChange;

        ListenerBuilder(ObservableProperty<T> property, boolean filterNonNull,
                        Runnable onNull, Consumer<T> onChange) {
            this.property      = property;
            this.filterNonNull = filterNonNull;
            this.onNull        = onNull;
            this.onChange      = onChange;
        }

        public ListenerBuilder<T> nonNull() {
            return new ListenerBuilder<>(property, true, onNull, onChange);
        }

        public ListenerBuilder<T> whenNull(Runnable handler) {
            return new ListenerBuilder<>(property, filterNonNull, handler, onChange);
        }

        public ListenerBuilder<T> changed(Consumer<T> handler) {
            return new ListenerBuilder<>(property, filterNonNull, onNull, handler);
        }

        public <R> MappedListenerBuilder<T, R> map(Function<T, R> mapper) {
            return new MappedListenerBuilder<>(property, filterNonNull, onNull, mapper, null);
        }

        public Consumer<T> bind() {
            Consumer<T> listener = value -> {
                if (value == null) {
                    if (onNull    != null) onNull.run();
                } else {
                    if (onChange  != null) onChange.accept(value);
                }
                if (value != null || !filterNonNull) {
                    if (value == null && onChange != null && !filterNonNull)
                        onChange.accept(null);
                }
            };
            // version propre
            Consumer<T> clean = value -> {
                if (value == null) {
                    if (onNull != null) onNull.run();
                    if (!filterNonNull && onChange != null) onChange.accept(null);
                } else {
                    if (onChange != null) onChange.accept(value);
                }
            };
            property.addListener(clean);
            return clean;
        }
    }

    // ── Phase 2 : après le map ────────────────────────────────────────────────

    public static class MappedListenerBuilder<T, R> {

        private final ObservableProperty<T> property;
        private final boolean               filterNonNull;
        private final Runnable              onNull;
        private final Function<T, R>        mapper;
        private final Consumer<R>           onChange;

        MappedListenerBuilder(ObservableProperty<T> property, boolean filterNonNull,
                              Runnable onNull, Function<T, R> mapper, Consumer<R> onChange) {
            this.property      = property;
            this.filterNonNull = filterNonNull;
            this.onNull        = onNull;
            this.mapper        = mapper;
            this.onChange      = onChange;
        }

        public MappedListenerBuilder<T, R> changed(Consumer<R> handler) {
            return new MappedListenerBuilder<>(property, filterNonNull, onNull, mapper, handler);
        }

        public <R2> MappedListenerBuilder<T, R2> map(Function<R, R2> next) {
            return new MappedListenerBuilder<>(property, filterNonNull, onNull, mapper.andThen(next), null);
        }

        public Consumer<T> bind() {
            Consumer<T> listener = value -> {
                if (value == null) {
                    if (onNull != null) onNull.run();
                } else {
                    if (onChange != null) onChange.accept(mapper.apply(value));
                }
            };
            property.addListener(listener);
            return listener;
        }
    }
}

