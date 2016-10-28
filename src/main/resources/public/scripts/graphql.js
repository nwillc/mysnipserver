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

define(['jquery'], function ($) {
    return {
        Graphql: function (url, query) {
            "use strict";
            var _this = this;
            this.url = url;
            this.query = query;
            this.variables = {};

            this.toString = function () {
                return JSON.stringify(_this);
            };

            this.execute = function (consumer) {
                console.log("GraphQL Request: " + _this);
                $.ajax({
                    url: _this.url,
                    async: true,
                    method: "POST",
                    contentType: "application/json",
                    data: _this.toString(),
                    dataType: "json",
                    success: function (response) {
                        if (response.errors != undefined) {
                            console.log("GraphQL Errors: " + JSON.stringify(response.errors));
                        }
                        consumer(response);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log("Request failed: " + textStatus);
                        console.log(errorThrown);
                    }
                });
            };
        }
    }
});
