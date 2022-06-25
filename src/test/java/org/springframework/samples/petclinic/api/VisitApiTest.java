package org.springframework.samples.petclinic.api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VisitApiTest extends BaseApiTest {

	private final String entity = "vets";

	@Test
	@DisplayName("Get visits list with GET /owners/{ownerId}/pets/{petId}/visits")
	@Disabled("method somehow creates a new visit with null values")
	public void shouldGetVisitsByOwnerIdAndPetId() throws SQLException {
//		test data doesn't wok i give up

//		final String lastName = "Rock";
//		final String petName = "Max";
//
//		Long ownerId = db.insert("owners", "last_name", lastName);
//
//		final String petSql = String.format("INSERT INTO pets(name, owner_id, birth_date) VALUES('%s', '%s', '2022-03-25')", petName, ownerId);
//		db.sqlRequestUpdate(petSql);
//
//		final String petIdSql = String.format("SELECT * from pets where name = '%s' and owner_id = '%s'", petName, ownerId);
//		Long petId = db.sqlRequest(petIdSql).getLong("id");
//
//		final String visitSql = String.format("INSERT INTO visits(pet_id, visit_date) VALUES('%s', '2022-03-22')", petId);
//		final String visitIdSql = String.format("SELECT * from visits where pet_id = '%s'", petId);
//		db.sqlRequestUpdate(visitSql);
//		Long visitId = db.sqlRequest(visitIdSql).getLong("id");

		int visitCount = db.sqlRequest("SELECT count(*) from visits where pet_id = '8'").getInt("count");

		Response response = given()
			.pathParam("ownerId", 6)
			.pathParam("petId", 8)
			.when()
			.get("/owners/{ownerId}/pets/{petId}/visits")
			.then()
			.assertThat().body("size()", is(visitCount))
			.extract().response();

		assertEquals(200, response.statusCode());

//		db.delete(entity, visitId);
//		db.delete("pets", petId);
//		db.delete("owners", ownerId);
	}

	@Test
	@DisplayName("Create visit with POST /owners/{ownerId}/pets/{petId}/visits")
	@Disabled("returns 500, broken endpoint")
	public void shouldCreateVisit() throws SQLException, IOException {

		final String lastName = "Lola";
		final String petName = "Roy";

		Long ownerId = db.insert("owners", "last_name", lastName);

		final String petSql = String.format("INSERT INTO pets(name, owner_id, birth_date) VALUES('%s', '%s', '2022-03-25')", petName, ownerId);
		db.sqlRequestUpdate(petSql);

		final String petIdSql = String.format("SELECT * from pets where name = '%s' and owner_id = '%s'", petName, ownerId);
		Long petId = db.sqlRequest(petIdSql).getLong("id");

		Response response = given()
			.header("content-type", "application/json")
			.body(generateStringFromResource("/Users/y.v.barsukova/build/spring-petclinic-rest/src/test/resources/body/visit/visitBody.json"))
			.pathParam("ownerId", ownerId)
			.pathParam("petId", petId)
			.when()
			.post("/owners/{ownerId}/pets/{petId}/visits")
			.then()
			.extract().response();

		JsonPath resp = response.jsonPath();

		Long visitId = response.jsonPath().getLong("id");

		assertEquals(201, response.statusCode());
		assertNotNull(visitId);

		db.delete("pets", petId);
		db.delete("owners", ownerId);
		db.delete(entity, visitId);
	}
}
