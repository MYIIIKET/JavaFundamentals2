package com.epam.trainings.java.fundamentals.jdbc.cp;import java.sql.Connection;import java.sql.SQLException;import java.util.concurrent.BlockingQueue;public interface PooledConnection extends ConnectionWrapper {    BlockingQueue<Connection> getConnectionQueue();    static PooledConnection create(Connection connection,                            BlockingQueue<Connection> connectionQueue) throws SQLException {        connection.setAutoCommit(true);        return new PooledConnection() {            @Override            public BlockingQueue<Connection> getConnectionQueue() {                return connectionQueue;            }            @Override            public Connection toSrc() {                return connection;            }        };    }    default void reallyClose() throws SQLException {        toSrc().close();    }    @Override    default void close() throws SQLException {        if (toSrc().isClosed())            throw new SQLException("Attempting to close closed connection.");        if (toSrc().isReadOnly())            toSrc().setReadOnly(false);        if (!getConnectionQueue().offer(this))            throw new SQLException("Error allocating connection in the pool.");    }}