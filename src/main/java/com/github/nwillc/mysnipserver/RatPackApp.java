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

package com.github.nwillc.mysnipserver;

import com.github.nwillc.mysnipserver.handlers.GraphQLHandler;
import com.github.nwillc.mysnipserver.util.guice.MemoryBackedModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.pmw.tinylog.Logger;
import ratpack.func.Action;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfigBuilder;
import ratpack.session.SessionModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class RatPackApp {
    private static final String PUBLIC = "public";
    private final Integer port;
    private final InetAddress address;
    private final GraphQLHandler graphQLHandler;


    public static void main(final String... args) throws Exception {
        new RatPackApp(args).start();
    }

    private RatPackApp(final String... args) throws Exception {
        final OptionParser parser = CliOptions.getOptions();

        Logger.info("Processing Args: " + Arrays.toString(args));
        final OptionSet options = parser.parse(args);

        if (options.has(CliOptions.CLI.help.name())) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        port = (Integer) options.valueOf(CliOptions.CLI.port.name());
        Logger.info("Using port: " + port);
        address = InetAddress.getByName((String) options.valueOf(CliOptions.CLI.address.name()));
        Logger.info("Using address: " + address);
        final String store = (String) options.valueOf(CliOptions.CLI.store.name());
        Logger.info("Configuring store: " + store);
        final Module module = (Module) Class.forName(MemoryBackedModule.class.getPackage().getName()
                + '.' + store + "Module").newInstance();

        final Injector injector = Guice.createInjector(module);
        graphQLHandler = injector.getInstance(GraphQLHandler.class);
    }

    private void start() throws Exception {
        String properties = "";

        try (
                final InputStreamReader isr = new InputStreamReader(RatPackApp.class.getResourceAsStream("/build.json"));
                final BufferedReader bufferedReader = new BufferedReader(isr)
        ) {
            properties = bufferedReader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            Logger.warn("Could not load build info", e);
        }

        final String props = properties;

        final RatpackServer server = RatpackServer.of(s -> s
                .registry(ratpack.guice.Guice.registry(b -> b.module(SessionModule.class)))
                .serverConfig(config())
                .handlers(chain -> chain
                        .get("ping", ctx -> ctx.render("PONG"))
                        .get("properties", ctx -> ctx.render(props))
                        .post(GraphQLHandler.PATH, graphQLHandler)
                        .files(f -> f.dir(PUBLIC).indexFiles("index.html"))
                )
        );
        server.start();
    }

    private Action<ServerConfigBuilder> config() {
        final Path baseDir = BaseDir.find(PUBLIC);

        return Action.from(c -> c
                .port(port)
                .address(address)
                .baseDir(baseDir)
        );
    }


}