package org.springframework.samples.petclinic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ApiTest {

	private Connection connection;

	@BeforeEach
	public void connect() throws SQLException {
		connection = DriverManager.getConnection(
			"jdbc:postgresql://localhost/petclinic",
			"petclinic",
			"petclinic"
		);
		}

	@AfterEach
	public void disconnect() throws SQLException {
		connection.close();
	}


	//TO DO написать тесты, не успел из-за того что не смог запустить базовые тесты
	@Test
	public void shouldProcessCreationFormOwners() {
		given()
			.contentType("application/json")
			.body(String.format("{\n" +
				"  \"address\": \": qwerty,\n" +
				"  \"city\": \": qwerty,\n" +
				"  \"firstName\": \": qwerty,\n" +
				"  \"id\": \": \": qwerty,\n" +
				"  \"id\": \": \": qwerty,\n" +

				"  \"countryName\": \"%s\"\n" +
				"}"))
			.when()
			.put(String.format("/owners))
			.then()
			.statusCode(200);
	}


}


