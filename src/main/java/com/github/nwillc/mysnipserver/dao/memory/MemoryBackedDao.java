package com.github.nwillc.mysnipserver.dao.memory;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.myorchsnip.dao.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MemoryBackedDao<T extends Entity> implements Dao<T> {
	private final List<T> entities = new ArrayList<>();

	@Override
	public void delete(String s) {

	}

	@Override
	public Optional<T> findOne(String s) {
		return findAll().filter(t -> t.getKey().equals(s)).findFirst();
	}

	@Override
	public Stream<T> findAll() {
		return entities.stream();
	}

	@Override
	public void save(T t) {
		entities.add(t);
	}
}
