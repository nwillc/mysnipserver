

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

import joptsimple.OptionParser;

final public class CliOptions {
    public enum CLI {
        address,
        help,
        noauth,
        port,
        store
    }

    private CliOptions() {
    }

    public static OptionParser getOptions() {
        OptionParser parser = new OptionParser(true);
        parser.accepts(CLI.help.name(), "Get command line help.");
        parser.accepts(CLI.address.name(), "IP address to listen on.")
                .withRequiredArg().describedAs("ip_address").defaultsTo("0.0.0.0");
        parser.accepts(CLI.port.name(), "Port number to listen on.")
                .withRequiredArg().ofType(Integer.class).describedAs("port_no").defaultsTo(4567);
        parser.accepts(CLI.noauth.name(), "Make server not require authentication.");
        parser.accepts(CLI.store.name(), "Storage mechanism, MemoryBacked, or MongoDb.")
                .withRequiredArg().describedAs("store_name").defaultsTo("MemoryBacked");
        return parser;
    }
}
