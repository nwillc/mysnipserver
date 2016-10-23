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

package com.github.nwillc.mysnipserver.entity;

import org.junit.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static com.github.nwillc.mysnipserver.entity.SnippetPredicate.Field.*;

public class SnippetPredicateTest {
   private Snippet snippet = new Snippet("1", "a title", "a body");

	@Test
	public void shouldMatchCategory() throws Exception {
		SnippetPredicate predicate = new SnippetPredicate(category, "1");

		assertThat(predicate.test(snippet)).isTrue();
	}

	@Test
	public void shouldNotMatchCategory() throws Exception {
		SnippetPredicate predicate = new SnippetPredicate(category, "2");

		assertThat(predicate.test(snippet)).isFalse();
	}

	@Test
	public void shouldMatchTitle() throws Exception {
		SnippetPredicate predicate = new SnippetPredicate(title, "title");

		assertThat(predicate.test(snippet)).isTrue();
	}

	@Test
	public void shouldNotMatchTitle() throws Exception {
		SnippetPredicate predicate = new SnippetPredicate(title, "body");

		assertThat(predicate.test(snippet)).isFalse();
	}

	@Test
	public void shouldMatchBody() throws Exception {
		SnippetPredicate predicate = new SnippetPredicate(body, "body");

		assertThat(predicate.test(snippet)).isTrue();
	}

	@Test
	public void shouldNotMatchBody() throws Exception {
		SnippetPredicate predicate = new SnippetPredicate(body, "title");

		assertThat(predicate.test(snippet)).isFalse();
	}

	@Test
	public void shouldNegate() throws Exception {
		Predicate<Snippet> predicate = new SnippetPredicate(body, "title").negate();

		assertThat(predicate.test(snippet)).isTrue();

	}

	@Test
	public void shouldOr() throws Exception {
		Predicate<Snippet> predicate = new SnippetPredicate(category, "1")
				.or(new SnippetPredicate(body, "title"));

		assertThat(predicate.test(snippet)).isTrue();
	}

	@Test
	public void shouldAnd() throws Exception {
		Predicate<Snippet> predicate = new SnippetPredicate(category, "1")
				.and(new SnippetPredicate(body, "body"));

		assertThat(predicate.test(snippet)).isTrue();
	}

	@Test
	public void shouldAndFail() throws Exception {
		Predicate<Snippet> predicate = new SnippetPredicate(category, "1")
				.and(new SnippetPredicate(body, "title"));

		assertThat(predicate.test(snippet)).isFalse();
	}
}