/*
 * Copyright (c) 2015,  nwillc@gmail.com
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

import com.github.nwillc.mysnipserver.util.guice.MemoryBackedModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.github.nwillc.mysnipserver.CommandLineInterface.CLI;
import static spark.Spark.ipAddress;
import static spark.Spark.port;

public class MySnipServer {
    private static final String LOG_CONFIG = "/logging.properties";
    private final static Logger LOGGER = Logger.getLogger(MySnipServer.class.getSimpleName());

    static {
        try (InputStream in = MySnipServer.class.getResourceAsStream(LOG_CONFIG)) {
            if (in == null) {
                System.err.println("Could not open " + LOG_CONFIG);
            }
            LogManager.getLogManager().readConfiguration(in);
        } catch (IOException e) {
            System.err.println("Failed reading " + LOG_CONFIG);
        }
    }

    public static void main(String[] args) {
        LOGGER.info("Starting");

        Options options = CommandLineInterface.getOptions();
        CommandLineParser commandLineParser = new DefaultParser();

        Module module = null;

        try {
            CommandLine commandLine = commandLineParser.parse(options, args);

            if (commandLine.hasOption(CLI.help.name())) {
                CommandLineInterface.help(options, 0);
            }

            if (commandLine.hasOption(CLI.port.name())) {
                LOGGER.info("Configuring port: " + commandLine.getOptionValue(CLI.port.name()));
                port(Integer.parseInt(commandLine.getOptionValue(CLI.port.name())));
            }

            if (commandLine.hasOption(CLI.address.name())) {
                LOGGER.info("Configuring address: " + commandLine.getOptionValue(CLI.address.name()));
                ipAddress(commandLine.getOptionValue(CLI.address.name()));
            }

            if (commandLine.hasOption(CLI.store.name())) {
                module = (Module) Class.forName(MemoryBackedModule.class.getPackage().getName() + "." +
                        commandLine.getOptionValue(CLI.store.name()) + "Module").newInstance();
            } else {
                module = new MemoryBackedModule();
            }

        } catch (ParseException e) {
            LOGGER.severe("Failed to parse command line: " + e);
            CommandLineInterface.help(options, 1);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            LOGGER.severe("Failed instantiating DAO class: " + e);
            CommandLineInterface.help(options, 1);
        }

        Guice.createInjector(module).getInstance(MySnipServerApplication.class).init();
        LOGGER.info("Completed");
    }
}
