package org.springframework.samples.petclinic.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VisitApiTest extends BaseApiTest {

	private final String entity = "visits";

	@Test
	@DisplayName("Get visits list with GET /owners/{ownerId}/pets/{petId}/visits")
	public void shouldGetVisitsByOwnerIdAndPetId() throws SQLException {

		// given
		final String lastName = "Ike";
		final String petName = "Mysta";

		Long ownerId = db.insert("owners", "last_name", lastName);

		final String addPetSql = String.format(
				"INSERT INTO pets(name, owner_id, birth_date) VALUES('%s', '%s', '2022-03-25')", petName, ownerId);
		db.sqlRequestUpdate(addPetSql);

		final String petIdSql = String.format("SELECT * from pets where name = '%s' and owner_id = '%s'", petName,
				ownerId);
		Long petId = db.sqlRequest(petIdSql).getLong("id");

		final String addVisitSql1 = String.format(
				"INSERT INTO visits(pet_id, description, visit_date) VALUES('%s', '%s', '2022-03-22')", petId, "poop");
		final String addVisitSql2 = String.format(
				"INSERT INTO visits(pet_id, description, visit_date) VALUES('%s', '%s', '2022-03-23')", petId, "pee");
		db.sqlRequestUpdate(addVisitSql1);
		db.sqlRequestUpdate(addVisitSql2);

		Long visitId1 = db
				.sqlRequest(
						String.format("SELECT * from visits where pet_id = '%s' and description = '%s'", petId, "poop"))
				.getLong("id");
		Long visitId2 = db
				.sqlRequest(
						String.format("SELECT * from visits where pet_id = '%s' and description = '%s'", petId, "pee"))
				.getLong("id");

		int visitCount = db.sqlRequest(String.format("SELECT count(*) from visits where pet_id = '%s'", petId))
				.getInt("count");

		// when
		Response response = given().pathParam("ownerId", ownerId).pathParam("petId", petId).when()
				.get("/owners/{ownerId}/pets/{petId}/visits").then().assertThat().body("size()", is(visitCount))
				.extract().response();

		// then
		assertEquals(200, response.statusCode());
		assertEquals(visitId1, response.jsonPath().getLong("id[0]"));
		assertEquals(visitId2, response.jsonPath().getLong("id[1]"));

		// erase test data
		db.delete(entity, visitId1);
		db.delete(entity, visitId2);
		db.delete("pets", petId);
		db.delete("owners", ownerId);
	}

	@Test
	@DisplayName("Create visit with POST /owners/{ownerId}/pets/{petId}/visits")
	public void shouldCreateVisit() throws SQLException, IOException {

		// given
		final String lastName = "Lola";
		final String petName = "Roy";

		Long ownerId = db.insert("owners", "last_name", lastName);

		final String petSql = String.format(
				"INSERT INTO pets(name, owner_id, birth_date) VALUES('%s', '%s', '2022-03-25')", petName, ownerId);
		db.sqlRequestUpdate(petSql);

		final String petIdSql = String.format("SELECT * from pets where name = '%s' and owner_id = '%s'", petName,
				ownerId);
		Long petId = db.sqlRequest(petIdSql).getLong("id");

		// when
		Response response = given().header("content-type", "application/json")
				.body(generateStringFromResource("src/test/resources/body/visit/visitBody.json"))
				.pathParam("ownerId", ownerId).pathParam("petId", petId).when()
				.post("/owners/{ownerId}/pets/{petId}/visits").then().extract().response();

		Long visitId = response.jsonPath().getLong("pets.visits.id[0][0]");

		// then
		assertEquals(201, response.statusCode());
		assertNotNull(visitId);

		// erase test data
		db.delete(entity, visitId);
		db.delete("pets", petId);
		db.delete("owners", ownerId);
	}

}
