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

package com.github.nwillc.mysnipserver.dao.jdbc;

import com.github.nwillc.funjdbc.functions.ConnectionProvider;
import com.github.nwillc.funjdbc.migrate.Manager;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.pmw.tinylog.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class JdbcDatabase implements ConnectionProvider {
    private final static String DEFAULT_DRIVER = "org.h2.Driver";
    private final static String DEFAULT_URL = "jdbc:h2:";
    private final DataSource dataSource;
    private final Manager manager;

    public JdbcDatabase(String dbName) throws ClassNotFoundException, SQLException {
             this(DEFAULT_DRIVER, DEFAULT_URL, dbName);
    }
    
    public JdbcDatabase(String driver, String url, String dbName) throws ClassNotFoundException, SQLException {
        Logger.info("{} Database {}{}", driver, url, dbName);
        Class.forName(driver);

        dataSource = setupDataSource(url + dbName);

        manager = Manager.getInstance();
        manager.setConnectionProvider(this);
        if (!manager.migrationsEnabled()) {
            manager.enableMigrations();
        }
    }

    private static DataSource setupDataSource(String connectURI) {
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, null);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        return new PoolingDataSource<>(connectionPool);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public Manager getManager() {
        return manager;
    }
}
