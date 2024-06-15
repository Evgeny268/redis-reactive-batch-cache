package org.nullhater.libraries.redis.cache.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface ReactiveRedisCache {

    Mono<Object> get(Object key);

    Mono<Void> put(Object key, Object value);

    Mono<Void> evict(Object key);

    Mono<Void> clear();

    default Mono<Void> invalidate() {
        return clear();
    }

    default Mono<Object> getMono(Object key) {
        return get(key);
    }

    default <T> Mono<T> getMono(Object key, Class<T> clazz) {
        return getMono(key).cast(clazz);
    }

    default <T> Mono<T> putMono(Object key, Mono<T> value) {
        return value.flatMap(data -> put(key, data).thenReturn(data));
    }

    @SuppressWarnings("unchecked")
    default Flux<Object> getFlux(Object key) {
        return get(key).cast(ArrayList.class).flatMapIterable(arrayList -> arrayList);
    }

    default <T> Flux<T> getFlux(Object key, Class<T> clazz) {
        return getFlux(key).cast(clazz);
    }

    default <T> Flux<T> putFlux(Object key, Flux<T> dataStream) {
        ArrayList<T> data = new ArrayList<>();
        return dataStream.doOnNext(data::add).concatWith(put(key, data).thenMany(Flux.empty()));
    }
}
