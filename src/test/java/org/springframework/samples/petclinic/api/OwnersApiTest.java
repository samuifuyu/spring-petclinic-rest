package org.springframework.samples.petclinic.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OwnersApiTest extends BaseApiTest {

	private final String entity = "owners";

	@Test
	@DisplayName("Get Owner info by last name with GET /owners")
	public void shouldGetOwnerInfoByLastName() throws SQLException {
		final String lastName = "Bond";
		Long id = db.insert(entity, "last_name", lastName);

		Response response = given().queryParam("lastName", lastName).queryParam("page", 1).when().get("/owners").then()
				.extract().response();

		assertEquals(200, response.statusCode());
		assertEquals(lastName, response.jsonPath().getString("lastName[0]"));
		assertEquals(id, response.jsonPath().getLong("id[0]"));

		db.delete(entity, id);
	}

	@Test
	@DisplayName("Get Owner info by id with GET /owners/{ownerId}")
	public void shouldGetOwnerInfoById() throws SQLException {
		final String lastName = "Claus";
		Long id = db.insert(entity, "last_name", lastName);

		Response response = given().pathParam("ownerId", id).when().get("/owners/{ownerId}").then().extract()
				.response();

		assertEquals(200, response.statusCode());
		assertEquals(lastName, response.jsonPath().getString("lastName"));
		assertEquals(id, response.jsonPath().getLong("id"));

		db.delete(entity, id);
	}

	@Test
	@DisplayName("Create Owner with POST /owners")
	public void shouldCreateOwnerInfoByLastName() throws SQLException, IOException {
		Response response = given().header("content-type", "application/json")
				.body(String.format(generateStringFromResource("src/test/resources/body/owner/ownerBody.json"), "Bond"))
				.when().post("/owners").then().extract().response();

		assertEquals(201, response.statusCode());
		assertNotNull(db.selectById(entity, response.jsonPath().getLong("id")));

		db.delete(entity, response.jsonPath().getLong("id"));
	}

	@Test
	@DisplayName("Update Owner with POST /owners/{ownerId}")
	public void shouldUpdateOwnerInfo() throws SQLException, IOException {
		final String lastName = "Claus";
		final String newLastName = "Claus";
		Long id = db.insert(entity, "last_name", lastName);

		Response response = given().header("content-type", "application/json").pathParam("ownerId", id).body(
				String.format(generateStringFromResource("src/test/resources/body/owner/ownerBody.json"), newLastName))
				.when().post("/owners/{ownerId}").then().extract().response();

		assertEquals(201, response.statusCode());
		assertEquals(db.sqlRequest(String.format("SELECT * from owners where id = %s", id)).getString("last_name"),
				response.jsonPath().getString("lastName"));

		db.delete(entity, id);
	}

}
