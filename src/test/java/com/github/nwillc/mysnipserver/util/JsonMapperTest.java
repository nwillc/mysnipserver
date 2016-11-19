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

package com.github.nwillc.mysnipserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonMapperTest implements JsonMapper {

	@Test
	public void testGetMapper() throws Exception {
		final ObjectMapper mapper = getMapper();
		assertThat(mapper).isNotNull().isInstanceOf(ObjectMapper.class);
	}

	@Test
	public void testToJsonException() throws Exception {
		final Sample sample = mock(Sample.class);
		when(sample.getNumber()).thenThrow(JsonProcessingException.class);
		assertThatThrownBy(() -> toJson(sample)).isInstanceOf(RuntimeException.class).hasMessageContaining("JSON generation");
	}

	@Test
	public void testToJson() throws Exception {
		Sample sample = new Sample(1L, "one", true, 1, 2, 3);

		assertThat(sample).isNotNull();
		assertThat(toJson(sample)).isEqualTo("{\"number\":1,\"str\":\"one\",\"flag\":true,\"dead\":null,\"bits\":[1,2,3]}");
	}

	@Test
	public void testFromJson() throws Exception {
		Simple simple = fromJson("{ \"name\": \"foo\", \"value\": \"bar\" }", Simple.class);
		assertThat(simple).isNotNull();
		assertThat(simple.name).isEqualTo("foo");
		assertThat(simple.value).isEqualTo("bar");
	}

	static class Simple {
		public String name;
		public String value;
	}

	static class Sample {
		final private Long number;
		final public String str;
		final public Boolean flag;
		final public Object dead = null;
		final public int[] bits;

		public Sample(Long number, String str, Boolean flag, int... bits) {
			this.number = number;
			this.str = str;
			this.flag = flag;
			this.bits = bits;
		}

		public Long getNumber() {
			return number;
		}
	}
}
