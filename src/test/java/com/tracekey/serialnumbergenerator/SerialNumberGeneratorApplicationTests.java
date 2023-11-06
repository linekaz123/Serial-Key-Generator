package com.tracekey.serialnumbergenerator;

import com.tracekey.serialnumbergenerator.conf.TestDatabaseConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestDatabaseConfig.class)
@ActiveProfiles("test")
class SerialNumberGeneratorApplicationTests {

	@Test
	void contextLoads() {
	}

}
