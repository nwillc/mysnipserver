package com.github.nwillc.mysnipserver.dao;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.myorchsnip.entity.Snippet;

import java.util.Optional;
import java.util.stream.Stream;

public class SnippetDao implements Dao<Snippet> {
	@Override
	public void delete(String s) {

	}

	@Override
	public Optional<Snippet> findOne(String s) {
		return Optional.empty();
	}

	@Override
	public Stream<Snippet> findAll() {
		return null;
	}

	@Override
	public void save(Snippet snippet) {

	}
}
