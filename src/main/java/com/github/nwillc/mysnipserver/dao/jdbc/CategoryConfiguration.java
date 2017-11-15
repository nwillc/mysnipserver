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

package com.github.nwillc.mysnipserver.dao.jdbc;

import com.github.nwillc.funjdbc.SqlStatement;
import com.github.nwillc.funjdbc.functions.ConnectionProvider;
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.funjdbc.migrate.MigrationBase;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.opa.impl.jdbc.JdbcDaoConfiguration;
import com.github.nwillc.opa.impl.jdbc.SqlEntry;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CategoryConfiguration implements JdbcDaoConfiguration<String, Category> {
    private final ConnectionProvider delegate;

    public CategoryConfiguration(JdbcDatabase delegate) {
        this.delegate = delegate;
        delegate.getManager().add(new CreateMigration());
        delegate.getManager().doMigrations();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    @Override
    public Extractor<Category> getExtractor() {
        return rs -> {
            final Category category = new Category();
            category.setKey(rs.getString("key"));
            category.setName(rs.getString("name"));
            return category;
        };
    }

    @Override
    public SqlStatement getQueryAll() {
        return new SqlStatement("SELECT DISTINCT * FROM Category");
    }

    @Override
    public SqlEntry<Category> getCreate() {
        return category -> new SqlStatement("INSERT INTO Category (key, name) VALUES ('%s', '%s')", category.getKey(), category.getName());
    }

    @Override
    public SqlEntry<Category> getUpdate() {
        return category -> new SqlStatement("UPDATE Category SET name = '%s' WHERE key = '%s'", category.getName(), category.getKey());
    }

    @Override
    public SqlEntry<String> getDelete() {
        return key -> new SqlStatement("DELETE FROM Category WHERE key = '%s'", key);
    }

    private static class CreateMigration extends MigrationBase {
        @Override
        public String getDescription() {
            return "Create Category Table";
        }

        @Override
        public String getIdentifier() {
            return "category-1";
        }

        @Override
        public void perform() throws Exception {
            try (Connection c = getConnection();
                 Statement statement = c.createStatement()) {
                statement.execute("CREATE TABLE Category ( key CHAR(40), name VARCHAR(200), PRIMARY KEY(key) )");
            }
        }
    }
}
