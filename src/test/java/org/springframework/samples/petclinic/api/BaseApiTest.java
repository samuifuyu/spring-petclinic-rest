package org.springframework.samples.petclinic.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract class BaseApiTest {

	static Connection connection;
	static DatabaseTestClient db;

	@BeforeAll
	public static void setUpErrorLogging() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}

	@BeforeAll
	protected static void connect() throws SQLException {
		connection = DriverManager.getConnection(
			"jdbc:postgresql://localhost:5432/petclinic",
			"petclinic",
			"petclinic"
		);

		db = new DatabaseTestClient(connection);
	}

	@AfterAll
	protected static void disconnect() throws SQLException {
		connection.close();
	}

	public String generateStringFromResource(String path) throws IOException {
		return new String(Files.readAllBytes(Paths.get(path)));
	}
}
