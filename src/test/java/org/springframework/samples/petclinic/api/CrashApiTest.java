package org.springframework.samples.petclinic.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;

public class CrashApiTest extends BaseApiTest {

	@Test
	@DisplayName("Get 500 error with GET /oups")
	public void shouldGetError() {
		when()
			.get("/oups")
			.then()
			.statusCode(500);
	}
}
