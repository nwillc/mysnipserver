package com.github.nwillc.mysnipserver.dao.memory;

import com.github.nwillc.myorchsnip.dao.Dao;
import com.github.nwillc.mysnipserver.entity.Snippet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SnippetDao implements Dao<Snippet> {
	private final List<Snippet> snippets = new ArrayList<>();

	public SnippetDao() {
		save(new Snippet("Java", "import", "This is an import"));
		save(new Snippet("Shell Script", "shebang", "#!/bin/bash"));
		save(new Snippet("Shell Script", "export variable", "# an export\nexport X=foo"));
	}

	@Override
	public void delete(String s) {

	}

	@Override
	public Optional<Snippet> findOne(String s) {
		return Optional.empty();
	}

	@Override
	public Stream<Snippet> findAll() {
		return snippets.stream();
	}

	@Override
	public void save(Snippet snippet) {
		snippets.add(snippet);
	}
}
