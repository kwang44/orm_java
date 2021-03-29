package com.korm.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface KormConnectable {
    public Connection getConnection() throws SQLException;
}
