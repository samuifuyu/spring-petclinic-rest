package org.springframework.samples.petclinic.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VetApiTest extends BaseApiTest {

	private final String entity = "vets";

	@Test
	@DisplayName("Get vets list with GET /vets")
	public void shouldGetVetsListByOwnerId() throws SQLException {
		String sqlRequest = String.format("SELECT count(*) from %s", entity);
		int expectedCount = db.sqlRequest(sqlRequest).getInt("count");

		Response response = when()
			.get("/vets")
			.then()
			.assertThat().body("vetList.size()", is(expectedCount))
			.extract().response();

		assertEquals(200, response.statusCode());
	}
}
