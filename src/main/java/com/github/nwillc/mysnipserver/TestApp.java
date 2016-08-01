package com.github.nwillc.mysnipserver;

import org.pmw.tinylog.Logger;
import spark.servlet.SparkApplication;

import static spark.Spark.get;

public class TestApp implements SparkApplication {
    @Override
    public void init() {
        Logger.info("Starting TestApp");
        get("/ping", (request, response) -> "PONG");
    }
}