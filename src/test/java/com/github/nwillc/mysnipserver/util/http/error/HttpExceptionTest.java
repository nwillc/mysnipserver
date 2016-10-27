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

package com.github.nwillc.mysnipserver.util.http.error;

import com.github.nwillc.mysnipserver.util.http.HttpStatusCode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class HttpExceptionTest {
	private final HttpException exception = new HttpException(HttpStatusCode.CREATED, "out of nothing");

	@Test
	public void testCodeConstrucctor() throws Exception {
		final HttpException exception = new HttpException(HttpStatusCode.OK);
		assertThat(exception).isNotNull();
		assertThat(exception.getCode()).isEqualTo(HttpStatusCode.OK);
	}

	@Test
	public void testFullConstrucctor() throws Exception {
		final RuntimeException runtimeException = new RuntimeException();
		final HttpException exception = new HttpException(HttpStatusCode.OK, "foo", runtimeException);
		assertThat(exception).isNotNull();
		assertThat(exception.getCode()).isEqualTo(HttpStatusCode.OK);
		assertThat(exception.getMessage()).isEqualTo("foo");
		assertThat(exception.getCause()).isEqualTo(runtimeException);
	}

	@Test
	public void testGetCode() throws Exception {
		assertThat(exception.getCode()).isEqualTo(HttpStatusCode.CREATED);
	}

	@Test
	public void testToString() throws Exception {
		assertThat(exception.toString()).isEqualTo("HttpException{code=CREATED (201), message= 'out of nothing'}");
	}

}