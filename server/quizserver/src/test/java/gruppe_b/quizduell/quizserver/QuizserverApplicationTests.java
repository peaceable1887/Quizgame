package gruppe_b.quizduell.quizserver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import gruppe_b.quizduell.application.categories.CategoryRepository;

@SpringBootTest
@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
class QuizserverApplicationTests {

	@Autowired
	SeedDb seedDb;

	@Autowired
	CategoryRepository categoryRepo;

	static {
		System.setProperty("DB_PORT", "3306");
		System.setProperty("DB_HOSTNAME", "localhost");
		System.setProperty("DB_USERNAME", "root");
		System.setProperty("DB_PASSWORD", "root");
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testSeed() {
		seedDb.Seed();
		assertTrue(categoryRepo.findAll().size() > 0);
	}

}
