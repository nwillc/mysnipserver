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

package com.github.nwillc.mysnipserver.util.http;

import static java.net.HttpURLConnection.*;

public enum HttpStatusCode {
    OK(HTTP_OK),
    CREATED(HTTP_CREATED),
    UNAUTHORIZED(HTTP_UNAUTHORIZED),
    NOT_FOUND(HTTP_NOT_FOUND),
    INTERNAL_SERVER_ERROR(HTTP_INTERNAL_ERROR),
    BAD_REQUEST(HTTP_BAD_REQUEST);

    public final int code;

    HttpStatusCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return name() + " (" + code + ')';
    }
}
