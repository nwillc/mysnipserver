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

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Support for command line processing.
 */
public final class CommandLineInterface {
    private CommandLineInterface() {
    }

    public static void help(Options options, int status) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java " + MySnipServer.class.getCanonicalName(), options, true);
        System.exit(status);
    }

    public static Options getOptions() {
        Option option;

        Options options = new Options();

        option = new Option(CLI.help.name().substring(0, 1), CLI.help.name(), false, "Get command line help.");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(CLI.address.name().substring(0, 1), CLI.address.name(), true, "IP address to listen on.");
        option.setArgName(CLI.address.name());
        option.setArgs(1);
        option.setType(String.class);
        option.setRequired(false);
        options.addOption(option);

        option = new Option(CLI.port.name().substring(0, 1), CLI.port.name(), true, "Port number to listen on.");
        option.setArgName(CLI.port.name());
        option.setArgs(1);
        option.setType(Integer.class);
        option.setRequired(false);
        options.addOption(option);

        option = new Option(CLI.noauth.name().substring(0, 1), CLI.noauth.name(), false, "Should the server require authentication");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(CLI.store.name().substring(0, 1), CLI.store.name(), true,
                "Storage mechanism, MemoryBacked or Orchestrate.");
        option.setArgName(CLI.store.name());
        option.setArgs(1);
        option.setType(String.class);
        option.setRequired(false);
        options.addOption(option);

        return options;
    }

    enum CLI {
        address,
        help,
        noauth,
        port,
        store
    }
}
