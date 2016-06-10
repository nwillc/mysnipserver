/*
 * Copyright (c) 2016,  nwillc@gmail.com
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

import com.github.nwillc.mysnipserver.MySnipServerApplication;
import com.github.nwillc.mysnipserver.dao.orchestrate.CollectionDao;
import com.github.nwillc.mysnipserver.entity.Category;
import com.github.nwillc.mysnipserver.entity.Snippet;
import com.github.nwillc.mysnipserver.entity.User;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import io.orchestrate.client.Client;
import io.orchestrate.client.OrchestrateClient;
import org.pmw.tinylog.Logger;

public class OrchestrateModule extends AbstractModule {
    private static final String ORCH_API_KEY = System.getenv("ORCH_API_KEY");

    @Override
    protected void configure() {
        Logger.info("Configuring Orchestrate Backed module.");
        Client client = new OrchestrateClient(ORCH_API_KEY);
        bind(new TypeLiteral<MySnipServerApplication>() {
        }).toInstance(new MySnipServerApplication(
                new CollectionDao<>(client, Category.class),
                new CollectionDao<>(client, Snippet.class),
                new CollectionDao<>(client, User.class)));
    }
}
