package com.github.nwillc.mysnipserver.dao.memory;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.myorchsnip.dao.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MemoryBackedDao<T extends Entity> implements Dao<T> {
	private final Map<String, T> entities = new HashMap<>();

	@Override
	public void delete(String s) {
		entities.remove(s);
	}

	@Override
	public Optional<T> findOne(String s) {
		return Optional.ofNullable(entities.get(s));
	}

	@Override
	public Stream<T> findAll() {
		return entities.values().stream();
	}

	@Override
	public void save(T t) {
		entities.put(t.getKey(), t);
	}
}
