package io.taxventures;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "jwt-secret = some-jwt-secret"} )
class TaxventuresApplicationTests {

	@Test
	void contextLoads() {
	}

}
