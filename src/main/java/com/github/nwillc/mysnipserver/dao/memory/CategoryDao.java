package com.github.nwillc.mysnipserver.dao.memory;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Category;

import java.util.Optional;
import java.util.stream.Stream;

public class CategoryDao implements Dao<Category> {

	@Override
	public void delete(String s) {

	}

	@Override
	public Optional<Category> findOne(String s) {
		return null;
	}

	@Override
	public Stream<Category> findAll() {
		return Stream.of(new Category("Java"), new Category("Shell Script"));
	}

	@Override
	public void save(Category category) {

	}
}
