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

package com.github.nwillc.mysnipserver.util.guice;

import com.github.nwillc.mysnipserver.dao.memory.CategoryDao;
import com.github.nwillc.mysnipserver.dao.memory.SnippetDao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.entity.User;
import com.github.nwillc.opa.Dao;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import org.pmw.tinylog.Logger;


public class MemoryBackedModule implements Module {

    @Override
    public void configure(Binder binder) {
        Logger.info("DI Module: Memory Backed");
        binder.bind(new TypeLiteral<Dao<String, Category>>() {
        }).to(CategoryDao.class).in(Scopes.SINGLETON);
        binder.bind(new TypeLiteral<Dao<String, Snippet>>() {
        }).to(SnippetDao.class).in(Scopes.SINGLETON);
    }
}
