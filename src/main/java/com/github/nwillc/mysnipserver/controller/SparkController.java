/*
 * Copyright (c) 2015,  nwillc@gmail.com
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

package com.github.nwillc.mysnipserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.nwillc.mysnipserver.dao.Dao;
import com.github.nwillc.mysnipserver.dao.Entity;
import com.github.nwillc.mysnipserver.util.rest.Version;
import spark.Route;
import spark.Spark;

/**
 * Isolate as much Spark specific code here as possible.
 */
public abstract class SparkController<T extends Entity> implements Version {
    private final Dao<T> dao;
    private final ThreadLocal<ObjectMapper> mapper;

    public SparkController(Dao<T> dao) {
        this.dao = dao;
        mapper = ThreadLocal.withInitial(() -> new ObjectMapper().registerModule(new Jdk8Module()));
    }

    protected Dao<T> getDao() {
        return dao;
    }

    public ThreadLocal<ObjectMapper> getMapper() {
        return mapper;
    }

    protected void get(String path, Route route) {
        Spark.get(versionedPath(path), route, this::toJson);
    }

    protected void post(String path, Route route) {
        Spark.post(versionedPath(path), route, this::toJson);
    }

    protected void delete(String path, Route route) {
        Spark.delete(versionedPath(path), route, this::toJson);
    }

    private String toJson(Object obj) {
        try {
            return mapper.get().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON", e);
        }
    }
}
