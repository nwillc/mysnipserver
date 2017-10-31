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
import com.github.nwillc.funjdbc.functions.Extractor;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.opa.impl.jdbc.JdbcDaoConfiguration;
import com.github.nwillc.opa.impl.jdbc.SqlEntry;

import java.sql.Connection;
import java.sql.SQLException;

public class CategoryConfiguration implements JdbcDaoConfiguration<String, Category> {
    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public Extractor<Category> getExtractor() {
        return null;
    }

    @Override
    public SqlStatement getQueryAll() {
        return null;
    }

    @Override
    public SqlEntry<Category> getCreate() {
        return null;
    }

    @Override
    public SqlEntry<String> getRetrieve() {
        return null;
    }

    @Override
    public SqlEntry<Category> getUpdate() {
        return null;
    }

    @Override
    public SqlEntry<String> getDelete() {
        return null;
    }
}
