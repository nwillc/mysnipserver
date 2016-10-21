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

requirejs.config({
    shim: {
        "jquery": {
            exports: "$"
        },
        "jquery-ui": {
            deps: ["jquery"]
        },
        "gapi": {
            exports: "gapi"
        }
    },
    paths: {
        "jquery": "http://code.jquery.com/jquery-3.1.1.min",
        "jquery-ui": "http://code.jquery.com/ui/1.12.1/jquery-ui.min",
        "gapi": "https://apis.google.com/js/platform"
    }
});

define(["gapi"], function (gapi) {
    gapi.load("auth2", function () {
        gapi.auth2.init({
            client_id: "728919834589-6e41p6kek58pe4honddltevel30cusuo.apps.googleusercontent.com"
        });
    });
});