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

package com.github.nwillc.mysnipserver.dao.h2;

import com.github.nwillc.funjdbc.functions.ConnectionProvider;
import com.github.nwillc.funjdbc.migrate.Manager;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.pmw.tinylog.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public final class H2Database implements ConnectionProvider {
    private final static String DRIVER = "org.h2.Driver";
    private final static String URL = "jdbc:h2:";
    private final BoneCP connectionPool;
    private final Manager manager;

    public H2Database(String dbName) throws ClassNotFoundException, SQLException {
        Logger.info("H2 Database: " + dbName);
        Class.forName(DRIVER);
        BoneCPConfig config = new BoneCPConfig();
        config.setUsername("sa");
        config.setPassword("sa");
        config.setJdbcUrl(URL + dbName);
        connectionPool = new BoneCP(config);
        manager = Manager.getInstance();
        manager.setConnectionProvider(this);
        if (!manager.migrationsEnabled()) {
            manager.enableMigrations();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    public Manager getManager() {
        return manager;
    }
}
