/*
 * Copyright (c) 2017, nwillc@gmail.com
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

package com.github.nwillc.mysnipserver.dao.h2;

import com.github.nwillc.funjdbc.SqlStatement;
import com.github.nwillc.funjdbc.functions.ConnectionProvider;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.funjdbc.migrate.MigrationBase;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.opa.impl.jdbc.JdbcDaoConfiguration;
import com.github.nwillc.opa.impl.jdbc.SqlEntry;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SnippetConfiguration implements JdbcDaoConfiguration<String, Snippet> {
    private final ConnectionProvider delegate;

    public SnippetConfiguration(H2Database delegate) {
        this.delegate = delegate;
        delegate.getManager().add(new CreateMigration());
        delegate.getManager().doMigrations();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    public Extractor<Snippet> getExtractor() {
        return rs -> {
            final Snippet snippet = new Snippet();
            snippet.setKey(rs.getString("key"));
            snippet.setCategory(rs.getString("category"));
            snippet.setTitle(rs.getString("title"));
            snippet.setBody(rs.getString("body"));
            return snippet;
        };
    }

    @Override
    public SqlStatement getQueryAll() {
        return new SqlStatement("SELECT DISTINCT * FROM Snippet");
    }


    @Override
    public SqlEntry<Snippet> getCreate() {
        return snippet -> new SqlStatement("INSERT INTO Snippet (key, category, title, body) VALUES ('%s', '%s', '%s', '%s')",
                snippet.getKey(), snippet.getCategory(), snippet.getTitle(), snippet.getBody());
    }

    @Override
    public SqlEntry<Snippet> getUpdate() {
        return snippet -> new SqlStatement("UPDATE Snippet SET category = '%s', title = '%s', body = '%s' WHERE key = '%s'",
                snippet.getCategory(), snippet.getTitle(), snippet.getBody(), snippet.getKey());
    }

    @Override
    public SqlEntry<String> getDelete() {
        return key -> new SqlStatement("DELETE FROM Snippet WHERE key = '%s'", key);
    }

    static class CreateMigration extends MigrationBase {
        @Override
        public String getDescription() {
            return "Create Snippet Table";
        }

        @Override
        public String getIdentifier() {
            return "snippet-1";
        }

        @Override
        public void perform() throws Exception {
            try (Connection c = getConnection();
                 Statement statement = c.createStatement()) {
                statement.execute("CREATE TABLE Snippet ( key CHAR(40), category VARCHAR(200), title VARCHAR(200), body VARCHAR(1024), PRIMARY KEY(key) )");
            }
        }
    }
}
