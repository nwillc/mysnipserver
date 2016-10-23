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

import java.util.function.Predicate;

public class SnippetPredicate implements Predicate<Snippet> {
	public enum Field {
		category,
		title,
		body
	}
	private final Field field;
	private final String pattern;

	private SnippetPredicate() {
		this(null,null);
	}

	public SnippetPredicate(Field field, String pattern) {
		this.field = field;
		this.pattern = pattern;
	}

	public SnippetPredicate negate() {
		return new Negated(this);
	}

	public SnippetPredicate group() {
		    return new Grouped(this);
	}

	public SnippetPredicate and(SnippetPredicate other) {
		return new And(this, other);
	}

	public SnippetPredicate or(SnippetPredicate other) {
		return new Or(this, other);
	}

	@Override
	public boolean test(Snippet snippet) {
		switch (field) {
			case category:
				return snippet.getCategory().equals(pattern);
			case title:
				return snippet.getTitle().contains(pattern);
			case body:
				return snippet.getBody().contains(pattern);
		}
		return false;
	}

	@Override
	public String toString() {
		return field.name() + ":\"" + pattern + '"';
	}

	private static class Negated extends SnippetPredicate {
		final SnippetPredicate inside;

		public Negated(SnippetPredicate inside) {
			this.inside = inside;
		}

		@Override
		public boolean test(Snippet snippet) {
			return !inside.test(snippet);
		}

		@Override
		public String toString() {
			return "NOT " + inside.toString();
		}
	}

	private static class Grouped extends SnippetPredicate {
		final SnippetPredicate inside;

		public Grouped(SnippetPredicate inside) {
			this.inside = inside;
		}

		@Override
		public boolean test(Snippet snippet) {
			return inside.test(snippet);
		}

		@Override
		public String toString() {
			return "( " + inside.toString() + " )";
		}
	}

	private static class Or extends SnippetPredicate {
		final SnippetPredicate one;
		final SnippetPredicate two;

		public Or(SnippetPredicate one, SnippetPredicate two) {
			this.one = one;
			this.two = two;
		}

		@Override
		public boolean test(Snippet snippet) {
			return one.test(snippet) || two.test(snippet);
		}

		@Override
		public String toString() {
			return one.toString() + " OR " + two.toString();
		}
	}

	private static class And extends SnippetPredicate {
		final SnippetPredicate one;
		final SnippetPredicate two;

		public And(SnippetPredicate one, SnippetPredicate two) {
			this.one = one;
			this.two = two;
		}

		@Override
		public boolean test(Snippet snippet) {
			return one.test(snippet) && two.test(snippet);
		}

		@Override
		public String toString() {
			return one.toString() + " AND " + two.toString();
		}
	}
}
