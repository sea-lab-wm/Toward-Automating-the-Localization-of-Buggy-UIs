package io.github.zwieback.familyfinance.util;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.annimon.stream.Collector;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CollectionUtils {

    /**
     * Determine emptiness of array.
     *
     * @param array source array
     * @param <T>   any type of object
     * @return {@code true} if array is empty or null, {@code false} otherwise
     */
    public static <T> boolean isEmpty(@Nullable T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Swap unique keys and unique values in a map to avoid data loss.
     *
     * @param map source map
     * @param <K> source type of key
     * @param <V> source type of value
     * @return map with swapped keys and values
     */
    public static <K, V> Map<V, K> swapUniqueMap(Map<K, V> map) {
        return Stream.of(map)
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    /**
     * Swap keys and values of the map.
     *
     * @param map source map
     * @param <K> source type of key
     * @param <V> source type of value
     * @return list of pairs with swapped keys and values of map
     * @implNote return type is List due to possible data loss: the key must be
     * unique, but the value is not, and swap of the keys and the values does
     * not guarantee this
     */
    public static <K, V> List<Pair<V, K>> swapMap(Map<K, V> map) {
        return Stream.of(map)
                .map(entry -> Pair.create(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    /**
     * Copied from {@link Collectors#toMap(Function, Function)} of
     * stream lib version 1.1.9
     */
    public static <T, K, V> Collector<T, ?, Map<K, V>> toMap(
            final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valueMapper) {
        return toMap(keyMapper, valueMapper, hashMapSupplier());
    }

    /**
     * Copied from {@link Collectors#toMap(Function, Function, Supplier)} of
     * stream lib version 1.1.9
     */
    public static <T, K, V, M extends Map<K, V>> Collector<T, ?, M> toMap(
            final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valueMapper,
            final Supplier<M> mapFactory) {
        return new CollectionUtils.CollectorsImpl<>(
                mapFactory,
                (map, t) -> {
                    final K key = keyMapper.apply(t);
                    final V value = valueMapper.apply(t);
                    final V oldValue = map.get(key);
                    final V newValue = (oldValue == null) ? value : oldValue;
                    if (newValue == null) {
                        map.remove(key);
                    } else {
                        map.put(key, newValue);
                    }
                }
        );
    }

    /**
     * Copied from {@link Collectors#hashMapSupplier()} of
     * stream lib version 1.1.9
     */
    private static <K, V> Supplier<Map<K, V>> hashMapSupplier() {
        return HashMap::new;
    }

    /**
     * Copied from {@link Collectors.CollectorsImpl} of
     * stream lib version 1.1.9
     */
    private static final class CollectorsImpl<T, A, R> implements Collector<T, A, R> {

        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final Function<A, R> finisher;

        public CollectorsImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator) {
            this(supplier, accumulator, null);
        }

        public CollectorsImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, Function<A, R> finisher) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.finisher = finisher;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }
    }

    private CollectionUtils() {
    }
}
