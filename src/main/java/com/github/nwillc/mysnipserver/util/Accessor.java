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


import org.pmw.tinylog.Logger;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * A utility to convert Reflection access to a function.
 *
 * @since 2.5.0
 */
public final class Accessor {
    private Accessor() {
    }

    /**
     * Create a Function from a instance variable name that returns it's value in a class.
     *
     * @param fieldName the instance variable name
     * @param clz       the class
     * @param <T>       the instance type of the argument to the function
     * @param <R>       the return type of the function/
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> getFunction(final String fieldName, final Class<T> clz) {

        Field f = null;
        Class<?> tmpClass = clz;
        do {
            try {
                f = tmpClass.getDeclaredField(fieldName);
                f.setAccessible(true);
                break;
            } catch (NoSuchFieldException e) {
                tmpClass = tmpClass.getSuperclass();
            }
        } while (tmpClass != null);

        if (f == null) {
            return null;
        }
        final Field field = f;
        return t -> {
            try {
                return (R) field.get(t);
            } catch (IllegalAccessException e) {
                Logger.error("Can not access field: " + clz.getName() + '.' + fieldName, e);
            }
            return null;
        };
    }
}