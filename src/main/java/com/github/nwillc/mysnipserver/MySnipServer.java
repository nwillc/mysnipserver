/*
 * Copyright (c) 2016, nwillc@gmail.com
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
 *
 */

package com.github.nwillc.mysnipserver;

import com.github.nwillc.mysnipserver.util.guice.MemoryBackedModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.pmw.tinylog.Logger;

import java.io.IOException;

import static spark.Spark.ipAddress;
import static spark.Spark.port;

public final class MySnipServer {

    public static void main(String[] args) throws IOException {

        Module module = null;
        boolean auth = true;

        final OptionParser parser = CliOptions.getOptions();

        final OptionSet options = parser.parse(args);

        if (options.has(CliOptions.CLI.help.name())) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

        if (options.has(CliOptions.CLI.noauth.name())) {
            Logger.warn("Disabling authentication.");
            auth = false;
        }

        Integer port = (Integer) options.valueOf(CliOptions.CLI.port.name());
        Logger.info("Configuring port: " + port);
        port(port);

        String address = (String) options.valueOf(CliOptions.CLI.address.name());
        Logger.info("Configuring address: " + address);
        ipAddress(address);

        String store = (String) options.valueOf(CliOptions.CLI.store.name());
        Logger.info("Configuring store: " + store);
        try {
            module = (Module) Class.forName(MemoryBackedModule.class.getPackage().getName()
                    + '.' + store + "Module").newInstance();
        } catch (Exception e) {
            Logger.error("Failed loading store: " + store);
            System.exit(1);
        }

        Logger.info("Starting");

        MySnipServerApplication application = Guice.createInjector(module).getInstance(MySnipServerApplication.class);
        application.setAuth(auth);
        application.init();
        Logger.info("Completed");
    }
}
