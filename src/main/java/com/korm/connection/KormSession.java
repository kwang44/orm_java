package com.korm.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class KormSession implements KormConnectable{
    Connection connection;

    public KormSession(KormConnectionManager connectionManager) throws SQLException {
        this.connection = connectionManager.getConnection();
        this.connection.setAutoCommit(false);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollBack() throws SQLException {
        connection.rollback();
    }

    public void rollBack(Savepoint savePoint) throws SQLException {
        connection.rollback(savePoint);
    }

    public void setTransactionIsolation(int level) throws SQLException {
        this.connection.setTransactionIsolation(level);
    }

    public Savepoint setSavepoint() throws SQLException {
        return this.connection.setSavepoint();
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.connection.releaseSavepoint(savepoint);
    }

    public void close() throws SQLException {
        connection.close();
    }

}
