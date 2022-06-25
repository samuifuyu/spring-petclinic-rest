package org.springframework.samples.petclinic.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PetApiTest extends BaseApiTest {

	private final String entity = "pets";

	@Test
	@DisplayName("Get pets by owner id with GET /owners/{ownerId}/pets")
	public void shouldGetPetInfoByOwnerId() throws SQLException {
		final String lastName = "Sloan";
		final String petName = "Fish";

		Long ownerId = db.insert("owners", "last_name", lastName);

		final String petSql = String.format("INSERT INTO pets(name, owner_id, birth_date) VALUES('%s', '%s', '2022-01-25')", petName, ownerId);
		db.sqlRequestUpdate(petSql);

		final String petIdSql = String.format("SELECT * from pets where name = '%s' and owner_id = '%s'", petName, ownerId);
		Long expectedPetId = db.sqlRequest(petIdSql).getLong("id");

		Response response = given()
			.pathParam("ownerId", ownerId)
			.when()
			.get("/owners/{ownerId}/pets")
			.then()
			.extract().response();

		Long petId = response.jsonPath().getLong("id[0]");

		assertEquals(200, response.statusCode());
		assertEquals(expectedPetId, petId);

		db.delete("pets", expectedPetId);
		db.delete("owners", ownerId);
	}

	@Test
	@DisplayName("Get pet by owner id and pet id with GET /owners/{ownerId}/pets/{petId}")
	public void shouldGetPetInfoByOwnerIdAndPetId() throws SQLException {
		final String lastName = "Sloan";
		final String petName = "Fish";

		Long ownerId = db.insert("owners", "last_name", lastName);

		final String petSql = String.format("INSERT INTO pets(name, owner_id, birth_date) VALUES('%s', '%s', '2022-01-25')", petName, ownerId);
		db.sqlRequestUpdate(petSql);

		final String petIdSql = String.format("SELECT * from pets where name = '%s' and owner_id = '%s'", petName, ownerId);
		Long expectedPetId = db.sqlRequest(petIdSql).getLong("id");

		String countSqlRequest = String.format("SELECT count(*) from %s where owner_id = %s and id = %s", entity, ownerId, expectedPetId);

		Response response = given()
			.pathParam("ownerId", ownerId)
			.when()
			.get("/owners/{ownerId}/pets")
			.then()
			.extract().response();

		Long expectedNumberOfPets = db.sqlRequest(countSqlRequest).getLong("count");
		Long actualPetId = response.jsonPath().getLong("id[0]");

		assertEquals(200, response.statusCode());
		assertEquals(expectedPetId, actualPetId);
		assertEquals(expectedNumberOfPets, 1);

		db.delete("pets", expectedPetId);
		db.delete("owners", ownerId);
	}

	@Test
	@DisplayName("Create pets by owner id with POST /owners/{ownerId}/pets")
	public void shouldCreateOwnerInfoByLastName() throws SQLException, IOException {
		final String lastName = "Rock";
		Long ownerId = db.insert("owners", "last_name", lastName);
		String sqlRequest = String.format("SELECT * from %s where owner_id = %s", entity, ownerId);

		Response response = given()
			.header("content-type", "application/json")
			.body(generateStringFromResource("/Users/y.v.barsukova/build/spring-petclinic-rest/src/test/resources/body/pet/petBody.json"))
			.pathParam("ownerId", ownerId)
			.when()
			.post("/owners/{ownerId}/pets")
			.then()
			.extract().response();

		Long petId = response.jsonPath().getLong("id");
		Long expectedPetId = db.sqlRequest(sqlRequest).getLong("id");

		assertEquals(201, response.statusCode());
		assertEquals(expectedPetId, petId);

		db.delete(entity, petId);
		db.delete("owners", ownerId);
	}
}
