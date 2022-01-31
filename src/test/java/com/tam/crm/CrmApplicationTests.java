package com.tam.crm;

import com.tam.crm.controllers.UtilController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfiguration.class)
class CrmApplicationTests {
	@Autowired
	private UtilController controller;
	@Test
	void contextLoads() {
		Assertions.assertNotNull(controller);
	}

}
