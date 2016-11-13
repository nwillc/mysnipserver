package com.github.nwillc.mysnipserver.dao;

import com.github.nwillc.mysnipserver.entity.Entity;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheLoader;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CachedDao<T extends Entity> implements Dao<T> {
    private static final CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
    private final Cache<String, T> cache;
    private final Dao<T> dao;

    public CachedDao(final String name, final Factory<CacheLoader<String, T>> loaderFactory,
                     final Class<T> tClass, final Dao<T> dao) {
        cache = getCache(name, loaderFactory, tClass);
        this.dao = dao;
    }

    @Override
    public Optional<T> findOne(String key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public Stream<T> findAll() {
        return dao.findAll().peek(e -> cache.put(e.getKey(),e));
    }

    @Override
    public Stream<T> find(Predicate<T> predicate) {
        return dao.find(predicate).peek(e -> cache.put(e.getKey(),e));
    }

    @Override
    public void save(T entity) {
        dao.save(entity);
        cache.put(entity.getKey(), entity);
    }

    @Override
    public void delete(String key) {
        dao.delete(key);
        cache.remove(key);
    }

    private T get(String key) {
        return cache.get(key);
    }

    private static <T> Cache<String, T> getCache(final String name, final Factory<CacheLoader<String, T>> loaderFactory, final Class<T> tClass) {
        final Cache<String, T> cache = Caching.getCachingProvider().getCacheManager().getCache(name,
                String.class, tClass);
        if (cache != null) {
            return cache;
        }
        MutableConfiguration<String, T> configuration = new MutableConfiguration<>();
        configuration.setTypes(String.class, tClass);
        configuration.setStoreByValue(false);
        configuration.setReadThrough(true);
        configuration.setCacheLoaderFactory(loaderFactory);
        return cacheManager.createCache(name, configuration);
    }
}
