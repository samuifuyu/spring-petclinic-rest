package org.springframework.samples.petclinic.api;

import java.sql.*;

public class DatabaseTestClient {
	Connection connection;

	DatabaseTestClient(Connection connection) {
		this.connection = connection;
	}

	public Long insert(String table, String column, String value) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			String.format("INSERT INTO %s(%s) VALUES(?)", table, column),
			Statement.RETURN_GENERATED_KEYS
		);
		statement.setString(1, value);

		statement.executeUpdate();
		ResultSet keys = statement.getGeneratedKeys();
		keys.next();
		return keys.getLong(1);
	}

	public void delete(String table, Long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			String.format("DELETE from %s where id = ?", table)
		);
		statement.setLong(1, id);
		statement.executeUpdate();
	}

	public String selectById(String table, Long id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			String.format("SELECT * from %s where id = ?", table)
		);
		statement.setLong(1, id);
		ResultSet row = statement.executeQuery();
		if (row.next()) return row.getString(2);
		else return null;
	}

	public ResultSet sqlRequest(String request) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(request);
		ResultSet result = statement.executeQuery();
		if (result.next()) return result;
		else return null;
	}

	public void sqlRequestUpdate(String request) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(request);
		statement.executeUpdate();
	}
}
