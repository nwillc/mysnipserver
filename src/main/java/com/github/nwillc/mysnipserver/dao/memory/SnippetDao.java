package com.github.nwillc.mysnipserver.dao.memory;

import com.github.nwillc.mysnipserver.entity.Snippet;

public class SnippetDao extends MemoryBackedDao<Snippet> {
	public SnippetDao() {
		save(new Snippet("Java", "import", "This is an import"));
		save(new Snippet("Shell Script", "shebang", "#!/bin/bash"));
		save(new Snippet("Shell Script", "export variable", "# an export\nexport X=foo"));
	}
}
