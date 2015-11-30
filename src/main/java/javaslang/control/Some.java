/*     / \____  _    _  ____   ______  / \ ____  __    _ _____
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  / /  _  \   Javaslang
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/  \__/  /   Copyright 2014-now Daniel Dietrich
 * /___/\_/  \_/\____/\_/  \_/\__\/__/___\_/  \_//  \__/_____/    Licensed under the Apache License, Version 2.0
 */
package javaslang.control;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Some represents a defined {@link javaslang.control.Option}. It contains a value which may be null. However, to
 * create an Option containing null, {@code new Some(null)} has to be called. In all other cases
 * {@link Option#of(Object)} is sufficient.
 *
 * @param <T> The type of the optional value.
 * @author Daniel Dietrich
 * @since 1.0.0
 */
public final class Some<T> implements Option<T>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The singleton instance of {@code Some<Void>}.
     */
    private static final Some<Void> NOTHING = new Some<>(null);

    private final T value;

    /**
     * Creates a new Some containing the given value.
     *
     * @param value A value, may be null
     */
    public Some(T value) {
        this.value = value;
    }

    /**
     * Return the singleton instance of {@code Some<Void>}.
     *
     * @return {@link #NOTHING}
     */
    public static Some<Void> nothing() {
        return NOTHING;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public <U> U accept(Supplier<U> visitNone, Function<? super T, ? extends U> visitSome) {
        return visitSome.apply(value);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this) || (obj instanceof Some && Objects.equals(value, ((Some<?>) obj).value));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "Some(" + value + ")";
    }
}
