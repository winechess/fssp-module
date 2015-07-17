package com.impulsm.fssp.models.documents.extdoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vinichenkosa on 16.07.15.
 */
public class ExtDocCursor implements AutoCloseable {

    private Connection connection;
    private CallableStatement statement;
    private ResultSet cursor;

    @Override
    public void close() throws Exception {
        if (cursor != null)
            try {
                cursor.close();
            } catch (Exception ex) {
                logger.error("Не удалось закрыть курсор.");
            }
        if (statement != null) try {
            statement.close();
        } catch (Exception ex) {
            logger.error("Не удалось закрыть statement.");
        }
        if (connection != null) try {
            connection.close();
        } catch (Exception ex) {
            logger.error("Не удалось закрыть соединение.");
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setStatement(CallableStatement statement) {
        this.statement = statement;
    }

    public void setCursor(ResultSet cursor) {
        this.cursor = cursor;
    }

    public ResultSet getCursor() {
        return cursor;
    }

    public boolean next() throws SQLException {
        return cursor.next();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
}
