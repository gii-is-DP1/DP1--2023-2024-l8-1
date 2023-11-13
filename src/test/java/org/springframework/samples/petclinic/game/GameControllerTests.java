package org.springframework.samples.petclinic.game;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.context.junit4.SpringRunner;

@WebMvcTest(value = {
        GameRestController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
        classes = WebSecurityConfigurer.class))
public class GameControllerTests {
    
    private static final int TEST_PLAYER_ID = 1;
	private static final String BASE_URL = "/api/v1/game";



}
