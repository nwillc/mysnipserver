/*
 * Copyright (c) 2016, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package com.github.nwillc.mysnipserver.dao.orchestrate;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Entity;
import io.orchestrate.client.Client;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.Result;
import org.pmw.tinylog.Logger;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

public class CollectionDao<T extends Entity> implements Dao<T> {
	private static final CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
	private static final int LIMIT = 100;
	private final Class<T> tClass;
	private final String collection;
	private final Client client;
	private final Cache<String, T> cache;

	public CollectionDao(Client client, Class<T> tClass) {
		this(client, tClass.getSimpleName(), tClass);
	}

	public CollectionDao(Client client, String collection, Class<T> tClass) {
		this.collection = collection;
		this.tClass = tClass;
		this.client = client;
		cache = getCache(collection, tClass);
	}

	@Override
	public Optional<T> findOne(final String key) {
		return Optional.ofNullable(get(key));
	}

	@Override
	public Stream<T> findAll() {
		Set<String> keys = stream(client.listCollection(collection)
				.limit(LIMIT)
				.withValues(false)
				.get(tClass)
				.get().spliterator(), false).map(KvObject::getKey).collect(toSet());
		return find(keys);
	}

	@Override
	public Stream<T> find(Predicate<T> predicate) {
		Set<String> keys = stream(client.searchCollection(collection)
				.limit(LIMIT)
				.get(tClass, predicate.toString())
				.get().spliterator(), false).map(Result::getKvObject).map(KvObject::getKey).collect(toSet());
		return find(keys);
	}

	@Override
	public void save(final T entity) {
		Logger.info("Writing out to orchestrate: " + entity);
		client.kv(collection, entity.getKey())
				.put(entity)
				.get();
		cache.put(entity.getKey(), entity);
		Logger.info("Thinks its: " + get(entity.getKey()));
	}

	@Override
	public void delete(final String key) {
		client.kv(collection, key)
				.delete(true)
				.get();
		cache.remove(key);
	}

	public T get(String key) {
		return cache.get(key);
	}

	private Stream<T> find(Set<String> keys) {
		return keys.stream().map(this::get).filter(Objects::nonNull);
	}

	public String getCollection() {
		return collection;
	}

	private Cache<String, T> getCache(String name, Class<T> clz) {
		final Cache<String, T> cache = Caching.getCachingProvider().getCacheManager().getCache(name,
				String.class, clz);
		if (cache != null) {
			return cache;
		}
		MutableConfiguration<String, T> configuration = new MutableConfiguration<>();
		configuration.setTypes(String.class, tClass);
		configuration.setStoreByValue(false);
		configuration.setReadThrough(true);
		configuration.setCacheLoaderFactory((Factory<CacheLoader<String, T>>) OrchestrateLoader::new);
		return cacheManager.createCache(collection, configuration);
	}

	private class OrchestrateLoader implements CacheLoader<String, T> {
		@Override
		public T load(String key) throws CacheLoaderException {
			KvObject<T> categoryKvObject = client.kv(collection, key)
					.get(tClass)
					.get();
			return categoryKvObject == null ? null : categoryKvObject.getValue(tClass);
		}

		@Override
		public Map<String, T> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
			return null;
		}
	}
}
