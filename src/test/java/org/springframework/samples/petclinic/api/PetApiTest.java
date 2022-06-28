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

		// given
		final String lastName = "Sloan";
		final String petName = "Fish";

		Long ownerId = db.insert("owners", "last_name", lastName);

		final String petSql = String.format(
				"INSERT INTO pets(name, owner_id, birth_date) VALUES('%s', '%s', '2022-01-25')", petName, ownerId);
		db.sqlRequestUpdate(petSql);

		final String petIdSql = String.format("SELECT * from pets where name = '%s' and owner_id = '%s'", petName,
				ownerId);
		Long expectedPetId = db.sqlRequest(petIdSql).getLong("id");

		// when
		Response response = given().pathParam("ownerId", ownerId).when().get("/owners/{ownerId}/pets").then().extract()
				.response();

		Long petId = response.jsonPath().getLong("id[0]");
		int petCount = response.jsonPath().getList("$").size();

		// then
		assertEquals(200, response.statusCode());
		assertEquals(expectedPetId, petId);
		assertEquals(1, petCount);

		// erase test data
		db.delete("pets", expectedPetId);
		db.delete("owners", ownerId);
	}

	@Test
	@DisplayName("Get create pet for ownerId with POST /owners/{ownerId}/pets")
	public void shouldGetPetInfoByOwnerIdAndPetId() throws SQLException, IOException {

		// given
		final String lastName = "Sloan";
		final String petName = "Fish";

		Long ownerId = db.insert("owners", "last_name", lastName);

		// when
		Response response = given().header("content-type", "application/json").pathParam("ownerId", ownerId)
				.body(String.format(generateStringFromResource("src/test/resources/body/pet/petBody.json"), petName))
				.when().post("/owners/{ownerId}/pets").then().extract().response();

		final String petIdSql = String.format("SELECT * from pets where owner_id = '%s'", ownerId);
		Long expectedPetId = db.sqlRequest(petIdSql).getLong("id");
		Long actualPetId = response.jsonPath().getLong("id");

		// then
		assertEquals(201, response.statusCode());
		assertEquals(expectedPetId, actualPetId);

		// erase test data
		db.delete("pets", expectedPetId);
		db.delete("owners", ownerId);
	}

	@Test
	@DisplayName("Update pet with POST /owners/{ownerId}/pets/{petId}")
	public void shouldUpdatePet() throws SQLException, IOException {

		final String lastName = "Potter";
		final String petName = "Fish";
		final String newPetName = "Cat";

		Long ownerId = db.insert("owners", "last_name", lastName);

		final String petSql = String.format(
				"INSERT INTO pets(name, owner_id, birth_date) VALUES('%s', '%s', '2022-01-25')", petName, ownerId);
		db.sqlRequestUpdate(petSql);

		final String petIdSql = String.format("SELECT * from pets where name = '%s' and owner_id = '%s'", petName,
				ownerId);
		Long petId = db.sqlRequest(petIdSql).getLong("id");

		Response response = given().header("content-type", "application/json")
				.body(String.format(generateStringFromResource("src/test/resources/body/pet/petBody.json"), newPetName))
				.pathParam("ownerId", ownerId).pathParam("petId", petId).when().post("/owners/{ownerId}/pets/{petId}")
				.then().extract().response();

		assertEquals(201, response.statusCode());
		assertEquals(db.sqlRequest(String.format("SELECT name from %s where id = %s", entity, petId)).getString("name"),
				newPetName);

		db.delete(entity, petId);
		db.delete("owners", ownerId);
	}

}
