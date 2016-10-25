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

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This Predicate has a dual purpose. First it implements a traditional predicate to test if a Snippet matches a given
 * field/pattern pair. Second it's toString will generate the Lucene query matching the logic of the Predicate. The field
 * matching follows the following logic, key fields must match the pattern, text fields must contain the pattern.
 */
public class SnippetPredicate implements Predicate<Snippet> {
    public enum Field {
        key((s,p) -> s.getKey().equals(p)),
        category((s, p) -> s.getCategory().equals(p)),
        title((s, p) -> s.getTitle().contains(p)),
        body((s, p) -> s.getBody().contains(p));

        final BiFunction<Snippet, String, Boolean> match;

        Field(BiFunction<Snippet, String, Boolean> match) {
            this.match = match;
        }
    }

    private final Field field;
    private final String pattern;
    private final Function<Snippet, Boolean> test;
    private final Function<SnippetPredicate, String> toString;


    private SnippetPredicate(Field field, String pattern, Function<Snippet, Boolean> test, Function<SnippetPredicate, String> toString) {
        this.field = field;
        this.pattern = pattern;
        this.toString = toString;
        this.test = test;
    }

    public SnippetPredicate(final Field field, final String pattern) {
        this(field, pattern,
                snippet -> field.match.apply(snippet, pattern),
                snippetPredicate -> snippetPredicate.field.name() + ":\"" + snippetPredicate.pattern + '"'
        );
    }

    public SnippetPredicate negate() {
        return new SnippetPredicate(field, pattern,
                snippet -> !test.apply(snippet), s -> "NOT " + toString.apply(this)
        );
    }

    public SnippetPredicate group() {
        return new SnippetPredicate(field, pattern,
                test, s -> "( " + toString.apply(this) + " )"
        );
    }

    public SnippetPredicate and(final SnippetPredicate other) {
        return new SnippetPredicate(field, pattern,
                snippet -> test.apply(snippet) && other.test(snippet),
                s -> toString.apply(this) + " AND " + other.toString()
        );
    }

    public SnippetPredicate or(final SnippetPredicate other) {
        return new SnippetPredicate(field, pattern,
                snippet -> test.apply(snippet) || other.test(snippet),
                s -> toString.apply(this) + " OR " + other.toString()
        );
    }

    @Override
    public boolean test(final Snippet snippet) {
        return test.apply(snippet);
    }

    @Override
    public String toString() {
        return toString.apply(this);
    }
}
