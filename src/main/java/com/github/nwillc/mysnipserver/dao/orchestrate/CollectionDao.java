/*
 * Copyright (c) 2015,  nwillc@gmail.com
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
 */

package com.github.nwillc.mysnipserver.dao.orchestrate;

import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.dao.Entity;
import io.orchestrate.client.Client;
import io.orchestrate.client.KvObject;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionDao<T extends Entity> implements Dao<T> {
	private final Class<T> tClass;
	private final String collection;
	private final Client client;
	private int limit = 100;
    private final Cache<String,T> cache;

	public CollectionDao(Client client, Class<T> tClass) {
		this(client, tClass.getSimpleName(), tClass);
	}

	public CollectionDao(Client client, String collection, Class<T> tClass) {
		this.collection = collection;
		this.tClass = tClass;
		this.client = client;
        cache = Caching.getCachingProvider().getCacheManager().createCache(collection,getCacheConfig());
        loadCache();
	}

	protected Client getClient() {
		return client;
	}

	protected String getCollection() {
		return collection;
	}

	protected Class<T> getEntityClass() {
		return tClass;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public Optional<T> findOne(final String key) {
		KvObject<T> categoryKvObject =
				client.kv(collection, key)
						.get(tClass)
						.get();

		if (categoryKvObject == null) {
			return Optional.empty();
		}

		return Optional.of(categoryKvObject.getValue(tClass));
	}

	@Override
	public Stream<T> findAll() {
		return StreamSupport.stream(client.listCollection(collection)
				.limit(limit)
				.get(tClass)
				.get().spliterator(), false)
				.map(entryKvObject -> entryKvObject.getValue(tClass));
	}

	@Override
	public void save(final T entity) {
		client.kv(collection, entity.getKey())
				.put(entity)
				.get();
	}

	@Override
	public void delete(final String key) {
		client.kv(collection, key)
				.delete(true)
				.get();
	}

    @SuppressWarnings("unchecked")
    private void loadCache() {
        MutableConfiguration<String,Entity> conf = cache.getConfiguration(MutableConfiguration.class);
        conf.setWriteThrough(false);
        StreamSupport.stream(client.listCollection(collection)
                .limit(limit)
                .get(tClass)
                .get().spliterator(), false)
                .map(entryKvObject -> entryKvObject.getValue(tClass))
                .forEach(e -> cache.put(e.getKey(), e));
        conf.setWriteThrough(true);
    }

    private MutableConfiguration<String, T> getCacheConfig() {
        MutableConfiguration<String, T> configuration = new MutableConfiguration<>();
        return configuration;
    }
}
