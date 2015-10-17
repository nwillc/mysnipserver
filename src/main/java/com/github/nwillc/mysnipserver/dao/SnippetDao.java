package com.github.nwillc.mysnipserver.dao;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Snippet;

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
		return Stream.of(new Snippet("Java", "import", "This is an import"),
						new Snippet("Shell Script", "shebang", "#!/bin/bash"));
	}

	@Override
	public void save(Snippet snippet) {

	}
}
